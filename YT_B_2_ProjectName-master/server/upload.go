package main

import (
	"bytes"
	"crypto/sha256"
	"fmt"
	"io"
	"log"
	"net/http"
	"os"

	"github.com/gin-gonic/gin"
	"github.com/h2non/filetype"
	"github.com/jinzhu/gorm"
)

// UploadHandler will handle uploading files
func (*Files) UploadHandler(c *gin.Context) {
	var err error
	user := c.MustGet("user").(User)

	// get file from form data
	file, _, err := c.Request.FormFile("file")
	defer file.Close()
	if err != nil {
		c.AbortWithStatusJSON(http.StatusBadRequest, gin.H{
			"status":  http.StatusBadRequest,
			"message": fmt.Sprintf("get form err: %s", err.Error()),
		})
		return
	}

	// read file into buffer
	buf := bytes.NewBuffer(nil)
	if _, err = io.Copy(buf, file); err != nil {
		c.AbortWithStatusJSON(http.StatusBadRequest, gin.H{
			"status":  http.StatusBadRequest,
			"message": fmt.Sprintf("read file error: %s", err.Error()),
		})
		return
	}

	// check file header for type
	fileBytes := buf.Bytes()
	head := fileBytes[:261]
	var ext string

	// check file type
	if filetype.IsMIME(head, "image/jpeg") {
		ext = ".jpg"
	} else {
		c.AbortWithStatusJSON(http.StatusBadRequest, gin.H{
			"status":  http.StatusBadRequest,
			"message": "invalid file type",
		})
		return
	}

	// hash the file
	hasher := sha256.New()
	sha256sum := string(hasher.Sum(fileBytes))

	// filename
	filename := sha256sum + ext

	// check if file already exists
	if _, err = os.Stat("./upload/" + filename); !os.IsNotExist(err) {
		// associate file with user
		tx := db.Begin()
		tx, err = associateFileWithUser(c, user, filename)
		if err != nil {
			return
		}

		tx.Commit()

		// tell user it already exists
		c.AbortWithStatusJSON(http.StatusFound, gin.H{
			"status":  http.StatusFound,
			"fileid":  filename,
			"message": "file already exists",
		})
		return
	}

	// save file name to db
	files := Files{
		FileID: filename,
	}

	tx := db.Begin()
	if err = tx.Create(&files).Error; err != nil {
		tx.Rollback()
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{
			"status":  http.StatusInternalServerError,
			"message": fmt.Sprintf("db register error: %s", err.Error()),
		})
		return
	}

	// associate file with user
	tx, err = associateFileWithUser(c, user, filename)
	if err != nil {
		return
	}

	// open file on disk
	out, err := os.Create("./upload/" + filename)
	if err != nil {
		tx.Rollback()
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{
			"status":  http.StatusInternalServerError,
			"message": fmt.Sprintf("create file error: %s", err.Error()),
		})
		return
	}
	defer out.Close()

	// save data to file
	if _, err := io.Copy(out, file); err != nil {
		tx.Rollback()
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{
			"status":  http.StatusInternalServerError,
			"message": fmt.Sprintf("save file error: %s", err.Error()),
		})
		return
	}

	// tell user the name of the file
	tx.Commit()
	c.JSON(http.StatusOK, gin.H{
		"status": http.StatusOK,
		"fileid": filename,
	})
}

// UGetAllJSON will get a list of user's files as JSON
func (*Files) UGetAllJSON(c *gin.Context) {
	var uploads []Files
	var ufiles []UsersFiles

	user := c.MustGet("user").(User)

	// get list of files for user
	if err := db.Find(ufiles, "UserID=", user.Sub).Error; err != nil {
		log.Printf("[Files.UGetAllJSON] UsersFiles db error: %s\n", err.Error())
	}

	if len(ufiles) == 0 {
		c.AbortWithStatusJSON(http.StatusBadRequest, gin.H{
			"status":  http.StatusBadRequest,
			"message": "No files found for user.",
		})
		return
	}

	// append each of users files to array
	log.Println("[Files.UGetAllJSON] appending user files...")
	for i := 0; i < len(ufiles); i++ {
		var f Files
		if err := db.First(f, ufiles[i].UsersFileID).Error; err != nil {
			log.Printf("[Files.UGetAllJSON] Files db error: %s\n", err.Error())
		}

		uploads = append(uploads, f)
	}

	// return
	c.JSON(http.StatusOK, gin.H{
		"status": http.StatusOK,
		"data":   uploads,
	})

}

// associateFileWithUser will associate file with user
// Note: this will rollback but not commit
// Usage:
//   tx, err = associateFileWithUser(c, user, filename)
//   if err != nil {
//   	return
//   }
func associateFileWithUser(c *gin.Context, user User, filename string) (tx *gorm.DB, err error) {
	usersfiles := UsersFiles{
		UserID:      user.Sub,
		UsersFileID: filename,
	}

	if err = tx.Create(&usersfiles).Error; err != nil {
		tx.Rollback()
		c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{
			"status":  http.StatusInternalServerError,
			"fileid":  filename,
			"message": fmt.Sprintf("associate file with user error: %s", err.Error()),
		})
	}

	return
}
