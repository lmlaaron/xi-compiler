# XI compiler

https://www.cs.cornell.edu/courses/cs4120/2018sp/project/language.pdf

Xi programs consist of a single source file containing definitions of one or more functions. Execution of a program consists of evaluating a call to the function main.
The language has two primitive types: integers (int) and booleans (bool). The array type T[] exists for any type T, so T[][] represents an array of arrays.
Functions may return a value, but need not. A function that does not return a value is called a procedure. A function may take multiple arguments. Unlike in languages such as C and Java, a function may also return multiple results.
Statement and expression forms should largely be familiar to C programmers.
There is no string type, but the type int[] may be used for most of the same purposes. Literal string constants have this type.
