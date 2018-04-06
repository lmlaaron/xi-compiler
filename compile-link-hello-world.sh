echo "compile hello.xi\n"
echo "./xic -libpath lib/runtime/include/ lib/runtime/examples/hello.xi\n"
./xic -libpath lib/runtime/include/ lib/runtime/examples/hello.xi
echo "hello.s generated in lib/runtime/examples/hello.s\n"
echo "linking hello.s\n"
echo " lib/runtime/linkxi.sh lib/runtime/examples/hello.s -o helloi\n"
 lib/runtime/linkxi.sh lib/runtime/examples/hello.s -o hello
echo "executable hello generated."

