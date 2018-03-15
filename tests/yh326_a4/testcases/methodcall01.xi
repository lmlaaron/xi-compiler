// method call no arg single return
use io
foo():int {
  println("in foo")
  return 9
}
main(argv:int[][]) {
  println({48+foo()})
  println("after foo")
}


// NOTE!!!!!!!!!!!!
// Procedure call is tested in "procedure#.xi"
// Method call with multiple return is tested in "vardeclmult#.xi"
