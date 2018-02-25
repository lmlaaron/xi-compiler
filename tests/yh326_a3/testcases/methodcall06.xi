// Call method with multiple results, wrong assignment
foo():int, bool {return '\x67',false}
bar() {a:int[], b:bool=foo()}