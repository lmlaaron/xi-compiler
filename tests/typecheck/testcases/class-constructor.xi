class C {
    a : int
    init(x : int) : C {
        // both of these should pass:
        a = x
        x=this.a

        return this
    }
}

makeC(a: int): C { return new C.init(a) }
