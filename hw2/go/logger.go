package main

import (
	"fmt"
)

func log(msg string) {
	fmt.Println(msg)
}

func logf(format string, args ...any) {
	fmt.Println(fmt.Sprintf(format, args...))
}
