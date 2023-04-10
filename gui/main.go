package main

import (
	"strings"

	gui "github.com/AllenDang/giu"
	"github.com/AllenDang/imgui-go"
)

var (
	about = false
	about_md = strings.Join([]string{
		"\n___\n",
		"__" + Version() + "__",
		"\n___\n",
		"Obfuscator for November Client",
	}, "\n")
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
		popup.Size(300, 220)
		popup.Layout(
			gui.Align(gui.AlignCenter).To(
				gui.Label("About").Font(gui.GetDefaultFonts()[0].SetSize(28)),
				gui.Markdown(&about_md).
					Header(0, gui.GetDefaultFonts()[0].SetSize(28), true).
					Header(1, gui.GetDefaultFonts()[0].SetSize(26), true).
					Header(2, nil, true),
			),
		)
	}

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

