use io
foo():int {
  {
    {
      {
        {
          if true
            {return 4}
          else
            {return 5}
        }
      }
    }
  }
}
main(argv:int[][]) {
   println({48+foo()})
}


// Basic grammar is tested in basically all files
// In stmt#.xi, only test corner cases
