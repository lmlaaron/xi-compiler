use io

bar ():int {
    println("printed in bar")
    return 10000
}

main(argv:int[][]) {
    a:int
    a = bar()
    println({a/100})
}
