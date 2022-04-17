// db-chatmessages.go implements RestAPI
package main

import (
	"github.com/gin-gonic/gin"
)

// GetJSON will return ChatMessages for a given id
func (*ChatMessages) GetJSON(c *gin.Context) {
	var data []ChatMessages

	if err := api.GetJSON(c, &data, "id"); err != nil {
		return
	}
}

func (*ChatMessages) GetAllJSON(c *gin.Context) { api.NotImplimented(c) }
func (*ChatMessages) Add(c *gin.Context)        { api.NotImplimented(c) }
func (*ChatMessages) Remove(c *gin.Context)     { api.NotImplimented(c) }
func (*ChatMessages) Change(c *gin.Context)     { api.NotImplimented(c) }
