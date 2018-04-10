use io
main(argv:int[][]) {
  two_to_32nd : int = 4294967296

  if (8589934592 / 2 == 4294967296) {
    println("success 1")
  }
  if (-8589934592 / 2 == -4294967296) {
    println("success 2")
  }
}