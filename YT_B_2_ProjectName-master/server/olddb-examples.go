// olddb-examples.go should be deleted

/// NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE
/// NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE
////////////////////////////////////////////////////////////////////////////////
///
/// NOTICE: This file is Depreciated, please only use it for code examples
///
////////////////////////////////////////////////////////////////////////////////
/// NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE
/// NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE

package main

import (
	"net/http"

	"github.com/gin-gonic/gin"

	"github.com/jinzhu/gorm"
)

// Test database fields
type Test struct {
	gorm.Model
	FirstName string `json:"firstname"`
	LastName  string `json:"lastname"`
}

// TransformedTest database fields
type TransformedTest struct {
	ID        uint   `json:"id"`
	FirstName string `json:"firstname"`
	LastName  string `json:"lastname"`
}

// CreateTest creates a new Test row
func CreateTest(c *gin.Context) {
	test := Test{FirstName: c.PostForm("firstname"), LastName: c.PostForm("lastname")}

	tx := db.Begin()
	if err := tx.Create(&test).Error; err != nil {
		tx.Rollback()
		c.JSON(http.StatusBadRequest, gin.H{"status": http.StatusBadRequest, "message": "Test item creation failed!"})
		return
	}
	// db.Save(&test)

	tx.Commit()
	c.JSON(http.StatusCreated, gin.H{"status": http.StatusCreated, "message": "Test item created successfully!", "resourceId": test.ID})
}

// FetchAllTest fetches all Test rows
func FetchAllTest(c *gin.Context) {
	var tests []Test
	var _tests []TransformedTest

	db.Find(&tests)

	if len(tests) <= 0 {
		c.JSON(http.StatusNotFound, gin.H{"status": http.StatusNotFound, "message": "No test found!"})
		return
	}

	//transforms the todos for building a good response
	for _, item := range tests {
		// completed := false
		// if item.Completed == 1 {
		// 	completed = true
		// } else {
		// 	completed = false
		// }
		_tests = append(_tests, TransformedTest{ID: item.ID, FirstName: item.FirstName, LastName: item.LastName})
	}
	c.JSON(http.StatusOK, gin.H{"status": http.StatusOK, "data": _tests})
}

// FetchSingleTest fetches single Test row
func FetchSingleTest(c *gin.Context) {
	var test Test
	testID := c.Param("id")

	db.First(&test, testID)

	if test.ID == 0 {
		c.JSON(http.StatusNotFound, gin.H{"status": http.StatusNotFound, "message": "No test found!"})
		return
	}

	// completed := false
	// if todo.Completed == 1 {
	// 	completed = true
	// } else {
	// 	completed = false
	// }

	_test := TransformedTest{ID: test.ID, FirstName: test.FirstName, LastName: test.LastName}
	c.JSON(http.StatusOK, gin.H{"status": http.StatusOK, "data": _test})
}

// UpdateTest updates a single Test row
func UpdateTest(c *gin.Context) {
	var test Test
	testID := c.Param("id")

	db.First(&test, testID)

	if test.ID == 0 {
		c.JSON(http.StatusNotFound, gin.H{"status": http.StatusNotFound, "message": "No test found!"})
		return
	}

	tx := db.Begin()
	err := tx.Model(&test).Update("FirstName", c.PostForm("firstname")).Error
	err1 := tx.Model(&test).Update("LastName", c.PostForm("lastname")).Error
	if err != nil || err1 != nil {
		tx.Rollback()
		c.JSON(http.StatusBadRequest, gin.H{"status": http.StatusBadRequest, "message": "Test update failed!"})
		return
	}

	tx.Commit()
	c.JSON(http.StatusOK, gin.H{"status": http.StatusOK, "message": "Test updated successfully!"})
}

// DeleteTest deletes a Test row
func DeleteTest(c *gin.Context) {
	var test Test
	testID := c.Param("id")

	db.First(&test, testID)

	if test.ID == 0 {
		c.JSON(http.StatusNotFound, gin.H{"status": http.StatusNotFound, "message": "No test found!"})
		return
	}

	db.Delete(&test)
	c.JSON(http.StatusOK, gin.H{"status": http.StatusOK, "message": "Test deleted successfully!"})
}

// dbGetAll returns array of all Test rows (for web templates)
func dbGetAll() []TransformedTest {
	var tests []Test
	var _tests []TransformedTest

	db.Find(&tests)

	if len(tests) <= 0 {
		return []TransformedTest{{ID: 0, FirstName: "", LastName: ""}}
	}

	for _, item := range tests {
		_tests = append(_tests, TransformedTest{ID: item.ID, FirstName: item.FirstName, LastName: item.LastName})
	}

	return _tests
}

// Other functions (need to rewrite)
////////////////////////////////////////////////////////////////////////////////
// func dbGetTables(db *sql.DB) []string {
// 	if db == nil {
// 		return []string{}
// 	}

// 	ret := []string{}

// 	rows, err := db.Query("SHOW TABLES;")
// 	if err != nil {
// 		log.Print(err)
// 	}

// 	for rows.Next() {
// 		var table string

// 		if err := rows.Scan(&table); err != nil {
// 			log.Print(err)
// 		}

// 		ret = append(ret, table)
// 	}

// 	if err := rows.Err(); err != nil {
// 		log.Print(err)
// 	}

// 	return ret
// }
