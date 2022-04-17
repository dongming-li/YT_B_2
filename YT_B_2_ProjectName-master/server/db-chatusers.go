// db-chatusers.go implements RestAPI
package main

import (
	"github.com/gin-gonic/gin"
)

// GetJSON will return a user by id
func (*ChatUsers) GetJSON(c *gin.Context) {
	var data ChatUsers
	if err := api.GetJSON(c, &data, "id"); err != nil {
		return
	}
}

// GetAllJSON will return all ChatUsers
func (*ChatUsers) GetAllJSON(c *gin.Context) {
	var data []ChatUsers

	if err := api.GetAllJSON(c, &data); err != nil {
		return
	}
}

func (*ChatUsers) Add(c *gin.Context)    { api.NotImplimented(c) }
func (*ChatUsers) Remove(c *gin.Context) { api.NotImplimented(c) }
func (*ChatUsers) Change(c *gin.Context) { api.NotImplimented(c) }
