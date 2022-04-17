package main

import (
	"fmt"
	"html/template"
	"log"
	"net/http"
	"strings"
	"time"

	"github.com/jinzhu/gorm"

	"github.com/gin-contrib/multitemplate"
	"github.com/gin-gonic/contrib/sessions"
	"github.com/gin-gonic/gin"
	"github.com/googollee/go-socket.io"
)

// compiler variables
var githash = "unknown"
var db *gorm.DB

// createMyRender defines render templates for the web routes
func createMyRender() multitemplate.Render {
	var baseLayout = "./templates/_layout.tmpl"
	r := multitemplate.New()
	r.AddFromFiles("database", baseLayout, "./templates/database.tmpl")
	r.AddFromFiles("draft", baseLayout, "./templates/draft.tmpl")
	r.AddFromFiles("index", baseLayout, "./templates/index.tmpl")
	r.AddFromFiles("profile", baseLayout, "./templates/profile.tmpl")
	r.AddFromFiles("error", baseLayout, "./templates/error.tmpl")
	r.AddFromFiles("ApiList", baseLayout, "./templates/ApiList.tmpl")
	r.AddFromFiles("items", baseLayout, "./templates/items.tmpl")
	r.AddFromFiles("admin", baseLayout, "./templates/admin.tmpl")
	r.AddFromFiles("public_items", baseLayout, "./templates/public_items.tmpl")
	r.AddFromFiles("public_dorms", baseLayout, "./templates/public_dorms.tmpl")
	r.AddFromFiles("public_houses", baseLayout, "./templates/public_houses.tmpl")
	r.AddFromFiles("public_vendors", baseLayout, "./templates/public_vendors.tmpl")
	return r
}

// revisionMiddleware will add headers that provide the current revision
func revisionMiddleware() gin.HandlerFunc {
	rev := strings.TrimSpace(githash)
	return func(c *gin.Context) {
		c.Writer.Header().Set("X-Revision", rev)
		c.Next()
	}
}

// AppEngine handles the main Gin Engine
func AppEngine() *gin.Engine {
	r := gin.Default()
	r.HTMLRender = createMyRender()

	// define middleware
	r.Use(gin.Recovery())
	r.Use(revisionMiddleware())
	//r.Use(sessions.Sessions("cyswappersession", store))

	// setup static files
	r.Static("/css", "./templates/css")
	r.Static("/img", "./templates/img")
	r.Static("/js", "./templates/js")

	// setup function maps
	//Might not be needed.
	r.SetFuncMap(template.FuncMap{
		"item.Add": items.Add,
	})

	// setup database
	DatabaseInit(db)

	// setup oauth
	auth.setup(r)

	// give token for login security
	r.POST("/authcode", auth.GetLoginCodeJWT)
	// TODO add refresh for authcode

	// setup our api group
	a := r.Group("/api")
	a.Use(jwtauth.Check()) // jwt auth security
	{
		// show web template TODO REMOVE IN FUTURE
		a.GET("/", func(c *gin.Context) {
			c.HTML(http.StatusOK, "ApiList", gin.H{})
		})

		// /api/loggedin
		u := a.Group("/loggedin")
		{
			// give login details
			u.GET("/", func(c *gin.Context) {
				if userID := sessions.Default(c).Get("user_id"); userID != nil {
					// cookie auth
					user := c.MustGet("user").(User)
					c.JSON(http.StatusOK, gin.H{
						"authtype": "cookie",
						"userid":   userID,
						"user":     user,
					})
				} else {
					// jwt auth
					user := c.MustGet("user").(User)
					c.JSON(http.StatusOK, gin.H{
						"authtype": "jwt",
						"userid":   user.Sub,
						"user":     user,
					})
				}
			})

			u.GET("/items", items.UGetAllJSON)
			u.POST("/upload", files.UploadHandler)
			u.GET("/upload", files.UGetAllJSON)
		}

		// items
		a.POST("/items", items.Add)
		a.GET("/items", items.GetAllJSON)
		a.GET("/items/:id", items.GetJSON)
		a.DELETE("/items/:id", items.Remove)
		a.PATCH("/items/:id", items.Change)

		// houses
		a.POST("/houses", houses.Add)
		a.GET("/houses", houses.GetAllJSON)
		a.GET("/houses/:id", houses.GetJSON)
		a.DELETE("/houses/:id", houses.Remove)
		a.PATCH("/houses/:id", houses.Change)

		// person
		a.POST("/person", person.Add)
		a.GET("/person", person.GetAllJSON)
		a.GET("/person/:id", person.GetJSON)
		a.DELETE("/person/:id", person.Remove)
		a.PATCH("/person/:id", person.Change)

		// communityadvisor (ca)
		a.POST("/ca", ca.Add)
		a.GET("/ca", ca.GetAllJSON)
		a.GET("/ca/:id", ca.GetJSON)
		a.DELETE("/ca/:id", ca.Remove)
		a.PATCH("/ca/:id", ca.Change)

		// residents
		a.POST("/residents", residents.Add)
		a.GET("/residents", residents.GetAllJSON)
		a.GET("/residents/:id", residents.GetJSON)
		a.DELETE("/residents/:id", residents.Remove)
		a.PATCH("/residents/:id", residents.Change)

		// dorms
		a.POST("/dorms", dorms.Add)
		a.GET("/dorms", dorms.GetAllJSON)
		a.GET("/dorms/:id", dorms.GetJSON)
		a.DELETE("/dorms/:id", dorms.Remove)
		a.PATCH("/dorms/:id", dorms.Change)

		// student
		a.POST("/student", student.Add)
		a.GET("/student", student.GetAllJSON)
		a.GET("/student/:id", student.GetJSON)
		a.DELETE("/student/:id", student.Remove)
		a.PATCH("/student/:id", student.Change)

		// vendor
		a.POST("/vendor", vendor.Add)
		a.GET("/vendor", vendor.GetAllJSON)
		a.GET("/vendor/:id", vendor.GetJSON)
		a.DELETE("/vendor/:id", vendor.Remove)
		a.PATCH("/vendor/:id", vendor.Change)

		// chatusers
		a.GET("/chatusers", chatusers.GetAllJSON)

		// chatmessages (chat)
		a.POST("/chat", chat.Add)
		a.GET("/chat/:id", chat.GetJSON)

		//Admin Functions
		a.GET("/admin", func(c *gin.Context) {
			if err := FailIfNotRequiredAuthGroup(c, AuthCA); err != nil {
				log.Printf("Error: %s\n", err.Error())
				return
			}
			c.HTML(http.StatusOK, "admin", gin.H{
				"itemNameList": createItemList(),
			})
		})

		//Used for Item Uploading now
		a.GET("/adminitems", func(c *gin.Context) {
			if err := FailIfNotRequiredAuthGroup(c, AuthCA); err != nil {
				log.Printf("Error: %s\n", err.Error())
				return
			}
			c.HTML(http.StatusOK, "items", gin.H{
				"itemList": items.GetAllJSON, //Not used. Left for example
			})
		})
	}

	// setup routes for web templates
	// r.GET("/", loginHandler)
	// r.GET("/auth", authHandler)

	// just display something at the homepage
	// TODO: replace with something better
	r.GET("/", func(c *gin.Context) {
		c.HTML(http.StatusOK, "index", gin.H{})
		c.JSON(http.StatusOK, gin.H{"status": http.StatusOK, "time": time.Now()})
	})

	r.GET("/db", func(c *gin.Context) {
		c.HTML(http.StatusOK, "database", gin.H{
			"dbdata": dbGetAll(),
			// "dbtables": dbGetTables(db),
		})
	})

	r.GET("/draft", func(c *gin.Context) {
		c.HTML(http.StatusOK, "draft", gin.H{})
	})

	r.GET("/error", func(c *gin.Context) {
		c.HTML(http.StatusOK, "error", gin.H{})
	})

	// Database List pages
	r.GET("/items", func(c *gin.Context) {
		c.HTML(http.StatusOK, "public_items", gin.H{
			"itemNameList": createItemList(),
		})
	})

	r.GET("/dorms", func(c *gin.Context) {
		c.HTML(http.StatusOK, "public_dorms", gin.H{
			"dormNameList": createDormList(),
		})
	})

	r.GET("/houses", func(c *gin.Context) {
		c.HTML(http.StatusOK, "public_houses", gin.H{
			"houseNameList": createHouseList(),
		})
	})

	r.GET("/vendors", func(c *gin.Context) {
		c.HTML(http.StatusOK, "public_vendors", gin.H{
			"vendorNameList": createVendorList(),
		})
	})

	return r
}

// MAIN FUNCTION ---------------------------------------------------------------
func main() {
	db = Database()
	db.LogMode(true)
	defer db.Close()

	go SocketIO()
	AppEngine().Run(":8080")
}

// -----------------------------------------------------------------------------

// SocketIO to setup socket for chats
func SocketIO() {
	server, err := socketio.NewServer(nil)
	if err != nil {
		log.Fatal(err)
	}
	server.On("connection", func(so socketio.Socket) {
		log.Println("on connection")
		so.Join("chat")
		so.On("chat message", func(msg string) {
			m := make(map[string]interface{})
			m["a"] = "你好"
			e := so.Emit("cn1111", m)
			//这个没有问题
			fmt.Print("\n\n")

			b := make(map[string]string)
			b["u-a"] = "中文内容" //这个不能是中文
			m["b-c"] = b
			e = so.Emit("cn2222", m)
			log.Println(e)

			log.Println("emit:", so.Emit("chat message", msg))
			so.BroadcastTo("chat", "chat message", msg)
		})
		// Socket.io acknowledgement example
		// The return type may vary depending on whether you will return
		// For this example it is "string" type
		so.On("chat message with ack", func(msg string) string {
			return msg
		})
		so.On("disconnection", func() {
			log.Println("on disconnect")
		})
	})
	server.On("error", func(so socketio.Socket, err error) {
		log.Println("error:", err)
	})

	http.Handle("/socket.io/", server)
	http.Handle("/", http.FileServer(http.Dir("./asset")))
	log.Println("Serving at localhost:5000...")
	log.Fatal(http.ListenAndServe(":5000", nil))
}
