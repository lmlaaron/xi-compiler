// return more than two values
use conv
use io
foo():int, int,int, int, int{
    return 2, 3, 4, 5, 6
}

main(argv:int[][]) {
    a:int, b:int, c:int, d:int, e:int= foo()
    println(unparseInt(a))   
    println(unparseInt(b))
    println(unparseInt(c))
    println(unparseInt(d))
    println(unparseInt(e))
}
