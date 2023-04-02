#include <stdio.h>

#define unused __attribute__((unused))

int main(unused int argc, unused char *argv[]) {
    printf("Hello World!\n");

    return 0;
}

