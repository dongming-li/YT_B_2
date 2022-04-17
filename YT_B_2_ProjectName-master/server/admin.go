package main

func createItemList() []string {
	var itemList []Items
	var itemNameList []string

	db.Find(&itemList)

	for i := 0; i < len(itemList); i++ {
		item := itemList[i]
		fullName := item.ItemName + ", " + item.ItemID
		itemNameList = append(itemNameList, fullName)
	}

	return itemNameList
}

func createHouseList() []string {
	var houseList []Houses
	var houseNameList []string

	db.Find(&houseList)

	for i := 0; i < len(houseList); i++ {
		item := houseList[i]
		fullName := item.HouseName + ", " + item.HouseID
		houseNameList = append(houseNameList, fullName)
	}

	return houseNameList
}

func createDormList() []string {
	var dormList []Dorms
	var dormNameList []string

	db.Find(&dormList)

	for i := 0; i < len(dormList); i++ {
		item := dormList[i]
		fullName := item.DormName + ", " + item.DormID
		dormNameList = append(dormNameList, fullName)
	}

	return dormNameList
}

func createVendorList() []string {
	var vendorList []Vendor
	var vendorNameList []string

	db.Find(&vendorList)

	for i := 0; i < len(vendorList); i++ {
		item := vendorList[i]
		fullName := item.CompanyName + ", " + item.VendorID
		vendorNameList = append(vendorNameList, fullName)
	}

	return vendorNameList
}
