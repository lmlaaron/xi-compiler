use class_a

class B extends A {
    b_ : int
    b() : int { return b_ }
    init() : B {return this}
}

main(argv: int[][]) {
	barray:A[] = {new A, new B, new A.init()}
}