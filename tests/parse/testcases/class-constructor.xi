class C {
    a : int
    init(x : int) : C {
        // both of these should pass:
        a = x
        this.a = x

        return this
    }
}

def makeC(a: int) { return new C.init(a) }