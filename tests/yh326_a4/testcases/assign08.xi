use io

bar ():int {
    println("printed in bar")
    return -10000
}

main(argv:int[][]) {
    _ = bar()
}
