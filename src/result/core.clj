(ns result.core
  "Utilities for defining operation results"
  (:refer-clojure :exclude [if-let])
  (:require [clojure.core.async :refer [go]]))

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

(defn collection-result
  "Evaluates all the results on the collection and returns a new result based on that"
  ([coll]
   (collection-result coll nil))
  ([coll error-message]
   (if (collection-succeeded? coll)
     (success coll)
     (failure error-message))))

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

(defn has-value?
  "Returns success or failure based on the result having object or not"
  [result]
  (boolean (:has-value result)))

(defn presence
  "Returns success or failure based on the param being nil or not. Also
  handles exceptions"
  ([obj]
   (presence obj nil))
  ([obj failed-msg]
   (cond
     (instance? Throwable obj) (exception obj)
     obj (success obj)
     :else (failure (or failed-msg "Empty data")))))

(defn as-object
  "Same as presence and add a has-value flag"
  ([obj]
   (as-object obj nil))
  ([obj failed-msg]
   (let [result (presence obj failed-msg)]
     (cond-> result
       (succeeded? result) (assoc :has-value true)))))

(defn unauthorised
  "A result the represents an unauthorised operation"
  []
  (-> (failure "Unauthorised")
      (assoc :unauthorised true)))

(defn unauthorised?
  "Checks if the given result is unauthorised"
  [result]
  (boolean (:unauthorised result)))

(defn forbidden
  "A result the represents an forbidden operation"
  []
  (-> (failure "Forbidden")
      (assoc :forbidden true)))

(defn forbidden?
  "Checks if the given result is forbidden"
  [result]
  (boolean (:forbidden result)))

(defn payment-required
  "A result the represents a payment required operation"
  []
  (-> (failure "PaymentRequired")
      (assoc :payment-required true)))

(defn payment-required?
  "Checks if the given result is a payment required"
  [result]
  (boolean (:payment-required result)))

(defn created
  "A result the represents an created operation"
  [data]
  (-> (success data)
      (assoc :created true)))

(defn created?
  "Checks if the given result is a created"
  [result]
  (boolean (:created result)))

(defmacro if-let
  "Mimics if-let but checks if a result succeeded"
  ([bindings then]
   `(if-let ~bindings ~then nil))
  ([bindings then else]
   (let [form (bindings 0) tst (bindings 1)]
     `(let [temp# ~tst]
        (if (succeeded? temp#)
          (let [~form temp#]
            ~then)
          ~else)))))

(defmacro on-success
  "Given a result, if the result succeeds, runs and returns the body.
  It not, the failed result is returned."
  [bindings & body]
   (let [form (bindings 0) tst (bindings 1)]
    `(let [temp# ~tst]
       (if (succeeded? temp#)
         (let [~form temp#]
           ~@body)
         temp#))))

(defmacro enforce-let
  "Gathers all results, checking each one in order if failed. If one fails,
  it short circuits the flow, won't call the next ones and returns the
  failing result."
  [bindings & body]
  (if (empty? bindings)
    `(do ~@body)
    (let [form (bindings 0) tst (bindings 1)]
      `(on-success [~form ~tst]
        (or (enforce-let ~(into [] (drop 2 bindings)) ~@body)
            ~form)))))

(defmacro async-on-success
  "returns a channel with on-success result"
  [bindings & body]
  `(go (on-success ~bindings ~@body)))

(defmacro async-enforce-let
  "returns a channel with enforce-let result"
  [bindings & body]
  `(go (enforce-let ~bindings ~@body)))
