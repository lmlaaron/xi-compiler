use io
main(argv:int[][])
{
    array:int[][][] = {{{}}}
    b:int = length(array) + 48
    c:int = length(array[0][0]) + 48
    println({b, ',', ' ', c})
}
