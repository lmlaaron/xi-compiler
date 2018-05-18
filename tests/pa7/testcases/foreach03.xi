// foeach on results of functions
foo(): int[5] {
    a: int[5] = {0, 1, 2, 3, 4}
    return a
}

main(args: int[][]) {
    for x in foo() {
      println(x)
    }
}
