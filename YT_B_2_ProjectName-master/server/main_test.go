package main

import (
	"bytes"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"os"
	"testing"

	"github.com/gin-gonic/gin"
)

// Setup Gin in test mode
func TestMain(m *testing.M) {
	gin.SetMode(gin.TestMode)

	db = Database()
	db.LogMode(true)
	defer db.Close()

	os.Exit(m.Run())
}

// Make sure the Gin server starts
func TestGinStartup(t *testing.T) {
	ts := httptest.NewServer(AppEngine())
	defer ts.Close()
}

// Helper function to process a request and test its response
// From: https://semaphoreci.com/community/tutorials/building-go-web-applications-and-microservices-using-gin
func testHTTPResponse(t *testing.T, r *gin.Engine, req *http.Request, f func(w *httptest.ResponseRecorder) bool) {
	// Create a response recorder
	w := httptest.NewRecorder()

	// Create the service and process the above request.
	r.ServeHTTP(w, req)

	if !f(w) {
		t.Fail()
	}
}

// // TODO IMPROVE
// // does not currently work with the api security
// func TestItems(t *testing.T) {
// 	r := AppEngine()
// 	req, _ := http.NewRequest("GET", "/api/items?page=1&limit=2", nil)
// 	testHTTPResponse(t, r, req, func(w *httptest.ResponseRecorder) bool {
// 		var ret bool
// 		p, err := ioutil.ReadAll(w.Body)
//
// 		type jsonData struct {
// 			Items   []Items `json:"data"`
// 			Status  int     `json:"status"`
// 			Message string  `json:"message"`
// 			Page    int     `json:"page"`
// 			Limit   int     `json:"limit"`
// 			Offset  int     `json:"offset"`
// 		}
//
// 		var jd jsonData
//
// 		if err2 := json.Unmarshal(p, &jd); err2 != nil {
// 			t.Error(err)
// 		}
//
// 		//pretty print for debugging
// 		if j, errj := PrettyJSON(jd); errj == nil {
// 			fmt.Println(j)
// 		}
//
// 		ret = jd.Status == w.Code && err == nil
//
// 		// if jd.Status == http.StatusOK {
// 		// 	fmt.Println(jd.Data)
// 		// 	ret = ret && len(jd.Data) > 0
// 		// } else {
// 		// 	fmt.Println(jd.Message)
// 		// 	ret = ret && len(jd.Message) > 0
// 		// }
//
// 		return ret
// 	})
// }

// PrettyJSON will pretty print JSON data
// From: https://stackoverflow.com/a/44359967
func PrettyJSON(data interface{}) (string, error) {
	buffer := new(bytes.Buffer)
	encoder := json.NewEncoder(buffer)
	encoder.SetIndent("", "\t")

	err := encoder.Encode(data)
	if err != nil {
		return "", err
	}
	return buffer.String(), nil
}
