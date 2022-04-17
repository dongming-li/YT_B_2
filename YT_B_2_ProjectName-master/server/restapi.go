// restapi.go has common functions for the REST API
package main

import (
	"bytes"
	"crypto/rand"
	"fmt"
	"log"
	"math/big"
	"net/http"
	"reflect"
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/jinzhu/gorm"
)

var (
	api APIHelper
)

const (
	mADD = iota
	mCHANGE
)

// RestAPI defines common functions that will be implemented by other functions
//
// REST API Return Codes
// See: https://gist.github.com/subfuzion/669dfae1d1a27de83e69
type RestAPI interface {
	// AddJSON will add a JSON item to the DB (POST)
	AddJSON(c *gin.Context)

	// ChangeJSON will modify an item from the DB (PATCH)
	ChangeJSON(c *gin.Context)

	// GetAllJSON will return a JSON array of JSON objects from given DB (GET)
	//
	// Optional Query Params:
	//   page  - set current page (starting from 0)
	//   limit - limit amout of items received (default 10, must be used with page param)
	GetJSON(c *gin.Context)

	// GetJSON will returns a JSON object based on given param from a given DB (GET)
	GetAllJSON(c *gin.Context)

	// Remove will remove an item from DB (DELETE)
	Remove(c *gin.Context)
}

// APIHelper holds all of the helper functions for the REST API
type APIHelper struct{}

// mAddChangeJSON is a helper that will either add or change JSON depending on
// the value of the method int
func (a *APIHelper) mAddChangeJSON(method int, c *gin.Context, out interface{}, user User) error {
	// unmarshal the json
	if err := c.BindJSON(&out); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"status":  http.StatusBadRequest,
			"message": "Invalid JSON.",
		})

		log.Printf("[RestAPI.mAddChangeJSON] Error Invalid JSON: %s\n", err.Error())
		return err
	}

	// perform db actions
	tx := db.Begin()

	// check the json for validity
	log.Printf("[RestAPI.mAddChangeJSON] TypeOf out is: %s\n", reflect.TypeOf(out))
	switch v := out.(type) {
	// case *Person:
	// 	v.ID = user.Sub
	// 	break
	// case *Student:
	// 	v.StudentID = user.Sub
	// 	break
	// case *CommunityAdviser:
	// 	v.CommunityAdviserID = user.Sub
	// 	break
	// case *Vendor:
	// 	v.VendorID = user.Sub
	// 	break
	// case *Residents:
	// 	break
	// case *ChatUsers:
	// 	break
	// case *ChatMessages:
	// 	break
	// case *Dorms:
	// 	break
	// case *Houses:
	case *Items:
		log.Printf("[RestAPI.mAddChangeJSON Items] Method=%d, ItemID='%s'\n",
			method, v.ItemID)

		// itemid must be unique for ADD
		if method == mADD && v.ItemID == "" {
			// generate string of len 16 consisting of numbers
			var buf bytes.Buffer
			for i := 0; i < 16; i++ {
				nBig, _ := rand.Int(rand.Reader, big.NewInt(9))
				buf.WriteString(fmt.Sprintf("%d", nBig.Int64()))
			}
			v.ItemID = buf.String()

			log.Printf("RestAPI.mAddChangeJSON Items] Creating new item with ID: '%s'\n",
				v.ItemID)
		} else if method == mADD && v.ItemID != "" {
			c.AbortWithStatusJSON(http.StatusBadRequest, gin.H{
				"status":  http.StatusBadRequest,
				"message": "You cannot set an invalid ItemID for ADD!",
			})
			return fmt.Errorf("Unauthorized Set ItemID Error")
		}

		// internal logic hacking for CHANGE
		if method == mCHANGE {
			if v.Available == true {
				v.BorrowerID = "-1"
			} else {
				v.BorrowerID = user.Sub
			}
		}

		// if ItemID is set for CHANGE, it must equal the id param
		// FIXME broken
		// if method == mCHANGE && (v.ItemID != "" || v.ItemID != c.Param("id")) {
		// 	c.AbortWithStatusJSON(http.StatusBadRequest, gin.H{
		// 		"status":  http.StatusBadRequest,
		// 		"message": "You cannot set an invalid ItemID for CHANGE!",
		// 	})
		// 	return fmt.Errorf("Unauthorized Set ItemID Error")
		// }

		// if user is trying to force their id to something
		if v.OwnerID != "" && v.OwnerID != user.Sub {
			c.AbortWithStatusJSON(http.StatusBadRequest, gin.H{
				"status":  http.StatusBadRequest,
				"message": "You cannot set OwnerID to somebody else!",
			})
			return fmt.Errorf("Unauthorized Set OwnerID Error")
		}
		v.OwnerID = user.Sub

		break

	default:
		log.Printf("[RestAPI.mAddChangeJSON WARN] Unmatched type, %s\n", reflect.TypeOf(out))
	}

	switch method {
	case mADD:
		if err := tx.Create(out).Error; err != nil {
			tx.Rollback()
			c.JSON(http.StatusInternalServerError, gin.H{
				"status":  http.StatusInternalServerError,
				"message": "Not added.",
			})

			log.Printf("[RestAPI mADD]: Add Error: %s\n", err.Error())
			return err
		}
		break

	case mCHANGE:
		id := c.Param("id")
		if err := tx.Model(out).Where(id).Updates(out).Error; err != nil {
			log.Printf("[RestAPI mCHANGE]: Update Error: %s\n", err.Error())
			log.Printf("[RestAPI.mCHANGE] TypeOf out is: %s\n", reflect.TypeOf(out))
			log.Println(out)
			log.Println()
			tx.Rollback()
			c.JSON(http.StatusInternalServerError, gin.H{
				"status":  http.StatusInternalServerError,
				"message": "Not updated.",
			})
			return err
		}
		break

	default:
		return fmt.Errorf("[RestAPI mAddChangeJSON]: incorrect method value")
	}

	tx.Commit()
	c.JSON(http.StatusOK, gin.H{
		"status": http.StatusOK,
		"data":   out,
	})
	return nil
}

// AddJSON is a common helper method to add JSON to the database.
//
// Usage:
//	if err := api.AddJSON(c, &data); err != nil {
//		return
//	}
//
func (a *APIHelper) AddJSON(c *gin.Context, out interface{}, user User) error {
	return a.mAddChangeJSON(mADD, c, out, user)
}

// ChangeJSON will replace given data with the original item in the database
// Usage:
//	if err := api.ChangeJSON(c, &data); err != nil {
//		return
//	}
//
func (a *APIHelper) ChangeJSON(c *gin.Context, out interface{}, user User) (err error) {
	return a.mAddChangeJSON(mCHANGE, c, out, user)
}

// GetJSON is a common helper method to get JSON.
// The checkParam string (used in c.Param) will be checked against the primary key.
//
// Usage:
//	if err := api.GetJSON(c, &data, "id"); err != nil {
//		return
//	}
//
func (a *APIHelper) GetJSON(c *gin.Context, out interface{}, checkParam string) error {
	// primaryKey := db.NewScope(out).PrimaryField().Name

	q := db.First(out, c.Param(checkParam))
	if err := a.CheckDBFound(c, q); err != nil {
		return err
	}

	c.JSON(http.StatusOK, gin.H{
		"status": http.StatusOK,
		"data":   out,
	})
	return nil
}

// GetAllJSON is a common helper method to get all JSON for the API.
//
// Optional Query Params:
//   page  - set current page (starting from 0)
//   limit - limit amout of items received (default 10, must be used with page param)
//
// Usage:
//	if err := api.GetAllJSON(c, &data); err != nil {
//		return
//	}
func (a *APIHelper) GetAllJSON(c *gin.Context, out interface{}, where ...interface{}) (err error) {
	var page, limit, offset int

	if where != nil {
		page, limit, offset, err = api.PaginateGetAllJSON(c, db, out, where)
	} else {
		page, limit, offset, err = api.PaginateGetAllJSON(c, db, out)
	}

	if err != nil {
		log.Println(err)
		return err
	}

	// when no data is in out
	if l, err := DBGetArrayLength(out); err == nil && l == 0 {
		c.JSON(http.StatusNotFound, gin.H{
			"status":  http.StatusNotFound,
			"message": "No data found.",
			// "page":    page,
			// "limit":   limit,
			// "offset":  offset,
		})
		return nil
	}

	// return data
	c.JSON(http.StatusOK, gin.H{
		"status": http.StatusOK,
		"data":   out,
		"page":   page,
		"limit":  limit,
		"offset": offset,
	})

	return nil
}

// Remove will remove specified item with correct ID from database
func (a *APIHelper) Remove(c *gin.Context, out interface{}, user User) (err error) {
	id := c.Param("id")
	tx := db.Begin()
	//var where string

	if err := db.Where(id).First(out).Error; err != nil {
		c.AbortWithStatusJSON(http.StatusBadRequest, gin.H{
			"status":  http.StatusBadRequest,
			"message": "Error finding object to remove.",
		})
		return fmt.Errorf("Error finding object to remove")
	}

	// validate things
	switch v := out.(type) {
	case *Items:
		// if user is trying to force their id to something
		// CAs can delete if they want though
		if user.AuthGroup < AuthCA && v.OwnerID != "" && v.OwnerID != user.Sub {
			log.Printf("Unauthorized Set OwnerID Error %s is %d.\n", user.Name, user.AuthGroup)

			c.AbortWithStatusJSON(http.StatusBadRequest, gin.H{
				"status":  http.StatusBadRequest,
				"message": "You cannot set OwnerID to somebody else!",
			})
			return fmt.Errorf("Unauthorized Set OwnerID Error")
		}

		if v.ItemID != "" && v.ItemID != id {
			log.Printf("[RestAPI Delete]: Error: v.ItemID %s != id %s.\n", v.ItemID, id)
			c.JSON(http.StatusBadRequest, gin.H{
				"status":  http.StatusBadRequest,
				"message": "Not deleted, ItemID is not matching.",
			})
			return fmt.Errorf("Unmatched ID Error")
		}

		//where = fmt.Sprintf("ItemID LIKE %s", id)

		break
	default:
		// we need to figure out how to do LIKE for the other tables
		log.Printf("[RestAPI Delete]: uncaught table type: %s.\n", reflect.TypeOf(out))
		//where = id
	}

	// delete
	log.Printf("[RestAPI Delete]: TypeOf out is: %s\n", reflect.TypeOf(out))
	log.Println(out)
	log.Println()
	if err := tx.Delete(out).Error; err != nil {
		log.Printf("[RestAPI Delete]: Error: %s\n", err.Error())
		log.Println(out)
		log.Println()

		tx.Rollback()

		c.JSON(http.StatusInternalServerError, gin.H{
			"status":  http.StatusInternalServerError,
			"message": "Not deleted.",
		})
		return err
	}

	tx.Commit()

	c.JSON(http.StatusOK, gin.H{
		"status":  http.StatusOK,
		"message": "Item Deleted successfully",
	})
	return nil
}

// Add will add item to database
func (a *APIHelper) Add(c *gin.Context, out interface{}) (err error) {
	tx := db.Begin()
	if err := tx.Create(out).Error; err != nil {
		tx.Rollback()
		c.JSON(http.StatusInternalServerError, gin.H{
			"status":  http.StatusInternalServerError,
			"message": "Not added.",
		})
		log.Printf("[RestAPI Add]: Error: %s\n", err.Error())
		return err
	}

	tx.Commit()
	c.JSON(http.StatusOK, gin.H{
		"status": http.StatusOK,
		"data":   out,
	})
	return nil
}

// PaginateGetAllJSON will do pagination things for GetAllJSON
// Make sure to return if err is not nil:
//
//	if err != nil {
//		log.Panicln(err)
//		return
//	}
//
// Optional Query Params:
//   page  - set current page (starting from 0)
//   limit - limit amout of items received (default 10, must be used with page param)
func (*APIHelper) PaginateGetAllJSON(c *gin.Context, db *gorm.DB, out interface{}, where ...interface{}) (page int, limit int, offset int, err error) {
	// default values
	page = -1
	offset = -1

	// get limit param
	limit, err = strconv.Atoi(c.Query("limit"))
	if err != nil {
		// issue with limit query (not fatal)
		//log.Println(err)
		limit = 10
		err = nil // needed, because this param is optional
	}

	// get page param
	pquery := c.Query("page")
	page, err = strconv.Atoi(pquery)
	offset = (page) * limit
	if pquery == "" {
		// default if no page param
		if where != nil {
			log.Printf("[PaginateGetAllJSON where[%d]]: %s\n", len(where), where)
			// handle interface
			if len(where) == 1 {
				db.Find(out, where[0])
			} else {
				db.Find(out, where)
			}
		} else {
			db.Find(out)
		}
		limit = -1
		err = nil
		return
	} else if err == nil && page >= 0 {
		// use pagination
		var derr error

		if where != nil {
			log.Printf("[PaginateGetAllJSON where[%d]]: %s\n", len(where), where)
			if len(where) == 1 {
				derr = db.Offset(offset).Limit(limit).Find(out, where[0]).Error
			} else {
				derr = db.Offset(offset).Limit(limit).Find(out, where).Error
			}
		} else {
			derr = db.Offset(offset).Limit(limit).Find(out).Error
		}

		if derr != nil {
			// issue with query
			c.JSON(http.StatusNotFound, gin.H{
				"status":  http.StatusNotFound,
				"message": "No items for given page.",
			})
			err = derr
			return
		}
	} else if err != nil {
		// error with page query
		c.JSON(http.StatusBadRequest, gin.H{
			"status":  http.StatusBadRequest,
			"message": "Error proccessing page query.",
		})
		return
	} else if page < 0 {
		// invalid page
		msg := "Invalid page"
		c.JSON(http.StatusBadRequest, gin.H{
			"status":  http.StatusBadRequest,
			"message": fmt.Sprintf("%s, %d.", msg, page),
		})
		err = fmt.Errorf(msg)
		return
	}

	return
}

// func (*APIHelper) GetPrimaryKeyForTable(out interface{}) {
// 	a := &gorm.Scope.New(out).PrimaryKey()

// 	for _, field := range gorm.DB.NewScope.NewScope(out).GetStructFields() {
// 		if field.IsPrimaryKey {

// 			break
// 		}
// 	}
// }

// CheckDBFound will check if record found in DB
// Usage:
// if err := a.CheckDBFound(c, q); err != nil {
// 	return err
// }
func (*APIHelper) CheckDBFound(c *gin.Context, q *gorm.DB) error {
	if q.Error != nil || q.RecordNotFound() {
		c.JSON(http.StatusNotFound, gin.H{
			"status":  http.StatusNotFound,
			"message": "Not found.",
		})
		return fmt.Errorf("RecordNotFound")
	}

	return nil
}

// NotImplimented will return JSON saying the feature is not implimented
func (*APIHelper) NotImplimented(c *gin.Context) {
	c.JSON(http.StatusNotImplemented, gin.H{
		"status":  http.StatusNotImplemented,
		"message": "This feature is not implimented.",
	})
}
