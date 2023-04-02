for file in *.c; do
    [ -f "$file" ] || continue
    echo "$file"
    make c $file EXT=c STD=c2x
    ./main
done
