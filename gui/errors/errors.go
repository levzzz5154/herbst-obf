package errors

import (
	"fmt"
	"os"
)

func HandleF(fatal bool, format string, args... interface{}) {
	fmt.Printf(format + "\n", args...)

	if fatal {
		os.Exit(1)
	}
}

func Handle(fatal bool, err error) {
	if err != nil {
		HandleF(fatal, "[Error] %s", err)
	}
}

