// db-houses.go implements RestAPI
package main

import (
	"log"

	"github.com/gin-gonic/gin"
)

// GetJSON will return a House
func (*Houses) GetJSON(c *gin.Context) {
	var data Houses

	if err := api.GetJSON(c, &data, "id"); err != nil {
		return
	}
}

// GetAllJSON will return all Houses
func (*Houses) GetAllJSON(c *gin.Context) {
	var data []Houses

	if err := api.GetAllJSON(c, &data); err != nil {
		return
	}
}

// Add will allow an admin to add a house
func (*Houses) Add(c *gin.Context) {
	if err := FailIfNotRequiredAuthGroup(c, AuthADMIN); err != nil {
		log.Printf("Error: %s\n", err.Error())
		return
	}
	api.NotImplimented(c)
}

// Remove will allow an admin to remove a house
func (*Houses) Remove(c *gin.Context) {
	if err := FailIfNotRequiredAuthGroup(c, AuthADMIN); err != nil {
		log.Printf("Error: %s\n", err.Error())
		return
	}
	api.NotImplimented(c)
}

// Change will allow an admin to change a house
func (*Houses) Change(c *gin.Context) {
	if err := FailIfNotRequiredAuthGroup(c, AuthADMIN); err != nil {
		log.Printf("Error: %s\n", err.Error())
		return
	}

	api.NotImplimented(c)
}
