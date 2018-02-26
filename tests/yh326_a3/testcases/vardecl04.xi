//var decl shadowing in if statement
foo() {
   a:int
   if (true) {
      a:int=1
   }
}
