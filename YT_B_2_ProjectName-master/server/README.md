# CySwapper API

HOST: `http://proj-309-yt-b-2.cs.iastate.edu:8080`

## API Authentication

### JWT

> JWT auth is useful for client applications.

1. Login via Google OAuth
2. `POST` the Google OAuth authcode to `/authcode`, with form data: `authCode=<authcode>`
3. Receive a [JWT](https://jwt.io) token
4. Use the JWT token in the following format as a header for all future requests: `Authorization: Bearer <token>`

See: [Integrate with Google Using the OAuth 2.0 Authorization Code Flow](https://developers.google.com/actions/identity/oauth2-code-flow)

### Cookie

> Cookie auth is useful for web clients.

1. `GET /login`
2. (Redirect to `/auth`)
3. Receive cookie

## Response format for `/api/` endpoints

- For each endpoint ending in `/:id`, you must replace `:id` with the primary key for the respective database when making your request
- `GET` requests for pagable endpoints have following optional params:
	- `page`
	- `limit` (default is 10 if not specified)

- All `GET` to endpoints **without** `/:id` in URL **are pageable** and will return one or more objects in a data array similar to the following:
```json
{
	"data":[
		{
			"example-object": 1
		},
		{
			"example-object": 2
		}
	],
	"status": "200"
}
```

- `GET` requests to endpoints ending in `/:id` will return a data object similar to the following:
```json
{
	"data":{
		"example-object": true
	},
	"status": "200"
}
```
- `POST` and `PATCH` require request data to be JSON
	- `POST` will create a new object at `/:id`
	- `PATCH` will modify an existing object at `/:id`; you only need to provide JSON fields for the data you want changed
	- The request format will be similar to the following:
```json
{
	"data":{
		"example-object": true
	}
}
```
- `DELETE` will remove an object at `/:id` if it exists

## List of JSON response error messages

| Message                                | Code  | Cause                       |
|----------------------------------------|-------| ----------------------------|
| `file already exists`                  | `302` | Upload: The file already exists, don't worry, you can still use it though. |
| `Invalid header.`                      | `400` | JWT/Cookie: Authorization header format is incorrect. Cookie will need to login again |
| `Invalid token.`                       | `400` | JWT: Token is either expired or wrong |
| `Invalid sub in token.`                | `400` | JWT/Cookie: This should really not ever happen |
| `Missing authCode.`                    | `400` | JWT: You need to send correct form data |
| `Invalid authCode.`                    | `400` | JWT: The authCode you sent is not being accepted by Google |
| `Error proccessing page query.`        | `400` | Pagable API: Page query doesn't have an integer |
| `Invalid page, %d.`                    | `400` | Pagable API: There is no page, %d |
| `get form err: %s`                     | `400` | Upload: missing field `file` in form data |
| `read file error: %s`                  | `400` | Upload: issue while reading file into buffer |
| `invalid file type`                    | `400` | Upload: invalid file type, try uploading a jpeg file |
| `No files found for user.`             | `400` | GET Upload: user hasn't uploaded anything yet |
| *                                      | `400` | Bad request, see message |
| `unauthorized`                         | `401` | You are probably not logged in or are going somewhere you should not |
| `Not found.`                           | `404` | API: Could not find data |
| `No items for given page.`             | `404` | Pagable API: Either no data exists, or you have the wrong page |
| *                                      | `406` | API PATCH: Unacceptable form data
| `Could not get user info from Google.` | `500` | OAuth: Google is very confused right now or server has network issues |
| `Error with reading Google user data.` | `500` | OAuth: Google is very confused right now or server has network issues |
| `Could not parse userinfo.`            | `500` | OAuth: Google is very confused right now or server has network issues |
| `associate file with user error: %s`   | `500` | Upload: error while assiciating uploaded file with current user |
| `db register error: %s`                | `500` | Upload: error while saving filename to DB |
| `create file error: %s`                | `500` | Upload: error while creating file on disk |
| `save file error: %s`                  | `500` | Upload: error while saving file to disk |
| *                                      | `500` | Internal server error, see message |
| `This feature is not implimented.`     | `501` | API: Endpoint is not implimented yet |

## List of `/api/` endpoints

### Loggedin
> Will provide current user context

- `GET /api/loggedin/` -> User details
```json
{
	"authtype": "cookie",
	"user": {
		"sub": "115707492445847153285",
		"name": "Layton Nelson",
		"picture": "https://lh6.googleusercontent.com/-7nezuW-tMAo/AAAAAAAAAAI/AAAAAAAAAho/0wS7_VI542Q/photo.jpg",
	},
	"userid": "115707492445847153285"
}
```

- `GET /api/loggedin/items`     -> User's items
- `POST /api/loggedin/upload`   -> Upload images
- `GET /api/loggedin/upload`    -> Get list of uploaded images for user

### Items
```
POST   /api/items                --> admin
GET    /api/items                --> user
GET    /api/items/:id            --> user
DELETE /api/items/:id            --> admin
PATCH  /api/items/:id            --> admin
```

### Houses
```
POST   /api/houses               --> admin
GET    /api/houses               --> user
GET    /api/houses/:id           --> user
DELETE /api/houses/:id           --> admin
PATCH  /api/houses/:id           --> admin
```

### Person
```
POST   /api/person               --> admin
GET    /api/person               --> user
GET    /api/person/:id           --> user
DELETE /api/person/:id           --> admin
PATCH  /api/person/:id           --> admin
```

### CA
```
POST   /api/ca                   --> admin
GET    /api/ca                   --> user
GET    /api/ca/:id               --> user
DELETE /api/ca/:id               --> admin
PATCH  /api/ca/:id               --> admin
```

### Residents
```
POST   /api/residents            --> admin
GET    /api/residents            --> user
GET    /api/residents/:id        --> user
DELETE /api/residents/:id        --> admin
PATCH  /api/residents/:id        --> admin
```

### Dorms
```
POST   /api/dorms                --> admin
GET    /api/dorms                --> user
GET    /api/dorms/:id            --> user
DELETE /api/dorms/:id            --> admin
PATCH  /api/dorms/:id            --> admin
```

### Student
```
POST   /api/student              --> admin
GET    /api/student              --> user
GET    /api/student/:id          --> user
DELETE /api/student/:id          --> admin
PATCH  /api/student/:id          --> admin
```

### Vendor
```
POST   /api/vendor               --> admin
GET    /api/vendor               --> user
GET    /api/vendor/:id           --> user
DELETE /api/vendor/:id           --> admin
PATCH  /api/vendor/:id           --> admin
```

## Format of JSON Data
> See also [server/db.go](https://git.linux.iastate.edu/309Fall2017/YT_B_2_ProjectName/blob/master/server/db.go)

```go
// Person table
type Person struct {
	ID         string `json:"ID"         gorm:"column:ID; primary_key; type:varchar(21)"`
	FirstName  string `json:"FirstName"  gorm:"column:FirstName"`
	LastName   string `json:"LastName"   gorm:"column:LastName"`
	PictureURL string `json:"PictureURL" gorm:"column:PictureURL; size:500"`
	Email      string `json:"Email"      gorm:"column:Email"`
}

// Student table
type Student struct {
	StudentID  string `json:"StudentID"  gorm:"column:StudentID; primary_key"`
	HouseName  string `json:"HouseName"  gorm:"column:HouseName; not null"`
	DormName   string `json:"DormName"   gorm:"column:DormName; not null"`
	RoomNumber string `json:"RoomNumber" gorm:"column:RoomNumber; not null"`
}

// CommunityAdviser table
type CommunityAdviser struct {
	CommunityAdviserID string `json:"CommunityAdviserID" gorm:"column:CommunityAdviserID; primary_key"`
	HouseName          string `json:"HouseName"          gorm:"column:HouseName; not null"`
	DormName           string `json:"DormName"           gorm:"column:DormName; not null"`
	RoomNumber         string `json:"RoomNumber"         gorm:"column:RoomNumber; not null"`
}

// Vendor table
type Vendor struct {
	VendorID  string `json:"VendorID"    gorm:"column:VendorID; primary_key"`
	HouseName string `json:"CompanyName" gorm:"column:CompanyName; not null"`
}

// Residents table
type Residents struct {
	HouseCAID string `json:"HouseCAID" gorm:"column:HouseCAID; primary_key"`
	StudentID string `json:"StudentID" gorm:"column:StudentID"`
}

// ChatUsers table
type ChatUsers struct {
	ChatID     string `json:"ChatID"     gorm:"column:ChatID; primary_key"`
	OwnerID    string `json:"OwnerID"    gorm:"column:OwnerID"`
	BorrowerID string `json:"BorrowerID" gorm:"column:BorrowerID"`
}

// ChatMessages table
type ChatMessages struct {
	ChatID        string `json:"ChatID"        gorm:"column:ChatID"`
	MessageID     string `json:"MessageID"     gorm:"column:MessageID; primary_key"`
	MessageText   string `json:"MessageText"   gorm:"column:MessageText; size:500"`
	TimeOfMessage int64  `json:"TimeOfMessage" gorm:"column:TimeOfMessage; type:timestamp"`
}

// Dorms table
type Dorms struct {
	DormName string `json:"DormName" gorm:"column:DormName"`
	DormID   string `json:"DormID"   gorm:"column:DormID; primary_key"`
}

// Houses table
type Houses struct {
	HouseName   string `json:"HouseName"   gorm:"column:HouseName"`
	HouseID     string `json:"HouseID"     gorm:"column:HouseID; primary_key"`
	DormHouseID string `json:"DormHouseID" gorm:"column:DormHouseID; not null"`
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

// Files table
type Files struct {
	FileName string `json:"FileID" gorm:"column:FileID; primary_key"`
}

// UsersFiles table
type UsersFiles struct {
	gorm.Model
	UserID      string `json:"UserID"      gorm:"column:UserID; not null"`
	UsersFileID string `json:"UsersFileID" gorm:"column:UsersFileID; not null"`
}

// FROM: user.go
// User type to handle user infromation from Google OAuth
type User struct {
	Sub           string    `gorm:"primary_key" json:"sub"`
	CreatedAt     time.Time `json:"-"`
	Name          string    `json:"name"`
	GivenName     string    `json:"given_name"`
	FamilyName    string    `json:"family_name"`
	Profile       string    `json:"profile"`
	Picture       string    `json:"picture"`
	Email         string    `json:"email"`
	EmailVerified bool      `json:"email_verified"`
	Gender        string    `json:"gender"`
	Hd            string    `json:"hd"`
}
```