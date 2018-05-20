use io
use conv

class A {
    a : int

    inc() {
        a = a + 1
    }

    print() {
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

    a.print() // 5
    a.inc()

    b : B = new B
    b.a = 10

    b.print() // 10
    b.inc()
    b.print() // still 10
    b.printB() // 1

    a = b


}