package main

import (
	"fmt"
)


var (
	version = "0.1.0"
	BRANCH, COMMIT string
)

func Version() string {
	return fmt.Sprintf("Herbst GUI v%s (%s/%s)", version, BRANCH, COMMIT)
}

