package main

import (
	"fmt"
	"os"
	"testing"

	"github.com/olekukonko/tablewriter"
)

func TestGetPeople(t *testing.T) {
	var people []Person
	db.Find(&people)
	fmt.Printf("Table Length %d\n", len(people))
	table := tablewriter.NewWriter(os.Stdout)
	table.SetHeader([]string{"FirstName", "LastName", "ID", "Email"})
	for _, i := range people {
		table.Append([]string{i.FirstName, i.LastName, i.ID, i.Email})
	}
	table.Render()
}

// func TestUserSetupDelete(t *testing.T) {
// 	u := User{
// 		Sub:           "113190304220000000000",
// 		Name:          "Kyle Zelnio",
// 		GivenName:     "Kyle",
// 		FamilyName:    "Zelnio",
// 		Picture:       "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png",
// 		EmailVerified: true,
// 	}

// 	//db.Where("ID = ?", u.Sub).Delete(&Person{})

// 	if err := u.Setup(db); err != nil {
// 		t.Error(err.Error())
// 	}

// 	if err := u.Delete(db); err != nil {
// 		t.Error(err.Error())
// 	}
// }
