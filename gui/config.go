package main

import (
	"herbst/gui/errors"
	"os"

	"gopkg.in/yaml.v3"
)

type Config struct {
	// Config struct {
	// 	Input        string `yaml:"input"`
	// 	Output       string `yaml:"output"`
	// 	Transformers struct {
	// 		Renamers struct {
	// 			Dictionary string `yaml:"dictionary"`
	// 			Length     int    `yaml:"length"`
	// 			Classes    bool   `yaml:"classes"`
	// 			Fields     bool   `yaml:"fields"`
	// 			Methods    bool   `yaml:"methods"`
	// 		} `yaml:"renamers"`
	// 	} `yaml:"transformers"`
	// }

	Map map[interface{}]interface{}
}

func (c* Config) Parse(file string) {
	contents, err := os.ReadFile(file)
	errors.Handle(true, err)

	// err = yaml.Unmarshal(contents, &c.Config)
	// errors.Handle(true, err)

	m := make(map[interface{}]interface{})
	err = yaml.Unmarshal(contents, &m)
	c.Map = m
	errors.Handle(true, err)
}

func (c* Config) ToString() string {
	d, err := yaml.Marshal(&c.Map)
	errors.Handle(true, err)

	return string(d)
}

func (c* Config) Write(file string) {
	err := os.WriteFile(file, []byte(c.ToString()), 0655)
	errors.Handle(true, err)
}

