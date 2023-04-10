package main

import (
	gui "github.com/AllenDang/giu"
	"github.com/AllenDang/imgui-go"
)


func MenuBar() *gui.MenuBarWidget {
	return gui.MenuBar().Layout(
		gui.Menu("File").Layout(),
		gui.Menu("Edit").Layout(),
		gui.Menu("Help").Layout(
			gui.MenuItem("About").OnClick(func() {
			}),
		),
	)
}

func View() []gui.Widget {
	viewport := []gui.Widget {
	}

	return append([]gui.Widget { MenuBar() }, viewport...)
}


func main() {
	window := gui.NewMasterWindow(Version(), 1280, 720, 0)
	window.Run(func() {
		imgui.ShowDemoWindow(nil)

		gui.SingleWindowWithMenuBar().Layout(
			View()...,
		)
	})
}

