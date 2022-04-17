// db-items.go implements RestAPI
package main

import (
	"fmt"
	"log"
	"net/http"
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
)

// GetJSON will return an Item
func (*Items) GetJSON(c *gin.Context) {
	var data Items

	if err := api.GetJSON(c, &data, "id"); err != nil {
		log.Println(err)
		return
	}
}

// GetAllJSON will return all Items
func (*Items) GetAllJSON(c *gin.Context) {
	var data []Items

	if err := api.GetAllJSON(c, &data); err != nil {
		log.Println(err)
		return
	}
}

// UGetAllJSON will return all Items related to logged in user
func (*Items) UGetAllJSON(c *gin.Context) {
	var data []Items

	user := c.MustGet("user").(User)

	if err := api.GetAllJSON(c, &data, "OwnerID=", user.Sub); err != nil {
		log.Println(err)
		return
	}
}

// Add will add item to database
// Needs slimmed down, not sure how. Possibly js.
func (*Items) Add(c *gin.Context) {
	var data Items
	user := c.MustGet("user").(User)

	if err := api.AddJSON(c, &data, user); err != nil {
		log.Println(err)
		return
	}
}

// Remove will remove specified item with correct ID from database
func (*Items) Remove(c *gin.Context) {
	var item Items
	user := c.MustGet("user").(User)

	if err := api.Remove(c, &item, user); err != nil {
		log.Println(err)
		return
	}
}

// Change will replace given data with the original item in the database
func (*Items) Change(c *gin.Context) {
	var data Items
	user := c.MustGet("user").(User)

	if err := api.ChangeJSON(c, &data, user); err != nil {
		log.Println(err)
		return
	}
}

// GetItemForm Parses form data into a readable JSON object
func (*Items) GetItemForm(c *gin.Context) (i Items, err error) {
	//ENTER DATE AS "YYYY-MM-DD"
	//These convert the format above to RFC3339 format
	//to work with the database correctly
	dBorrow, err := time.Parse(time.RFC3339, c.PostForm("DateBorrowed")+"T15:00:00.05Z")
	if err != nil {
		log.Println(err)
	}

	dDue, err := time.Parse(time.RFC3339, c.PostForm("DateDue")+"T15:00:00.05Z")
	if err != nil {
		log.Println(err)
	}

	//Will get bool from 1 or 0.
	available, err := strconv.ParseBool(c.PostForm("Available"))
	if err != nil {
		log.Println(err)
	}

	i = Items{
		ItemName:     c.PostForm("ItemName"),
		OwnerID:      c.PostForm("OwnerID"),
		ItemsHouseID: c.PostForm("ItemsHouseID"),
		ItemID:       c.PostForm("ItemID"),
		BorrowerID:   c.PostForm("BorrowerID"),
		DateBorrowed: dBorrow.Unix(),
		DateDue:      dDue.Unix(),
		Available:    available,
	}
	err = nil

	if i.ItemName == "" || i.ItemsHouseID == "" {
		c.JSON(http.StatusBadRequest, gin.H{
			"status":  http.StatusBadRequest,
			"message": "Form data incorrect - db-items - Add",
		})
		err = fmt.Errorf("FormDataIncorrect")
		return
	}

	return

}
