use io

foo() {
    a:int = 3
}

main(argv:int[][]) {
    a:int = 4
    foo()
    print({a+48})
}