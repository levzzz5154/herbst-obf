package main

import (
	"os"
	"strings"

	"herbst/gui/errors"

	gui "github.com/AllenDang/giu"
	"github.com/AllenDang/imgui-go"
)

func main() {
	if len(os.Args) < 2 {
		path := strings.Split(strings.ReplaceAll(os.Args[0], "\\", "/"), "/")
		errors.HandleF(true, "Usage: %s <config file>", path[len(path)-1])
	}

	InitUi()

	window := gui.NewMasterWindow(Version(), 1280, 720, 0)
	window.Run(func() {
		imgui.ShowDemoWindow(nil)

		gui.SingleWindowWithMenuBar().Layout(
			View()...,
		)
	})
}

