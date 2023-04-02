for file in *.cpp; do
    [ -f "$file" ] || continue
    echo "$file"
    make all $file
done
