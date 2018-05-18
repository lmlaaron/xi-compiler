// foeach on results of functions
foo(): int[] {
    a: int[] = {0, 1, 2, 3, 4}
    return a
}

main(args: int[][]) {
    for x in foo() {
      println(x)
    }
}
