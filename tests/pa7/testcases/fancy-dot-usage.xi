use io
use conv

class A {
	x, y: int
	
	init(): A {
		x = 100
		y = 100
		return this
	}

	initXY(x0: int, y0: int): A {
		x = x0
		y = y0
		return this
	}
}

main(argv: int[][]) {
	alist: A[] = {new A, new A, new A}
	alist[0].x = 1
	println(unparseInt(alist[0].x))
	println(unparseInt(alist[0].y))
	_ = alist[0].init()
	println(unparseInt(alist[0].x))
	println(unparseInt(alist[0].y))

	alist[1] = {alist[0], alist[1], alist[2]}[1].initXY(-1, -2)
	alist[2].y = {alist[0], alist[1], alist[2]}[2].y - 3
	println(unparseInt(alist[1].x))
	println(unparseInt(alist[1].y))
	println(unparseInt(alist[2].x))
	println(unparseInt(alist[2].y))
}