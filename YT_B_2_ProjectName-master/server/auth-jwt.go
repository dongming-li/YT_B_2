package main

import (
	"errors"
	"fmt"
	"log"
	"net/http"
	"strings"
	"time"

	jwt "github.com/dgrijalva/jwt-go"
	"github.com/gin-gonic/contrib/sessions"
	"github.com/gin-gonic/gin"
)

var (
	jwtauth JWTAuth
	// TODO this should not be hardcoded
	jwtSigningKey = []byte("Ft9VnjhWVcsjKhjbPnWdC7NLdMNFKTtHVhC9wCWnznzwHkzhPwTdRMc4spgMVMkq")
)

// JWTAuth class
type JWTAuth struct{}

// GetToken will return JWT token as JSON
func (*JWTAuth) GetToken(c *gin.Context) {
	info := c.MustGet("user").(User)
	user := User{}

	// create user in db if neccessary
	db.Where(User{Sub: info.Sub}).Attrs(
		User{
			// gorm.Model: gorm.Model{CreatedAt: time.Now()},
			Name:       info.Name,
			GivenName:  info.GivenName,
			FamilyName: info.FamilyName,
			Picture:    info.Picture,
			Email:      info.Email,
			AuthGroup:  AuthSTUDENT,
		},
	).FirstOrCreate(&user)

	if _, err := user.Setup(db); err != nil {
		log.Printf("[Auth auth] User.Setup error: %s\n", err.Error())
	}

	// Set up with person db
	// if d.RecordNotFound() {
	// 	user.Setup(db)
	// }

	// create token
	creationTime := time.Now()
	expireTime := time.Now().Add(time.Hour * 24)
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"sub": user.Sub,
		"nbf": creationTime.Unix(),
		"iat": creationTime.Unix(),
		"exp": expireTime.Unix(),
	})

	// sign the token
	tokenString, err := token.SignedString(jwtSigningKey)
	if err != nil {
		log.Println("[Error auth-jwt]: " + err.Error())
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{
			"status":  http.StatusInternalServerError,
			"message": "JWT token creation error.",
		})
	}

	log.Printf("[auth-jwt] user '%s' (%s) got a token.", user.Name, user.Sub)
	c.JSON(http.StatusOK, gin.H{
		"token":  tokenString,
		"expire": expireTime.Format(time.RFC3339),
	})
}

// Check is middleware that will verify logged in user sessions
// use this to secure the api
// TODO check if token needs to be refreshed
func (*JWTAuth) Check() gin.HandlerFunc {
	return func(c *gin.Context) {
		var sub string

		// this is a closure to get tokenString from the auth header
		// the header format should be Authorization: Bearer <token>
		tokenString, err := func() (string, error) {
			authHeader := c.Request.Header.Get("Authorization")
			if authHeader == "" {
				return "", errors.New("AuthHeaderEmpty")
			}

			// borrowed from https://github.com/appleboy/gin-jwt/blob/v2.3.1/auth_jwt.go#L316
			parts := strings.SplitN(authHeader, " ", 2)
			if !(len(parts) == 2 && parts[0] == "Bearer") {
				return "", errors.New("InvalidAuthHeader")
			}

			return parts[1], nil
		}()

		// BEG HACK HACK HACK HACK HACK HACK HACK HACK HACK HACK
		// allow cookie session auth
		if userID := sessions.Default(c).Get("user_id"); userID != nil {
			log.Println("[auth-jwt Check]: surrendering to auth.check() for cookie auth...")
			auth.check()(c)
			return
		}
		// END HACK HACK HACK HACK HACK HACK HACK HACK HACK HACK

		// check if tokenString parsed with an error
		if err != nil {
			log.Println("[Error auth-jwt header]: " + err.Error())
			c.AbortWithStatusJSON(http.StatusBadRequest, gin.H{
				"status":  http.StatusBadRequest,
				"message": "Invalid header.",
			})
			return
		}

		// parse tokenString to token
		token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
			// validate alg
			if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
				return nil, fmt.Errorf("Unexpected signing method: %v", token.Header["alg"])
			}

			return jwtSigningKey, nil
		})

		if err != nil {
			log.Println("[Error auth-jwt token]: " + err.Error())
			c.AbortWithStatusJSON(http.StatusBadRequest, gin.H{
				"status":  http.StatusBadRequest,
				"message": err.Error(),
			})
			return
		}

		// get claims
		if claims, ok := token.Claims.(jwt.MapClaims); ok && token.Valid {
			// get sub claim
			sub = claims["sub"].(string)
		} else {
			log.Println("[Error auth-jwt claims]: " + err.Error())
			c.AbortWithStatusJSON(http.StatusBadRequest, gin.H{
				"status":  http.StatusBadRequest,
				"message": "Invalid token.",
			})
			return
		}

		// check if sub exists as user
		var user User
		if db.First(&user, sub).Error == nil {
			// set user in gin context
			c.Set("user", user)
			log.Printf("[auth-jwt db]: setting user, %s\n", user.Name)
		} else {
			log.Println("[Error auth-jwt check]: Invalid user sub " + sub)
			c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{
				"status":  http.StatusInternalServerError,
				"message": "Invalid sub in token.",
			})
			return
		}

		c.Next()
	}
}
