use io
use conv

class X {
    a : int
    getA() : int { return a }

    initx(b : int) : X {
        a = b
        return this
    }

    printa1() {
        println(unparseInt(a))
    }
}

class Y extends X{
    //a : int

    inc() {
        a = a + 1
    }

    printa2() {
        println(unparseInt(a))
    }

    inity(x : int, y : int) : Y {
        _ = initx(x)
        a = y
        return this
    }
}

init_y(a : int, b : int) : Y {
    return new Y.inity(a, b)
}


main(argv:int[][]){
    x : X = new X.initx(0)
    y : Y = new Y.inity(1, 1)
    y.inc()

    x.printa1()
    y.printa1()
    y.printa2()
}
