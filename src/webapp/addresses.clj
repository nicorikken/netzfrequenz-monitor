(ns webapp.addresses)

(defonce db
  (atom [{:name "Joost"
          :phone "+31 1234 5678"
          :age 42}]))

(defn add-person
  [people person]
  (conj people person))

(defn add-person!
  [person]
  (swap! db add-person person))

(defn remove-by-name
  [people name]
  (filter #(not= name (:name %)) people))

(defn remove-by-name!
  [name]
  (swap! db remove-by-name name))

;; (swap! db function ...)
