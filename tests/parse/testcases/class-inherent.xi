class SuperClass {
	a:int
	b:int
	c() {}
}

class SubClass extends SuperClass {
	d:int
	c() { s:int; s=this.d  }
	e() {}
}

makeTest(super:SuperClass, sub:SubClass) {
	s:int = 0
	s= super.a
	s=super.b
	super.c()

	s=sub.a
	s=sub.b 
	sub.c()
	s=sub.d 
	sub.e()
}
