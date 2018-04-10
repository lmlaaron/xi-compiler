use io
use conv

main(args: int[][]) {
	a1: int[25][][][][]
	a2: int[47][][][]
	a3: int[20][][]
	a4: int[16][]
	a5: int[42]
	a1[17] = a2
	a1[17][16] = a3
	a1[17][16][15] = a4
	a1[17][16][15][14] = a5
	a1[17][16][15][14][13] = 12
	println(unparseInt(length(a1)))
	println(unparseInt(length(a1[17])))
	println(unparseInt(length(a1[17][16])))
	println(unparseInt(length(a1[17][16][15])))
	println(unparseInt(length(a1[17][16][15][14])))
	println(unparseInt(a1[17][16][15][14][13]))
	println("done")
}