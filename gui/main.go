package main

import (
	"fmt"
)

var BRANCH, COMMIT string

var (
	version = "0.1.0"

)

func Version() {
	fmt.Printf("Herbst GUI v%s (%s/%s)\n", version, BRANCH, COMMIT)
}


func main() {
	Version()
}

