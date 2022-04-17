// db-student.go implements RestAPI
package main

import (
	"log"

	"github.com/gin-gonic/gin"
)

// GetJSON will return a Student
func (*Student) GetJSON(c *gin.Context) {
	var data Student

	if err := api.GetJSON(c, &data, "id"); err != nil {
		return
	}
}

// GetAllJSON will return all students
func (*Student) GetAllJSON(c *gin.Context) {
	var data []Student

	if err := api.GetAllJSON(c, &data); err != nil {
		return
	}
}

// Add ...
func (*Student) Add(c *gin.Context) {
	if err := FailIfNotRequiredAuthGroup(c, AuthADMIN); err != nil {
		log.Printf("Error: %s\n", err.Error())
		return
	}
	api.NotImplimented(c)
}

// Remove ...
func (*Student) Remove(c *gin.Context) {
	if err := FailIfNotRequiredAuthGroup(c, AuthADMIN); err != nil {
		log.Printf("Error: %s\n", err.Error())
		return
	}
	api.NotImplimented(c)
}

// Change ...
func (*Student) Change(c *gin.Context) {
	if err := FailIfNotRequiredAuthGroup(c, AuthADMIN); err != nil {
		log.Printf("Error: %s\n", err.Error())
		return
	}
	api.NotImplimented(c)
}
