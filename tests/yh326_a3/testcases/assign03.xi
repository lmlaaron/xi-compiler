//non assignable lhs
bar(): int {
    return 1
}
foo() {
    bar() = a
}
