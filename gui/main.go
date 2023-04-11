package main

import (
	"os"
	"strings"

	"herbst/gui/errors"

	gui "github.com/AllenDang/giu"
	"github.com/AllenDang/imgui-go"
)

var (
	editor *gui.CodeEditorWidget
	split  float32 = 200
	about          = false
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

	viewport := []gui.Widget{
		gui.TabBar().TabItems(
			gui.TabItem("Code").Layout(
				editor,
			),
			gui.TabItem("Visual").Layout(
				gui.SplitLayout(gui.DirectionHorizontal, split, gui.Layout{
					gui.Label("Hello"),
				}, gui.Layout{
					gui.Label("World"),
				}),
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

	configFileContents, err := os.ReadFile(os.Args[1])
	if err != nil { errors.Handle(true, err) }
	editor = gui.CodeEditor().
		ShowWhitespaces(true).
		TabSize(4).
		Border(true).
		Text(string(configFileContents))

	window := gui.NewMasterWindow(Version(), 1280, 720, 0)
	window.Run(func() {
		imgui.ShowDemoWindow(nil)

		// gui.PushColorWindowBg(color.RGBA{0x21, 0x21, 0x21, 0xff})
		// gui.PushStyleColor(gui.StyleColorMenuBarBg, color.RGBA{0x21, 0x21, 0x21, 0xff})
		// gui.PushStyleColor(gui.StyleColorBorder, color.RGBA{0x12, 0x12, 0x12, 0xff})
		gui.SingleWindowWithMenuBar().Layout(
			View()...,
		)
		// gui.PopStyleColor()
		// gui.PopStyleColor()
		// gui.PopStyleColor()
	})
}

