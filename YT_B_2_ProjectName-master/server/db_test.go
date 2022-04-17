package main

import (
	"fmt"
	"os"
	"testing"

	"github.com/olekukonko/tablewriter"
)

type DescResult struct {
	Field   string `gorm:"column:Field"`
	Type    string `gorm:"column:Type"`
	Null    string `gorm:"column:Null"`
	Key     string `gorm:"column:Key"`
	Default string `gorm:"column:Default"`
	Extra   string `gorm:"column:Extra"`
}

func TestGetTableDesc(t *testing.T) {
	DatabaseInit(db)
	tables := []string{
		"Person",
		"Student",
		"CommunityAdviser",
		"Vendor",
		"Residents",
		"ChatUsers",
		"ChatMessages",
		"Dorms",
		"Houses",
		"Items",
	}

	for i := range tables {
		data := descTable(tables[i])
		fmt.Println(tables[i])

		table := tablewriter.NewWriter(os.Stdout)
		table.SetHeader([]string{"Field", "Type", "Null", "Key", "Default", "Extra"})

		for j := range data {
			d := data[j]
			if len(d.Field) > 0 {
				table.Append([]string{d.Field, d.Type, d.Null, d.Key, d.Default, d.Extra})
			}
		}

		table.Render()
		fmt.Println()
	}

}

func descTable(t string) []DescResult {
	var ret []DescResult

	rows, err := db.Raw("DESCRIBE " + t).Find(&ret).Rows()
	if err != nil {
		panic(err)
	}
	defer rows.Close()

	for rows.Next() {
		var d DescResult
		rows.Scan(&d)
		ret = append(ret, d)
	}

	return ret
}
