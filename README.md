# Xi compiler

Xi is an imperative procedural programming language, like C, with some basic object-oriented features. An example program of Xi can be found [pointexample.xi](tests/pa7/testcases/pointexample.xi). We build an compiler that generates x86 executable binaries from Xi. The main language features can be found at https://www.cs.cornell.edu/courses/cs4120/2018sp/project/language.pdf.

## Build the compiler 

The build process has been tested on Ubuntu 16.04. For other platforms the dependencies may need to be resolved manually.

```
sudo apt install jflex openjdk-8-*
```

We use a build script to build the xi compiler

```
./xic-build
```

## Run the compiler

To run the xi compiler, we can type

```
./xic /path/to/the/name/of_xi_file.xi
```

which will generate the assembly file in the same path. To generate the executable, we use the following commands

```
runtime/linkxi.sh /path/to/the/name/of_assm.s
``` 

which will generate the executable in the current path. We can then run the executable by


```
./a.out
```

For example, we can use the following command to compile, link and execute the pointexample.xi file.

```
$./xic tests/pa7/testcases/pointexample.xi
$runtime/linkxi.sh tests/pa7/testcases/pointexample.s
$./a.out
 ```

We sould be able to see the following in the prompt

```
Point at x = 100 and y = 200
Point at x = 100 and y = 200
Point at x = 105 and y = 205
ColoredPoint at x = 1000 and y = 2000 and Color with r = 255 and g = 128 and b = 0
ColoredPoint at x = 1000 and y = 2000 and Color with r = 255 and g = 128 and b = 0
ColoredPoint at x = 1005 and y = 2005 and Color with r = 255 and g = 128 and b = 0
Point at x = 1110 and y = 2210
Point at x = 1105 and y = 2205
```

