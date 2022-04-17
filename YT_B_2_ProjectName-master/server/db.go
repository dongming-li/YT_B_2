// db.go sets up the db driver and defines all of the tables
package main

import (
	"fmt"
	"log"
	"reflect"

	_ "github.com/go-sql-driver/mysql"
	"github.com/jinzhu/gorm"
)

var (
	items      Items
	houses     Houses
	person     Person
	ca         CommunityAdviser
	residents  Residents
	dorms      Dorms
	student    Student
	vendor     Vendor
	chat       ChatMessages
	chatusers  ChatUsers
	files      Files
	usersfiles UsersFiles
)

// Database return gorm.DB object with logged-in database
func Database() *gorm.DB {
	dbret, err := gorm.Open("mysql", "dbu309ytb2:Cwzr1S5C@tcp(mysql.cs.iastate.edu:3306)/db309ytb2?charset=utf8&parseTime=True&loc=Local")
	if err != nil {
		log.Fatal(err)
	}
	dbret.DB().SetMaxIdleConns(20)
	return dbret
}

// DatabaseInit initializes the database
func DatabaseInit(db *gorm.DB) {
	// db.CreateTable(
	// 	&Person{},
	// 	&Student{},
	// 	&CommunityAdviser{},
	// 	&Vendor{},
	// 	&Residents{},
	// 	&ChatUsers{},
	// 	&ChatMessages{},
	// 	&Dorms{},
	// 	&Houses{},
	// 	&Items{},
	// )

	db.AutoMigrate(
		&Person{},
		&Student{},
		&CommunityAdviser{},
		&Vendor{},
		&Residents{},
		&ChatUsers{},
		&ChatMessages{},
		&Dorms{},
		&Houses{},
		&Items{},
		&Files{},
		&UsersFiles{},
	)
}

// DBGetArrayLength will return the length of a given DB
func DBGetArrayLength(db interface{}) (l int, err error) {
	switch v := db.(type) {
	case *[]Person:
		l = len(*v)
		break
	case *[]Student:
		l = len(*v)
		break
	case *[]CommunityAdviser:
		l = len(*v)
		break
	case *[]Vendor:
		l = len(*v)
		break
	case *[]Residents:
		l = len(*v)
		break
	case *[]ChatUsers:
		l = len(*v)
		break
	case *[]ChatMessages:
		l = len(*v)
		break
	case *[]Dorms:
		l = len(*v)
		break
	case *[]Houses:
		l = len(*v)
		break
	case *[]Items:
		l = len(*v)
		break
	case *[]Files:
		l = len(*v)
		break
	case *[]UsersFiles:
		l = len(*v)
		break
	default:
		l = -1
		err = fmt.Errorf("InvalidTypeError")
		log.Printf("[DBGetArrayLength error] Type is: %s\n", reflect.TypeOf(db))
	}

	return
}

// Person table
type Person struct {
	ID         string `json:"ID"         gorm:"column:ID; primary_key; type:varchar(21)"`
	FirstName  string `json:"FirstName"  gorm:"column:FirstName"`
	LastName   string `json:"LastName"   gorm:"column:LastName"`
	PictureURL string `json:"PictureURL" gorm:"column:PictureURL; size:500"`
	Email      string `json:"Email"      gorm:"column:Email"`
	AuthGroup  int    `json:"AuthGroup"  gorm:"column:AuthGroup"`
}

// TableName for Person
func (u *Person) TableName() string {
	return "Person"
}

// Student table
type Student struct {
	StudentID  string `json:"StudentID"  gorm:"column:StudentID; primary_key"`
	HouseName  string `json:"HouseName"  gorm:"column:HouseName; not null"`
	DormName   string `json:"DormName"   gorm:"column:DormName; not null"`
	RoomNumber string `json:"RoomNumber" gorm:"column:RoomNumber; not null"`
}

// TableName for Student
func (u *Student) TableName() string {
	return "Student"
}

// CommunityAdviser table
type CommunityAdviser struct {
	CommunityAdviserID string `json:"CommunityAdviserID" gorm:"column:CommunityAdviserID; primary_key"`
	HouseName          string `json:"HouseName"          gorm:"column:HouseName; not null"`
	DormName           string `json:"DormName"           gorm:"column:DormName; not null"`
	RoomNumber         string `json:"RoomNumber"         gorm:"column:RoomNumber; not null"`
}

// TableName for CommunityAdviser
func (u *CommunityAdviser) TableName() string {
	return "CommunityAdviser"
}

// Vendor table
type Vendor struct {
	VendorID    string `json:"VendorID"    gorm:"column:VendorID; primary_key"`
	CompanyName string `json:"CompanyName" gorm:"column:CompanyName; not null"`
}

// TableName for Vendor
func (u *Vendor) TableName() string {
	return "Vendor"
}

// Residents table
type Residents struct {
	HouseCAID string `json:"HouseCAID" gorm:"column:HouseCAID; primary_key"`
	StudentID string `json:"StudentID" gorm:"column:StudentID"`
}

// TableName for Residents
func (u *Residents) TableName() string {
	return "Residents"
}

// ChatUsers table
type ChatUsers struct {
	ChatID     string `json:"ChatID"     gorm:"column:ChatID; primary_key"`
	OwnerID    string `json:"OwnerID"    gorm:"column:OwnerID"`
	BorrowerID string `json:"BorrowerID" gorm:"column:BorrowerID"`
}

// TableName for ChatUsers
func (u *ChatUsers) TableName() string {
	return "ChatUsers"
}

// ChatMessages table
type ChatMessages struct {
	ChatID        string `json:"ChatID"        gorm:"column:ChatID"`
	MessageID     string `json:"MessageID"     gorm:"column:MessageID; primary_key"`
	MessageText   string `json:"MessageText"   gorm:"column:MessageText; size:500"`
	TimeOfMessage int64  `json:"TimeOfMessage" gorm:"column:TimeOfMessage; type:timestamp"`
}

// TableName for ChatMessages
func (u *ChatMessages) TableName() string {
	return "ChatMessages"
}

// Dorms table
type Dorms struct {
	DormName string `json:"DormName" gorm:"column:DormName"`
	DormID   string `json:"DormID"   gorm:"column:DormID; primary_key"`
}

// TableName for Dorms
func (u *Dorms) TableName() string {
	return "Dorms"
}

// Houses table
type Houses struct {
	HouseName   string `json:"HouseName"   gorm:"column:HouseName"`
	HouseID     string `json:"HouseID"     gorm:"column:HouseID; primary_key"`
	DormHouseID string `json:"DormHouseID" gorm:"column:DormHouseID; not null"`
}

// TableName for Houses
func (u *Houses) TableName() string {
	return "Houses"
}

// Items table
type Items struct {
	ItemName      string `json:"ItemName"      gorm:"column:ItemName"`
	OwnerID       string `json:"OwnerID"       gorm:"column:OwnerID; not null"`
	ItemsHouseID  string `json:"ItemsHouseID"  gorm:"column:ItemsHouseID"`
	ItemPictureID string `json:"ItemPictureID" gorm:"column:ItemPictureID"`
	ItemID        string `json:"ItemID"        gorm:"column:ItemID; primary_key"`
	BorrowerID    string `json:"BorrowerID"    gorm:"column:BorrowerID"`
	DateBorrowed  int64  `json:"DateBorrowed"  gorm:"column:DateBorrowed"`
	DateDue       int64  `json:"DateDue"       gorm:"column:DateDue"`
	Available     bool   `json:"Available"     gorm:"column:Available"`
}

// TableName for Items
func (u *Items) TableName() string {
	return "Items"
}

// Files table
type Files struct {
	FileID string `json:"FileID" gorm:"column:FileID; primary_key"`
}

// TableName for Files
func (u *Files) TableName() string {
	return "Files"
}

// UsersFiles table
type UsersFiles struct {
	gorm.Model
	UserID      string `json:"UserID"      gorm:"column:UserID; not null"`
	UsersFileID string `json:"UsersFileID" gorm:"column:UsersFileID; not null"`
}

// TableName for UsersFiles
func (u *UsersFiles) TableName() string {
	return "UsersFiles"
}
