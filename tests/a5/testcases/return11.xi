// return more than two values
//use conv
use io
foo():int, int,int, int, int{
    return '0'+2, '0'+3, '0'+4, '0'+5, '0'+6
}

main(argv:int[][]) {
    a:int, b:int, c:int, d:int, e:int= foo()
    str:int[5]
    str[0] = '0'+ 2 
    str[1] = '0'+ 3
    str[2] = '0'+ 4
    str[3] = '0'+ 5
    str[4] = '0'+ 6
    println(str)
    //println(unparseInt(a))   
    //println(unparseInt(b))
    //println(unparseInt(c))
    //println(unparseInt(d))
    //println(unparseInt(e))
}
