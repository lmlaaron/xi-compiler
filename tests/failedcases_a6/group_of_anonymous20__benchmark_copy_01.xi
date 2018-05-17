use io
use conv

main(args: int[][]) {
	n:int = 30000000; i:int = 0; x:int; y:int; z:int; w:int; a:int; b:int; c:int
	while(i < n){
		x = i; y = x; z = y; w = z; a = w; b = a; c = b; x = c;
		x = i; y = x; z = y; w = z; a = w; b = a; c = b; x = c;
		x = i; y = x; z = y; w = z; a = w; b = a; c = b; x = c;
		x = i; y = x; z = y; w = z; a = w; b = a; c = b; x = c;
		x = i; y = x; z = y; w = z; a = w; b = a; c = b; x = c;
		x = i; y = x; z = y; w = z; a = w; b = a; c = b; x = c;
		x = i; y = x; z = y; w = z; a = w; b = a; c = b; x = c;
		x = i; y = x; z = y; w = z; a = w; b = a; c = b; x = c;
		x = i; y = x; z = y; w = z; a = w; b = a; c = b; x = c;
		x = i; y = x; z = y; w = z; a = w; b = a; c = b; x = c;
		x = i; y = x; z = y; w = z; a = w; b = a; c = b; x = c; i = i + 1;
		if(x > n - 2){ println(unparseInt(x)); }
	}
}
// Expected 29999999