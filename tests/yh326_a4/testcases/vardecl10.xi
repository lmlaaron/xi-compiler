//array declaration
use io
main(argv:int[][]) {
    i1:int = 4
    i2:int = 4
    i3:int = 4
    a:int[i1][i2][i3][][]
    a = {{{{"word"},{"vardecl"},{"third"},{"4th"},{"No. 5", "also 5"}}}}
    a[0][0][4][0][4] = '6'
    println(a[0][0][4][0])
    a[0][0][3] = {"changed"}
    println(a[0][0][3][0])
}
