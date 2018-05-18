class A {
    a_ : int
    a() : int { return a_ }
    init() : A {return this}
}

f() {
	a:A
	b:bool = a == null
	b = null == a
	b = new A == null
	b = null == new A
	b = new A.init() == null
}