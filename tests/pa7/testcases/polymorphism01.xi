use io
use conv

class A {
    a : int

    inc() {
        a = a + 1
    }

    printA() {
        println(unparseInt(a))
    }
}

class B extends A {
    b : int

    inc() {
        b = b + 1
    }

    printB() {
        println(unparseInt(b))
    }
}

main (argv : int[][]) {
    a : A = new A

    a.a = 5

    a.printA() // 5
    a.inc()

    b : B = new B
    b.a = 10

    b.printA() // 10
    b.inc()
    b.printA() // still 10
    b.printB() // 1

    a = b


}
