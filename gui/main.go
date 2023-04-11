package main

import (
	"fmt"
	"os"
	"strings"

	"herbst/gui/errors"

	gui "github.com/AllenDang/giu"
	"github.com/AllenDang/imgui-go"
)

var (
	split float32 = 200
	about         = false

	options []string
	option	string

	editor *gui.CodeEditorWidget
	list   *gui.ListBoxWidget
	config Config
)

func MenuBar() *gui.MenuBarWidget {
	return gui.MenuBar().Layout(
		gui.Menu("File").Layout(),
		gui.Menu("Edit").Layout(),
		gui.Menu("Help").Layout(
			gui.MenuItem("About").OnClick(func() {
				about = true
			}),
		),
	)
}

func View() []gui.Widget {
	if about {
		popup := gui.Window("About")
		popup.Size(290, 140)
		popup.Layout(
			gui.Align(gui.AlignCenter).To(
				gui.Label("About").Font(gui.GetDefaultFonts()[0].SetSize(28)),
				gui.Separator(),
				gui.Label(Version()),
				gui.Separator(),
				gui.Label("Obfuscator for November Client"),
			),
		)
		about = popup.HasFocus()
	}

	editor.Text(config.ToString())

	viewport := []gui.Widget{
		gui.TabBar().TabItems(
			gui.TabItem("Visual").Layout(
				gui.SplitLayout(gui.DirectionHorizontal, split, gui.Layout{
					list,
				}, gui.Layout{
					gui.Label(option),
				}),
			),
			gui.TabItem("Code").Layout(
				editor,
			),
		),
	}

	return append([]gui.Widget{MenuBar()}, viewport...)
}

func main() {
	if len(os.Args) < 2 {
		path := strings.Split(strings.ReplaceAll(os.Args[0], "\\", "/"), "/")
		errors.HandleF(true, "Usage: %s <config file>", path[len(path)-1])
	}


	config.Parse(os.Args[1])
	for k := range config.Map {
		options = append(options, strings.Title(fmt.Sprintf("%s", k)))
	}


	option = options[0]
	list = gui.ListBox("Config Options", options)
	list.OnChange(func(selectedIndex int) {
		option = options[selectedIndex]
	})
	
	editor = gui.CodeEditor().
		ShowWhitespaces(true).
		TabSize(4).
		Border(true)


	window := gui.NewMasterWindow(Version(), 1280, 720, 0)
	window.Run(func() {
		imgui.ShowDemoWindow(nil)

		gui.SingleWindowWithMenuBar().Layout(
			View()...,
		)
	})
}

