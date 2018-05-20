class A {
	x, y: int
	
	init(): A {
		x = 100
		y = 100
		return this
	}

	initXY(x0: int, y0: int) {
		x = x0
		y = y0
		return this
	}
}

main(argv: int[][]) {
	alist: A[] = {new A, new A, new A}
	a[0].x = 1
	println(unparseInt(a[0].x))
	println(unparseInt(a[0].y))
	a[0].init()
	println(unparseInt(a[0].x))
	println(unparseInt(a[0].y))

	{alist[0], alist[1], alist[2]}[1].initXY(-1, -2)
	{alist[0], alist[1], alist[2]}[2].y = -3
	println(unparseInt(a[1].x))
	println(unparseInt(a[1].y))
	println(unparseInt(a[2].x))
	println(unparseInt(a[2].y))
}