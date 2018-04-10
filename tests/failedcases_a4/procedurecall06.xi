use io
use conv

	main(args: int[][]) {
	x: int[] = {1, 2, 3}
	p(f(x, 1), f(x, 2), f(x, 3), f(x, 4), f(x, 5), f(x, 6))
}

f(x: int[], y: int): int {
	print("f() called: ")
	println(unparseInt(y))
	x[0] = x[0] + 1
	return x[0]
}

p(x1: int, x2: int, x3: int, x4: int, x5: int, x6: int) {
	print("p() called: ")
	print(unparseInt(x1))
	print(" ")
	print(unparseInt(x2))
	print(" ")
	print(unparseInt(x3))
	print(" ")
	print(unparseInt(x4))
	print(" ")
	print(unparseInt(x5))
	print(" ")
	print(unparseInt(x6))
	println("")
}