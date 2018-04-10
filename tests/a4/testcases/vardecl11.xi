use io
foo():int[] { return {1,1,1,1,1,2,2,2}}
main(argv:int[][]) {
    long_array : int[foo()[0]][foo()[1]][foo()[2]][foo()[3]][foo()[4]][foo()[5]][1]
    long_array[0][0][0][0][0][0][0] = '!'
    long_array[0][0][0][0][0][1][0] = '?'
    println(long_array[0][0][0][0][0][0])
}
