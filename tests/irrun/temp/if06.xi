use io
foo():bool {return true}
main(argv:int[][]) {
  if foo() {
     println("true")
  }
  if (foo()) {
     println("true")
  }
}
