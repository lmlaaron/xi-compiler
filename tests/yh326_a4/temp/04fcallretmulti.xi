use io

retmulti() : int, int, int {
    return 1,2,3
}

main(argv:int[][]) {
    a:int, b:int, c:int = retmulti()
    print({a+48})
    print({b+48})
    print({c+48})
}