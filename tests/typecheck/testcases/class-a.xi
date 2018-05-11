use class_a

class A {
    a_ : int
    a() : int { return a_ }
    init() : A {return this}
}

makeA(a_ : int): A {
    a : A = new A.init()
    a.a_ = a_
    return a
}