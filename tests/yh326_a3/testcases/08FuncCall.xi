foo(): int {
    return 2
}
bar(x:int): bool {
    return false
}
foobar(y:int, z:bool): bool, int {
    return true, 1
}

barfoo() {
    a:int = 0
    a = foo()
    b:bool
    b = bar(a)
    b':bool, a':int = foobar(a,b)
    _, a'':int = foobar(a,b)
    b'':bool, _ = foobar(a,b)
    _, _ = foobar(a,b)
    // The following will be invalid, but tested in "methodcall10.xi"
    //_ = foobar(a,b)
}
