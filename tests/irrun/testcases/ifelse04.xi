use io
foo(a:int): int {
  if a > 1
    if a == 2
      a = 'a'
    else if a == 3
      a = 'b'
    else
      a = 'c'
  else
    a = 'd'
  return a
}

main(argv:int[][]) {
    println({foo(1), foo(2), foo(3), foo(4)})
}
