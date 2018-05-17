use io
use conv


foo(): int[], int[] {
  return "bar", "quux"
}

foo_int(): int, int {
  return 1, 2
}

foo2(a: int, b: int, c: int, d: int, e: int, f: int, g: int, h: int ) : 
											int, int, int, int {
	// spillage to stack for args and returns
	println(unparseInt(a))
	println(unparseInt(b))
	println(unparseInt(c))
	println(unparseInt(d))
	println(unparseInt(e))
	println(unparseInt(f))
	println(unparseInt(g))
	println(unparseInt(h))
	return 10, 20, 30, 40
}

main(args: int[][]) {
	x:int, y:int = foo_int()
	println(unparseInt(x))
	println(unparseInt(y))

	_, z:int = foo_int()
	println(unparseInt(z))

	a:int[], b:int[] = foo()
	println(a)
	println(b)
	println("\"So Many returns and arguments that I just can't even\" - Stack 2016")

	d: int, e: int, f: int, g: int = foo2( 1, 2, 3, 4, 5, 6, 7, 8)
	println(unparseInt(d))
	println(unparseInt(e))
	println(unparseInt(f))
	println(unparseInt(g))
}
