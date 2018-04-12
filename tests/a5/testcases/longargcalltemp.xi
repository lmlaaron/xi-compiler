// procedure multiple args
// to test the calling convention
use io
use conv
f0(a0:int) {
  println(unparseInt(a0))
}
f1(a0: int, a1:int) {
  println(unparseInt(a0+a1))
}
f2(a0: int, a1:int, a2:int) {
  println(unparseInt(a0+a1+a2))
}
f3(a0:int, a1:int, a2:int, a3:int) {
  println(unparseInt(a0+a1+a2+a3))
}
f4(a0:int, a1:int, a2:int, a3:int, a4:int) {
  println(unparseInt(a0+a1+a2+a3+a4))
}
f5(a0:int, a1:int, a2:int, a3:int, a4:int, a5:int) {
  println(unparseInt(a0+a1+a2+a3+a4+a5))
}
f6(a0:int, a1:int, a2:int, a3:int, a4:int, a5:int, a6:int) {
  println(unparseInt(a0+a1+a2+a3+a4+a5+a6))
}
f7(a0:int, a1:int, a2:int, a3:int, a4:int, a5:int, a6:int, a7:int) {
  println(unparseInt(a0+a1+a2+a3+a4+a5+a6+a7))
}
f8(a0:int, a1:int, a2:int, a3:int, a4:int, a5:int, a6:int, a7:int, a8:int) {
  println(unparseInt(a0+a1+a2+a3+a4+a5+a6+a7+a8))
}
f9(a0:int, a1:int, a2:int, a3:int, a4:int, a5:int, a6:int, a7:int, a8:int, a9:int) {
  println(unparseInt(a0+a1+a2+a3+a4+a5+a6+a7+a8+a9))
}
main(argv:int[][]) {
x0:int = 0
x1:int = 1
x2:int = 2
x3:int = 3
x4:int = 4
x5:int = 5
x6:int = 6
x7:int = 7
x8:int = 8
x9:int = 9  
  f0(x0)
  f1(x0,x1)
  f2(x0,x1,x2)
  f3(x0,x1,x2,x3)
  f4(x0,x1,x2,x3,x4)
  f5(x0,x1,x2,x3,x4,x5)
  f6(x0,x1,x2,x3,x4,x5,x6)
  f7(x0,x1,x2,x3,x4,x5,x6,x7)
  f8(x0,x1,x2,x3,x4,x5,x6,x7,x8)
  f9(x0,x1,x2,x3,x4,x5,x6,x7,x8,x9)
}
