branch := $(shell git rev-parse --abbrev-ref HEAD)
commit := $(shell git rev-parse --short HEAD)

WINDOWS := -s -w -H=windowsgui -extldflags=-static
# Linux and MacOS
LINUX := -s -w

# using LINUX flags works for me on windows too
LDFLAGS := "${LINUX} -X main.COMMIT=${commit} -X main.BRANCH=${branch}"
FLAGS :=


run:
	$(MAKE) exec ACTION=run

build:
	$(MAKE) exec ACTION=build

exec:
	go $(ACTION) -ldflags $(LDFLAGS) $(FLAGS) . $(ARGS)

