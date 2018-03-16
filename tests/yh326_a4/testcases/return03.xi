//return expression with variables
foo():int {
    a:int
    return a*2
}

main(argv:int[][]) {
    a:int = foo()
}
