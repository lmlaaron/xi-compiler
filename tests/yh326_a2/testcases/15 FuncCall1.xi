one() : int {
    return 1
}

add(a : int, b : int) : int {
    return a + b
}

foo() {
    return add(one(), one())
}