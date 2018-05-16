class SuperClass {
	a:int
	b:int
	c() {}
}

globalInt:int
class SubClass extends SuperClass {
	d:int
	c() { s:int; globalInt = 2;   s=this.d  }
	e() {}
}

makeTest(super:SuperClass, sub:SubClass) {
}

