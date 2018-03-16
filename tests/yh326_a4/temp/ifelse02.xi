use io
foo(a: int):int {
  if a == 1 {
    a = a + 97 - 1
  } else {
    a = 'あ'
  }
  return a
}
main(argv:int[][]) {
  println({foo(1), foo(2)})
}
