// user.go handles functions performed on authenticated user
package main

import (
	"fmt"
	"log"
	"net/http"
	"time"

	"github.com/gin-gonic/gin"

	"github.com/jinzhu/gorm"
)

const (
	// AuthGUEST is Guest AuthGroup
	AuthGUEST = iota //0

	// AuthSTUDENT is Student AuthGroup
	AuthSTUDENT //1

	// AuthCA is CA AuthGroup
	AuthCA //2

	// AuthADMIN is Admin AuthGroup
	AuthADMIN //3
)

// User type to handle user infromation from Google OAuth
type User struct {
	Sub           string    `gorm:"primary_key" json:"sub"`
	CreatedAt     time.Time `json:"-"`
	Name          string    `json:"name"`
	GivenName     string    `json:"given_name"`
	FamilyName    string    `json:"family_name"`
	Profile       string    `json:"profile"`
	Picture       string    `json:"picture"`
	Email         string    `json:"email"`
	EmailVerified bool      `json:"email_verified"`
	Gender        string    `json:"gender"`
	Hd            string    `json:"hd"`
	AuthGroup     int       `json:"AuthGroup"`
}

// TableName for user table
func (*User) TableName() string {
	return "User"
}

// Setup user with the person database
func (u *User) Setup(db *gorm.DB) (person Person, err error) {

	tx := db.Begin()
	if err := tx.Where(Person{ID: u.Sub}).Attrs(
		Person{
			FirstName:  u.GivenName,
			LastName:   u.FamilyName,
			PictureURL: u.Picture,
			ID:         u.Sub,
			Email:      u.Email,
		},
	).FirstOrCreate(&person).Error; err != nil {
		tx.Rollback()
		return Person{}, err
	}

	tx.Commit()
	return
}

// Delete user from the database
func (u *User) Delete(db *gorm.DB) error {
	tx := db.Begin()
	if err := tx.Where("ID = ?", u.Sub).Delete(&Person{}).Error; err != nil {
		tx.Rollback()
		return err
	}

	tx.Commit()
	return nil
}

// GetDorm method to get dorm for a user
func (u *User) GetDorm() string {
	// TODO
	return ""
}

// FailIfNotRequiredAuthGroup will check if user has required auth
// Will abort with JSON and error
//
// Example usage:
// if err := FailIfNotRequiredAuthGroup(c, AuthADMIN); err != nil {
// 	log.Printf("Error: %s\n", err.Error())
// 	return
// }
func FailIfNotRequiredAuthGroup(c *gin.Context, group int) error {
	user := c.MustGet("user").(User)

	if group > user.AuthGroup {
		c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{
			"status":  http.StatusUnauthorized,
			"message": "AuthGroup level does not satisfy requirements.",
		})

		return fmt.Errorf(
			"User has AuthGroup '%d', which is less than required '%d'",
			user.AuthGroup, group)
	}

	log.Printf("User (%s) authorized with AuthGroup %d.\n", user.Name, user.AuthGroup)
	return nil
}
