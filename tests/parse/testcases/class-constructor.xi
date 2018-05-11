class C {
    a : int
    init(x : int) : C {
        // both of these should pass:
        a = x
        this.a = x

        return this
    }
}

makeC(a: int): C { return new C.init(a) }