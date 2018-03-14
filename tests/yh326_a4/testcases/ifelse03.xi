//If else statement
// check lub function
// should pass type check
foo(): int {
  a: int = 1
  if true {
    return a
  } else if false {
    return a-11
  } else {
    return a-22
  }
}

main(argv:int[][]) {
    _ = foo()
}
