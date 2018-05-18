use io
use conv

main(argv:int[][]) {
    x : bool[5]
    x[3] = true
    printbool(x[0])
    printbool(x[1])
    printbool(x[2])
    printbool(x[3])
    printbool(x[4])
}

printbool(x:bool) {
	if (x == true) {
	  println("x is true")
	}
	else {
	  println("x is false")
	}
}