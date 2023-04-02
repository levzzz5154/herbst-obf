for file in *.c; do
    [ -f "$file" ] || continue
    echo "$file"
    make all $file
done
