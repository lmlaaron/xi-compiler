asser(c:bool) { if (!c) { x:int = 1/0; } }
f(x:int, y:int, d1:int, d2:int, d3:int, d4:int, d5:int, a:int[][]):int {
  a[0][0] = a[0][0] + x + d5
  return a[0][0] + x + d5 + x*d5 + x*x*d5 + x*d5*d5 + x*x*x + d5*d5*d5
}
main(args:int[][]) {
  b:int[][] = {{1}, {2}}
  a:int[f(2, 9, 9, 9, 9, 9, 3, b)]
       [f(3, 9, 9, 9, 9, 9, 1, b)]
       [f(1, 9, 9, 9, 9, 9, 1, b)]
  i:int = 0
  while (i < length(a)) { j:int = 0 while (j < length(a[i])) { k:int = 0
    while (k < length(a[i][j])) {
      a[i][j][k] = 2 * i + 13 * j + 7 * k
      k = k + 1 } j = j + 1 } i = i + 1 }
  count:int = 0 i = 0
  while (i < length(a)) { j:int = 0 while (j < length(a[i])) { k:int = 0
    while (k < length(a[i][j])) { count = count + a[i][j][k] k = k + 1 }
      j = j + 1 } i = i + 1 }
  asser(count == 45113448) }