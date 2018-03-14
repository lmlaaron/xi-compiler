//sequential statements

foo(): int {
  bar()
  return 1
}
bar() {
  return
}

main(argv:int[][]) {
   _ = foo()
}
