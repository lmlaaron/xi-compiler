use io
use conv

class A {
    a : int

    incA() {
        a = a + 1
    }

    printA() {
        println(unparseInt(a))
    }
}

class B extends A{
    a : int

    incA() {
        a = a + 1
    }

    printAlternative() {
        println(unparseInt(a))
    }
}

main (argv : int[][]) {
    a : A = new A
    a.a = 5

    a.printA() // 5
    a.incA()

    b : B = new B
    b.a = 10

    b.printA() // 0
    b.incA()
    b.printA() // still 0

    b.printAlternative() // 11

    a = b


}