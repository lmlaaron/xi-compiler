use io
use conv

main(args: int[][]) {
	i: int = 0
	j: int = 0
	k: int = 0; l: int = 0
	while (i < 50000000) {
		i=i+ 1
		if (j > 100000000) {j = 0}
		else {j = 2 + 3*(i + 1) + 10}
		if (k > 100000000) {k = 0}
		else {k = 2 + 3*(i + 1) + 6 + j + i}
		if (l > 100000000) {l = 0}
		else {l = k + 2 + 3*(5 + i + 1) + 6 + j + i}
	}
	println(unparseInt(i))
	println(unparseInt(j)); println(unparseInt(k)); println(unparseInt(l))
}

// -Odce 3 seconds
// -Odce -Ocse 2 seconds