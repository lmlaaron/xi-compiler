class A {
    a_ : int
    a() : int { return a_ }
    init() : A {return this}

}

main(argv:int[][]) {
	a : A
	z1 : int = a.a()
	z2 : int = a.a_

	w1 : int = returnA().a()
	w2 : int = returnA().a_

	u1 : int = new A.a()
	u2 : int = a.init().init().init().a()
	u6 : int = new A.init().init().a_

	a.a_ = 3
	new A.a_ = 3
	new A.init().a_ = 3 // not sure if this is valid

	b : A[]
	y1 : int = b[0].a()
	y2 : int = b[0].a_

	v1 : int = returnAarray()[0].a()
	v2 : int = returnAarray()[0].a_

	x1 : int = {new A}[0].a()
	x2 : int = {new A}[0].a_
}

returnA(): A {
	a : A
	return a
}

returnAarray(): A[] {
	a, b : A
	return {a,  b}
}