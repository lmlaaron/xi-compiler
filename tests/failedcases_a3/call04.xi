foo(): int, bool, int[] {
  i: int
  i = f() + f()
  i = f() - f()
  i = f() * f()
  i = f() *>> f()
  i = f() / f()
  i = f() % f()
  i = -f()
  i = length(h())

  b: bool
  b = f() == f()
  b = f() != f()
  b = f() < f()
  b = f() <= f()
  b = f() > f()
  b = f() >= f()
  b = g() == g()
  b = g() != g()
  b = g() & g()
  b = g() | g()
  b = !g()
  b = h() == h()
  b = h() != h()

  a: int[]
  a = {f()}
  a = h() + h()

  if (g()) p(f(), g(), h())
  while (g()) p(f(), g(), h())
  return f(), g(), h()
}

f(): int {
  return 1
}

g(): bool {
  return true
}

h(): int[] {
  return ""
}

p(i: int, b: bool, a: int[]) {
}