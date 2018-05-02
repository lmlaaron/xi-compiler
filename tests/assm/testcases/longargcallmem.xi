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
x:int[10]
x[0] = 0
x[1] = 1
x[2] = 2
x[3] = 3
x[4] = 4
x[5] = 5
x[6] = 6
x[7] = 7
x[8] = 8
x[9] = 9  
  f0(x[0])
  f1(x[0],x[1])
  f2(x[0],x[1],x[2])
  f3(x[0],x[1],x[2],x[3])
  f4(x[0],x[1],x[2],x[3],x[4])
  f5(x[0],x[1],x[2],x[3],x[4],x[5])
  f6(x[0],x[1],x[2],x[3],x[4],x[5],x[6])
  f7(x[0],x[1],x[2],x[3],x[4],x[5],x[6],x[7])
  f8(x[0],x[1],x[2],x[3],x[4],x[5],x[6],x[7],x[8])
  f9(x[0],x[1],x[2],x[3],x[4],x[5],x[6],x[7],x[8],x[9])
}
