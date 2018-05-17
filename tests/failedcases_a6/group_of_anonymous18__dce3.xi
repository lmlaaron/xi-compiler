// dce test3: should remove only y:int = x since f() produces side effects

use io
use conv

main(args:int[][]) {
	x:int = f()
	y:int = x
}

f():int {
	println("17")
	return 7
}
