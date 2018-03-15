use io
foo(a:int): int {
  if a > 1
    if a == 2
      a = 'a'
    else if a == 3
      a = 'b'
    else
      a = 'c'
  return a
}

main(argv:int[][]) {
    println({foo('z'), foo(2), foo(3), foo(4)})
}
