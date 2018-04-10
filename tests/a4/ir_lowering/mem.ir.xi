(COMPUNIT
 test
 (FUNC
  a
  (SEQ
   (MOVE 
    (TEMP i) 
    (MEM 
     (ESEQ 
      (MOVE (TEMP j) (CONST 5)) 
      (TEMP j)
     )
    )
   ) 
  )
 )
)
