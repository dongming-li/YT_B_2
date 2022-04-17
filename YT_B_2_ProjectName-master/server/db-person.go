// db-person.go implements RestAPI
package main

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

// GetJSON will return a Person
func (*Person) GetJSON(c *gin.Context) {
	var data Person

	if err := api.GetJSON(c, &data, "id"); err != nil {
		return
	}
}

// GetAllJSON will return all Person
func (*Person) GetAllJSON(c *gin.Context) {
	var data []Person

	if err := api.GetAllJSON(c, &data); err != nil {
		return
	}
}

// Add will add a Person to the database
func (*Person) Add(c *gin.Context) {
	var person Person

	c.JSON(http.StatusOK, gin.H{"response": c.PostForm("person.Add")})
	if person.Email == "" || person.FirstName == "" || person.ID == "" || person.LastName == "" || person.PictureURL == "" {
		c.JSON(http.StatusNotAcceptable, gin.H{"message": "Form data incorrect"})
		return
	}

	db.Create(&person)
	c.JSON(http.StatusOK, gin.H{"status": http.StatusOK, "message": "Person creation successful."})
}

// Remove will delete the specified person from the database
// (Probably will only be used for testing)
func (*Person) Remove(c *gin.Context) {
	var person Person

	id := c.Param("id")

	db.Where("ID = ?", id).First(&person)

	if person.ID != id {
		c.JSON(http.StatusNotFound, gin.H{"status": http.StatusNotFound, "message": "No person found."})
		return
	} else if id == "" {
		c.JSON(http.StatusNotAcceptable, gin.H{"status": http.StatusNotFound, "message": "Form data incorrect"})
		return
	}

	db.Where("ID = ?", id).Delete(&person)
}

// Change will update the person's information safely
// (Without destroying data)
func (*Person) Change(c *gin.Context) {
	var person Person

	id := c.Param("id")

	db.Where("ID = ?", id).First(&person)

	if person.ID != id {
		c.JSON(http.StatusNotFound, gin.H{"status": http.StatusNotFound, "message": "No person found."})
		return
	} else if id == "" {
		c.JSON(http.StatusNotAcceptable, gin.H{"status": http.StatusNotFound, "message": "Form data incorrect"})
		return
	}

	//TODO
	c.JSON(http.StatusOK, gin.H{"status": http.StatusOK, "message": "Item change successful."})
}
