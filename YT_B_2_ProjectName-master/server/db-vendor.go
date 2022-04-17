// db-vendor.go implements RestAPI
package main

import (
	"log"

	"github.com/gin-gonic/gin"
)

// GetJSON will return a Vendor
func (*Vendor) GetJSON(c *gin.Context) {
	var data Vendor

	if err := api.GetJSON(c, &data, "id"); err != nil {
		return
	}
}

// GetAllJSON will return all Vendors
func (*Vendor) GetAllJSON(c *gin.Context) {
	var data []Vendor

	if err := api.GetAllJSON(c, &data); err != nil {
		return
	}
}

// Add ...
func (*Vendor) Add(c *gin.Context) {
	if err := FailIfNotRequiredAuthGroup(c, AuthADMIN); err != nil {
		log.Printf("Error: %s\n", err.Error())
		return
	}
	api.NotImplimented(c)
}

// Remove ...
func (*Vendor) Remove(c *gin.Context) {
	if err := FailIfNotRequiredAuthGroup(c, AuthADMIN); err != nil {
		log.Printf("Error: %s\n", err.Error())
		return
	}
	api.NotImplimented(c)
}

// Change ...
func (*Vendor) Change(c *gin.Context) {
	if err := FailIfNotRequiredAuthGroup(c, AuthADMIN); err != nil {
		log.Printf("Error: %s\n", err.Error())
		return
	}
	api.NotImplimented(c)
}
