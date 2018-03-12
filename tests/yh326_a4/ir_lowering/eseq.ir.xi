(COMPUNIT
 test
 (FUNC
  a
  (SEQ
   (MOVE
    (TEMP i)
    (ESEQ 
     (MOVE (TEMP k) (TEMP j)) 
     (ESEQ
      (MOVE (TEMP j) (TEMP i))
      (TEMP k)
     )
    )
   ) 
  )
 )
)
