(COMPUNIT
 test
 (FUNC
  a
  (SEQ
   (CJUMP 
    (ESEQ (MOVE (TEMP j) (CONST 5)) (TEMP j)) 
    (LABEL a1)
    (LABEL a2)
   )
  )
 )
)
