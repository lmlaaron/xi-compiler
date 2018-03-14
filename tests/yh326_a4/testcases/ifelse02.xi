//If else statement
// check lub function
// should pass type check
foo(): int {
  a: int = 1
  if true {
    return a
  } else {
    return a - 1
  }
}

main(argv:int[][]) {
  _ = foo()
}
