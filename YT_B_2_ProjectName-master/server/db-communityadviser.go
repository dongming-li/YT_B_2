// db-communityadviser.go implements RestAPI
package main

import (
	"log"

	"github.com/gin-gonic/gin"
)

// GetJSON will return a CommunityAdviser
func (*CommunityAdviser) GetJSON(c *gin.Context) {
	var data CommunityAdviser

	if err := api.GetJSON(c, &data, "id"); err != nil {
		return
	}
}

// GetAllJSON will return all CommunityAdvisors
func (*CommunityAdviser) GetAllJSON(c *gin.Context) {
	var data []CommunityAdviser

	if err := api.GetAllJSON(c, &data); err != nil {
		return
	}
}

// Add ...
func (*CommunityAdviser) Add(c *gin.Context) {
	if err := FailIfNotRequiredAuthGroup(c, AuthADMIN); err != nil {
		log.Printf("Error: %s\n", err.Error())
		return
	}
	api.NotImplimented(c)
}

// Remove ...
func (*CommunityAdviser) Remove(c *gin.Context) {
	if err := FailIfNotRequiredAuthGroup(c, AuthADMIN); err != nil {
		log.Printf("Error: %s\n", err.Error())
		return
	}
	api.NotImplimented(c)
}

// Change ...
func (*CommunityAdviser) Change(c *gin.Context) {
	if err := FailIfNotRequiredAuthGroup(c, AuthADMIN); err != nil {
		log.Printf("Error: %s\n", err.Error())
		return
	}
	api.NotImplimented(c)
}
