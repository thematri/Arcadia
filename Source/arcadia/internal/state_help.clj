(ns arcadia.internal.state-help
  (:require arcadia.literals)
  (:import [UnityEngine Debug]
           ArcadiaState))

(defn better-merge [m1 m2]
  (if (empty? m1)
    m2
    (persistent!
      (reduce-kv assoc! (transient m1) m2))))

(defn awake [^ArcadiaState as]
  (let [state (.state as)]
    (.BuildDatabaseAtom as true)
    (let [objdb (.objectDatabase as)]
      (binding [arcadia.literals/*object-db* objdb
                *data-readers* (better-merge *data-readers*
 arcadia.literals/the-bucket)]
        (try
          ;; new atom ensures clones made via .instantiate don't share the same atom
          (set! (.state as)
            (atom (read-string (.edn as))))
          (catch Exception e
            (Debug/Log "Exception encountered in ArcadiaState.Awake:")
            (Debug/Log e)
            (Debug/Log "arcadia.literals/*object-db*:")
            (Debug/Log arcadia.literals/*object-db*)
            (throw e)))))))
