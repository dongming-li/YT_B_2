// auth.go handles authentication
// Implementation heavily inspired by: https://github.com/yunv/random-game/blob/master/server-go/auth.go
package main

import (
	"crypto/rand"
	"encoding/base64"
	"encoding/json"
	"io/ioutil"
	"log"
	"net/http"

	"github.com/gin-gonic/contrib/sessions"
	"github.com/gin-gonic/gin"
	"github.com/golang/glog"
	"github.com/zalando/gin-oauth2/google"
	"golang.org/x/oauth2"
	g "golang.org/x/oauth2/google"
)

const (
	cookieDomain string = "proj-309-yt-b-2.cs.iastate.edu"
	endpoint     string = "http://proj-309-yt-b-2.cs.iastate.edu:8080"
	redirectURL  string = endpoint + "/auth"
	credFile     string = "creds.json"
)

// google auth scopes
var scopes = []string{
	"https://www.googleapis.com/auth/userinfo.email",
	"https://www.googleapis.com/auth/userinfo.profile", // https://developers.google.com/identity/protocols/googlescopes#google_sign-in
}

// Credentials defines cred file structure
type Credentials struct {
	ClientID     string `json:"clientid"`
	ClientSecret string `json:"secret"`
}

var auth *Auth

// Auth class
type Auth struct{}

// setup api
func (*Auth) setup(r *gin.Engine) {
	// Attempting to create authenticated sections in the api
	////////////////////////////////////////////////////////////////////////////
	// READ: https://github.com/zalando/gin-oauth2
	// https://gowalker.org/github.com/zalando/gin-oauth2/google
	// https://github.com/carljmosca/zmm/blob/ad0279506be8529fcb61b6a143ee67e98e02a643/zmm-server/zmm-server.go
	// https://github.com/yunv/random-game
	secret := []byte("LvWRjfqVWtncPvHctwXVdftPggCXxPHtfHm4LhW9nJHzRLKVxRwPnJ3HxrhbFzq4")
	sessionName := "cyswapper"

	// setup user db
	var user User
	db.AutoMigrate(&user)

	// DEBUG!!
	r.Use(gin.Logger())

	// setup
	google.Setup(redirectURL, credFile, scopes, secret)
	r.Use(google.Session(sessionName))
	r.Use(auth.setOptions())
	r.GET("/login", auth.loginHandler)
	r.GET("/logout", auth.logoutHandler)

	// oauth is pain
	oauth := r.Group("/auth")
	oauth.Use(google.Auth())
	oauth.GET("/", auth.auth)

	// show web loggedin details
	r.GET("/loggedin", func(c *gin.Context) {
		session := sessions.Default(c)

		if userID := session.Get("user_id"); userID != nil {
			var prsn Person

			db.Where("ID = ?", userID).First(&prsn)

			c.HTML(http.StatusOK, "profile", gin.H{
				"User": prsn,
			})

		} else {
			c.HTML(http.StatusOK, "profile", gin.H{
				"User": Person{
					ID:         "",
					FirstName:  "",
					LastName:   "",
					PictureURL: "",
					Email:      "",
					AuthGroup:  -1,
				},
			})

			// c.JSON(http.StatusOK, gin.H{
			// 	"loggedin": false,
			// 	"message":  "this only works with cookie auth, for jwt: please see /api/loggedin",
			// })
		}
	})
}

// GetLoginCodeJWT will give the client a JWT token for a login code
func (*Auth) GetLoginCodeJWT(ctx *gin.Context) {
	var cred Credentials
	file, err := ioutil.ReadFile(credFile)
	if err != nil {
		glog.Fatalf("Cred file error: %v\n", err)
		ctx.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{
			"status":  http.StatusInternalServerError,
			"message": "Internal server error.",
		})
	}
	json.Unmarshal(file, &cred)
	var conf = oauth2.Config{
		ClientID:     cred.ClientID,
		ClientSecret: cred.ClientSecret,
		RedirectURL:  redirectURL,
		Scopes:       scopes,
		Endpoint:     g.Endpoint,
	}

	code := ctx.PostForm("authCode")
	if code == "" {
		ctx.AbortWithStatusJSON(http.StatusBadRequest, gin.H{
			"status":  http.StatusBadRequest,
			"message": "Missing authCode.",
		})
		return
	}

	tok, err := conf.Exchange(oauth2.NoContext, code)
	if err != nil {
		log.Println(err.Error())
		ctx.AbortWithStatusJSON(http.StatusBadRequest, gin.H{
			"status":  http.StatusBadRequest,
			"message": "Invalid authCode.",
		})
		return
	}

	client := conf.Client(oauth2.NoContext, tok)
	email, err := client.Get("https://www.googleapis.com/oauth2/v3/userinfo")
	if err != nil {
		log.Println(err.Error())
		ctx.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{
			"status":  http.StatusInternalServerError,
			"message": "Could not get user info from Google.",
		})
		return
	}
	defer email.Body.Close()
	data, err := ioutil.ReadAll(email.Body)
	if err != nil {
		glog.Errorf("[Gin-OAuth] Could not read Body: %s", err)
		ctx.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{
			"status":  http.StatusInternalServerError,
			"message": "Error with reading Google user data.",
		})
		return
	}

	var user User
	err = json.Unmarshal(data, &user)
	if err != nil {
		glog.Errorf("[Gin-OAuth] Unmarshal userinfo failed: %s", err)
		ctx.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{
			"status":  http.StatusInternalServerError,
			"message": "Could not parse userinfo.",
		})
		return
	}

	ctx.Set("user", user)

	jwtauth.GetToken(ctx)
}

// gin middleware handler for session
func (*Auth) setOptions() gin.HandlerFunc {
	return func(c *gin.Context) {
		session := sessions.Default(c)
		session.Options(
			sessions.Options{
				Path:   "/",
				Domain: cookieDomain,
				MaxAge: 86400, // 1 day
			})
		c.Next()
	}
}

// randToken will generate a session state token
func (*Auth) randToken() string {
	b := make([]byte, 32)
	rand.Read(b)
	return base64.StdEncoding.EncodeToString(b)
}

// loginHandler will handle the initial oauth connection (web)
func (a *Auth) loginHandler(c *gin.Context) {
	state := a.randToken()
	session := sessions.Default(c)
	session.Set("state", state)
	session.Save()

	c.Redirect(http.StatusTemporaryRedirect, google.GetLoginURL(state))
}

// logoutHandler will logout
func (a *Auth) logoutHandler(c *gin.Context) {
	session := sessions.Default(c)
	session.Options(
		sessions.Options{
			Path:   "/",
			Domain: cookieDomain,
			MaxAge: -1,
		})

	if err := session.Save(); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"status":  http.StatusInternalServerError,
			"message": "An unknown error occured while saving session.",
		})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"status":  http.StatusOK,
		"message": "Logged out",
	})
}

// auth will handle session authentication
func (*Auth) auth(c *gin.Context) {
	info := c.MustGet("user").(google.User)
	user := User{}

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

	session := sessions.Default(c)
	session.Set("user_id", user.Sub)
	session.Save()

	c.Redirect(http.StatusTemporaryRedirect, "/loggedin")
}

// check middleware will verify logged in user sessions
// use this to secure the api
func (*Auth) check() gin.HandlerFunc {
	return func(c *gin.Context) {
		session := sessions.Default(c)
		userID := session.Get("user_id")

		if userID == nil {
			log.Printf("Invalid session %s", userID)
			unauthorized(c)
			return
		}

		// verify user
		var user User
		if db.First(&user, userID).Error == nil {
			log.Println("[auth check]: setting user to context...")
			c.Set("user", user)
		} else {
			log.Printf("Invalid user %s", userID)
			unauthorized(c)
			return
		}

		// set person
		if person, err := user.Setup(db); err == nil {
			c.Set("person", person)
		} else {
			log.Printf("[Auth auth] User.Setup error: %s\n", err.Error())
			// unauthorized(c)
			// return
		}

		c.Next()
	}
}

// unauthorized will abort the connection with a status 401
func unauthorized(c *gin.Context) {
	c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{
		"status":  http.StatusUnauthorized,
		"message": "unauthorized",
	})
}
