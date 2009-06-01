;Nothing to see here :) Just trying to learn Clojure using Stuart Halloway's book and kodkod using Emina Torlak's kodkod examples on my flight to NYC. This whole thing took three hours, including time spent reading Programming Clojure, so this is probably non-idiomatic clojure and inefficient use of kodkod. 

(ns pigeon
  (:import (kodkod.ast Formula Relation Variable)
	   (kodkod.engine Solution Solver)
		    (kodkod.engine.satlab SATFactory)
				   (kodkod.instance Bounds TupleFactory TupleSet Universe)))

(defn find-solution [num-holes num-pigeons]
  (def Pigeon (. Relation unary "Pigeon"))
  (def Hole (. Relation unary "Hole"))
  (def nest (. Relation binary "nest"))

  (def declarations (. nest function Pigeon Hole))
  
  (def p1 (. Variable unary "p1"))
  (def p2 (. Variable unary "p2"))
  
  (def atoms (concat (for [i (range 0 num-holes)] (format "Hole%d" i)) (for [i (range 0 num-pigeons)] (format "Pigeon%d" i))))
  (def uni (new Universe atoms))
  (def fac (. uni factory))
  (def hbound (. fac range (. fac tuple 1 0) (. fac tuple 1 (- num-holes 1))))
  (def pbound (. fac range (. fac tuple 1 num-holes) (. fac tuple 1 (+ num-holes num-pigeons -1))))
  (def allbounds (new Bounds uni))
  (def solver (new Solver))
  
  (. allbounds boundExactly Pigeon pbound)
  (. allbounds boundExactly Hole hbound)
  (. allbounds bound nest (. pbound product hbound))
  
  (def constraint 
       (. (. (. (. p1 eq p2) not) 
			      implies (. (. (. p1 join nest) intersection (. p2 join nest)) no)) 
						  forAll (. (. p1 oneOf Pigeon) and (. p2 oneOf Pigeon))))
  (. solver solve (. declarations and constraint) allbounds)
)
