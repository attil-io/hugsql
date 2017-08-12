(ns princess-bride.core
  (:require [princess-bride.db :refer [db]]
            [princess-bride.db.characters :as characters]
            [princess-bride.db.quotes :as quotes]
            [clojure.pprint :as pprint]
            [clojure.string :as string]
            [clojure.java.jdbc]))

;; save some typing
(def pp pprint/pprint)
(defn ppl [x]
  "pretty print w/ extra trailing line"
  (pp x) (println))

(defn ppr
  "pretty print w/ trailing result indicator ;;=>"
  [x]
  (print (string/replace (with-out-str (pp x)) #"\n$" ""))
  (println "  ;;=>"))

(defn ppsv
  "Pretty print an sqlvec"
  [sv]
  (println
    (string/join ""
      ["[\""
       (-> (first sv)
         (string/replace #"\"" "\\\\\"")
         (string/replace #"\n" "\n  "))
       "\""
       (when (seq (rest sv)) "\n,")
       (string/replace
         (string/join ","
           (map #(with-out-str (pp %)) (rest sv)))
         #"\n$"
         "")
       "]\n"])))

(defmacro ex
  "Example macro: Pretty print code, 
   eval, then pretty print result"
  [& code]
  `(do
     (ppr (quote ~@code))
     (ppl ~@code)))

(defmacro exsv
  "Example macro for sqlvec: Pretty print code, 
   eval, then pretty print sqlvec"
  [& code]
  `(do
     (ppr (quote ~@code))
     (ppsv ~@code)))

(defn create-tables []
  (characters/create-characters-table db)
  (quotes/create-quotes-table db))


(defn drop-tables []
  (characters/drop-characters-table db)
  (quotes/drop-quotes-table db))

(defn insert-character [ch-name ch-spec]
  (characters/insert-character db {:name ch-name :specialty ch-spec}))

(defn update-character [ch-name new-ch-spec]
    (let [character (characters/character-by-name db {:name ch-name})]
      (characters/update-character-specialty db {:id (:id character)
                                                 :specialty new-ch-spec})))

(defn delete-character [ch-name]
    (let [character (characters/character-by-name db {:name ch-name})]
      (characters/delete-character-by-id db {:id (:id character)})))


(defn character-by-name [ch-name] 
    (characters/character-by-name db {:name ch-name}))

(defn character-by-name-like [ch-name] 
    (characters/characters-by-name-like db {:name-like ch-name}))

(defn character-by-id [ch-id] 
    (characters/character-by-id db {:id ch-id}))

(defn -main []
  
  (println "\n\"The Princess Bride\" HugSQL Example App\n\n")

  (drop-tables)
  (create-tables)
  (insert-character "geza", "computers")
  (insert-character "gizi", "animals and plants")
  (insert-character "jani", "holes")
  (update-character "jani", "beer")
  (let [octavio (character-by-name "jani")] (println (str "" octavio)))
  (drop-tables)

  (println "\n\nTHE END\n"))

