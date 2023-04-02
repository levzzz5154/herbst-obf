CC     := gcc
STD    := c2x
CFLAGS := -std=$(STD) -g -Wpedantic -Wall -Wextra -Werror

CXX 	 := g++
CXXFLAGS := $(CFLAGS)

# use .c files by default
EXT       := c
src_files := $(wildcard *.$(EXT))
objects   := $(src_files:%.$(EXT)=%.o)


all:
	@if [ "$(suffix $(MAKECMDGOALS))" == ".c" ]; then \
		$(MAKE) EXT=c STD=c2x c; \
	elif [ "$(suffix $(MAKECMDGOALS))" == ".cpp"]; then \
		$(MAKE) EXT=cpp STD=c++20 cpp; \
	else \
		echo "Unknown/Unsupported extesion '$(suffix $(MAKECMDGOALS))'"; \
		exit 1; \
	fi
	@echo ""
	@echo " [ Running ] "
	@echo ""
	./main
	@echo ""


c: $(objects)
	$(CC) $(CFLAGS) $(objects) -o main
cpp: $(objects)
	$(CXX) $(CXXFLAGS) $(objects) -o main
.PHONY: c, cpp

%.o: %.c
	$(CC) $(CFLAGS) -c $<
%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $<

clean:
	rm *.o
	rm *.exe

