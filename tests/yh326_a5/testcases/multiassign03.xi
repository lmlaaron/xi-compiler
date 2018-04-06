use io

test():int[], int[], int[][] {
    return "ret 1", {'r','e','t',' ','2'}, {"ret 3"}
}

main(argv:int[][]) {
    a:int[], b:int[], c:int[][] = test()
    println(a)
    println(b)
    println(c[0])
}
