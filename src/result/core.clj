(ns result.core
  "Utilities for defining operation results")

(defn success
  "Creates a successful result"
  ([] {:success true})
  ([data]
    (if (map? data)
      (assoc data :success true)
      {:success true :data data})))

(defn failure
  "Creates a failed result"
  ([] {:success false})
  ([data]
    (if (map? data)
      (assoc data :success false)
      {:success false :data data})))

(defn enforce-empty
  "Succeeds if the given obj is empty"
  [data]
  {:success (empty? data) :data data})

(defn succeeded?
  "True if the given result succeed"
  [result]
  (:success result))

(defn failed?
  "True if the given result failed"
  [result]
  (not (:success result)))

(defn collection-succeeded?
  "Evaluates all the results on the collection"
  [coll]
  (every? #(get % :success) coll))

(defn collection-failed?
  "Evaluates all the results on the collection"
  [coll]
  (not (collection-succeeded? coll)))

(defn presence
  "Returns success or failure based on the param being nil or not"
  [obj]
  (if obj
    (success obj)
    (failure)))

(defn timedout
  "A result the represents a timeout"
  [info]
  (-> (failure info)
      (assoc :timedout true)))

(defn exception
  "A result the represents an exception"
  [ex]
  (-> (failure "Exception")
      (assoc :exception ex)))
