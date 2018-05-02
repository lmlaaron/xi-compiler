(COMPUNIT
 test
 (FUNC
  a
  (RETURN
   (MUL (CONST 3) (TEMP i))
   (ESEQ (MOVE (TEMP i) (CONST 4)) (TEMP i))
   (CONST 5)
  )
 )
)
