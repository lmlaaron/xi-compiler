//sequential statements
// should be type error
foo(): int {
  bar()
  return 1
}
bar(): int {
  return 1
}

