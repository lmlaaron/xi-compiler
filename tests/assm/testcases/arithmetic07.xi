use io
main(argv:int[][]) {
  two_to_32nd : int = 4294967296

  if (two_to_32nd * 2 == 8589934592) {
    println("hooray for >32 bits!")
  }
}