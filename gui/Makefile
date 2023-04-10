branch := $(shell git rev-parse --abbrev-ref HEAD)
commit := $(shell git rev-parse --short HEAD)

LDFLAGS := "-X main.COMMIT=${commit} -X main.BRANCH=${branch}"
FLAGS :=


run:
	$(MAKE) exec ACTION=run

build:
	$(MAKE) exec ACTION=build

exec:
	go $(ACTION) -ldflags $(LDFLAGS) $(FLAGS) .
	

