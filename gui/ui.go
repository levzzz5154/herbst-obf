package main

import (
	"fmt"
	"os"
	"strings"

	gui "github.com/AllenDang/giu"
)

var (
	split float32 = 200
	about         = false

	options []string
	option  string

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

func About() {
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
}

func Tab_Visual() gui.Layout {
	return gui.Layout{
		gui.SplitLayout(gui.DirectionHorizontal, split, gui.Layout{
			list,
		}, gui.Layout{
			gui.Label(option),
		}),
	}
}

func Tab_Code() gui.Layout {
	// TODO: let the user change the contents of the file
	//		 and actually show the file, not just the marshal of the config struct
	editor.Text(config.ToString())

	return gui.Layout{editor}
}

func View() []gui.Widget {
	About()

	viewport := []gui.Widget{MenuBar(),
		gui.TabBar().TabItems(
			gui.TabItem("Visual").Layout(Tab_Visual()),
			gui.TabItem("Code").Layout(Tab_Code()),
		),
	}

	return viewport
}

func InitUi() {
	config.Parse(os.Args[1])

	for k := range config.Map {
		options = append(options, strings.Title(fmt.Sprintf("%s", k)))
	}

	option = options[0]
	list = gui.ListBox("Config Options", options)
	list.OnChange(func(idx int) {
		option = options[idx]
	})

	editor = gui.CodeEditor().
		ShowWhitespaces(false).
		TabSize(2)
}

