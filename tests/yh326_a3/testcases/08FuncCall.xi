foo(): int {
    return 2
}
bar(a:int): bool {
    return false
}
foobar(a:int, b:bool): bool, int {
    return true, 1
}

barfoo() {
    a:int = 0
    a = foo()
    b:bool
    b = bar()
    b, a = foobar(a,b)
    _, a = foobar(a,b)
    b, _ = foobar(a,b)
    _, _ = foobar(a,b)
    _ = foobar(a,b)
}


