#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>


#define unused __attribute__((unused))
#define max(x, y) (x > y ? x : y)


const int gkey = 0x0d;


void* encrypt(void* in, int len) {
    unsigned char *buf = (unsigned char*)in,
                  *out = malloc(len);

    int key = (len * len) << gkey;
    uint8_t k1 = (key >> 24) & 0xff,
            k2 = (key >> 16) & 0xff,
            k3 = (key >>  8) & 0xff,
            k4 = key & 0xff;

    for (int i=0; i < len; i++) {
        char tmp = buf[i] ^ k1;
        tmp = (tmp ^ k2) ^ k3;
        tmp ^= k4;


        out[i] = tmp;
    }

    return (void*)out;
}


int main(unused int argc, unused char *argv[]) {
    char* str = "Hello World!";

    printf("Original: %s\n", str);

    char* enc = (char*) encrypt((void*) str, strlen(str));
    printf("Encrypted: %s\n", enc);
    char* dec = (char*) encrypt((void*) enc, strlen(str));
    printf("Decrypted: %s\n", dec);

    return 0;
}

