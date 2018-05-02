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
  f0(0)
  f1(0,1)
  f2(0,1,2)
  f3(0,1,2,3)
  f4(0,1,2,3,4)
  f5(0,1,2,3,4,5)
  f6(0,1,2,3,4,5,6)
  f7(0,1,2,3,4,5,6,7)
  f8(0,1,2,3,4,5,6,7,8)
  f9(0,1,2,3,4,5,6,7,8,9)
}
