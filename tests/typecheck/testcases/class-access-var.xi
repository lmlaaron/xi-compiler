use io
use conv
class C {
  a:int
  initC():C {
    return this;
  }
}

main(args:int[][]) {
  c:C=  new C.initC()
  c.a = 2
  println(unparseInt(c.a))
}
