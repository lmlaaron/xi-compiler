foo() {
  i: int

  ai1: int[] = {0, 1, 2,}
  i = (ai1 + {0})[0]

  aai1: int[][] = {{0}, ai1}
  i = (aai1[0] + ai1)[1]
}