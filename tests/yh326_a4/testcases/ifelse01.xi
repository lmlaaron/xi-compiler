use io
foo(a:int):int {
  if a == 1 {
    return a + 97 - 1
  } else {
    return 'あ'
  }
}
main(argv:int[][]) {
  println({foo(1), foo(2)})
}
