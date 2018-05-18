use class_a

class B extends A {
    b_ : int
    b() : int { return b_ }
    init() : B {return this}
}

main(argv: int[][]) {
	barray1:A[] = {new A, new B, new A.init()}
	barray2:A[] = {new B, new B, new B.init()}
}