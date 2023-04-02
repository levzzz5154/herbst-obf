{ pkgs ? import <nixpkgs> {} }:

pkgs.stdenv.mkDerivation {
    name = "java-env";
    buildInputs = [
        pkgs.jdk8
        pkgs.unzip
        pkgs.gcc # i like c
        pkgs.gnumake # and make
    ];
}

