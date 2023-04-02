for file in *.cpp; do
    [ -f "$file" ] || continue
    echo "$file"
    make cpp $file EXT=cpp STD=c++20
done
