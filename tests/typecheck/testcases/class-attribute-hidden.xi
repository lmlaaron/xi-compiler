use class_a

use io

main(argv:int[][]) {
    a : A = makeA(1)
    b:int = a.a_
    // ^^^ shouldn't be legal -- class attributes only accessible in
    // the file the class is defined in
}