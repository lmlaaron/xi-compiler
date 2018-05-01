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
x:int[2]
x[0] = 0
x[1] = 1
 
  f0(x[1])
  f1(x[0],x[1])
  f2(x[0],x[0],x[1])
  f3(x[0],x[0],x[0],x[1])
  f4(x[0],x[0],x[0],x[0],x[1])
  f5(x[0],x[0],x[0],x[0],x[0],x[1])
  f6(x[0],x[0],x[0],x[0],x[0],x[0],x[1])
  f7(x[0],x[0],x[0],x[0],x[0],x[0],x[0],x[1])
  f8(x[0],x[0],x[0],x[0],x[0],x[0],x[0],x[0],x[1])
  f9(x[0],x[0],x[0],x[0],x[0],x[0],x[0],x[0],x[0],x[1])
}
