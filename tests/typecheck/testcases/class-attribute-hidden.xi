use a

use io

main(argv:int[][]) {
    a : A = makeA(1)
    a.a_
    // ^^^ shouldn't be legal -- class attributes only accessible in
    // the file the class is defined in
}