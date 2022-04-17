// db-dorms.go implements RestAPI
package main

import (
	"log"

	"github.com/gin-gonic/gin"
)

// GetJSON will return a Dorms
func (*Dorms) GetJSON(c *gin.Context) {
	var data Dorms

	if err := api.GetJSON(c, &data, "id"); err != nil {
		return
	}
}

// GetAllJSON will return all Dorms
func (*Dorms) GetAllJSON(c *gin.Context) {
	var data []Dorms

	if err := api.GetAllJSON(c, &data); err != nil {
		return
	}
}

// Add ...
func (*Dorms) Add(c *gin.Context) {
	if err := FailIfNotRequiredAuthGroup(c, AuthADMIN); err != nil {
		log.Printf("Error: %s\n", err.Error())
		return
	}
	api.NotImplimented(c)
}

// Remove ...
func (*Dorms) Remove(c *gin.Context) {
	if err := FailIfNotRequiredAuthGroup(c, AuthADMIN); err != nil {
		log.Printf("Error: %s\n", err.Error())
		return
	}
	api.NotImplimented(c)
}

// Change ...
func (*Dorms) Change(c *gin.Context) {
	if err := FailIfNotRequiredAuthGroup(c, AuthADMIN); err != nil {
		log.Printf("Error: %s\n", err.Error())
		return
	}
	api.NotImplimented(c)
}
