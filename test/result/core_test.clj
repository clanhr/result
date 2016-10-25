(ns result.core-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [<!!]]
            [result.core :as result]))

(deftest success
  (let [data {:dummy "dummy"}
        result (result/success data)]
    (is (:success result))))

(deftest failure
  (let [data {:dummy "dummy"}
        result (result/failure data)]
    (is (not (:success result)))))

(deftest enforce-empty
  (testing "empty"
    (let [result (result/enforce-empty {})]
      (is (:success result))))

  (testing "not empty"
    (let [result (result/enforce-empty {:dummy "dummy"})]
      (is (not (:success result))))))

(deftest succeeded?
  (testing "succeeded"
    (let [data {:dummy "dummy"}
          result (result/success data)]
      (is (result/succeeded? result))))

  (testing "failed"
    (let [data {:dummy "dummy"}
          result (result/failure data)]
      (is (not (result/succeeded? result))))))

(deftest failed?
  (testing "failed"
    (let [data {:dummy "dummy"}
          result (result/failure data)]
      (is (result/failed? result))))

  (testing "succeeded"
    (let [data {:dummy "dummy"}
          result (result/success data)]
      (is (not (result/failed? result))))))

(deftest collection-succeeded?
  (testing "succeeded"
    (let [data {:dummy "dummy"}
          results [(result/success data) (result/success data)]]
      (is (result/collection-succeeded? results))))

  (testing "failed"
    (let [data {:dummy "dummy"}
          results [(result/failure data) (result/success data)]]
      (is (not (result/collection-succeeded? results))))))

(deftest collection-failed?
  (let [data {:dummy "dummy"}
        results [(result/failure data) (result/failure data)]]
    (is (result/collection-failed? results))))

(deftest result-timedout
  (let [data {:dummy "dummy"}
        result (result/timedout "postgres")]
    (is (result/failed? result))))

(deftest result-presence
  (let [specific-message "Specific message"
        result (result/presence nil specific-message)]
    (is (result/failed? result))
    (is (= specific-message (:data result)))))

(deftest result-has-value
  (testing "has value"
    (let [result (result/as-object {:foo "foo"})]
      (is (result/has-value? result))))
  (testing "hasn't value"
    (let [result (result/as-object nil)]
      (is (not (result/has-value? result))))))

(deftest result-unauthorised
  (let [result (result/unauthorised)]
    (is (result/unauthorised? result))
    (is (result/failed? result))))

(deftest result-forbidden
  (let [result (result/forbidden)]
    (is (result/forbidden? result))
    (is (result/failed? result))))

(deftest result-payment-required
  (let [result (result/payment-required)]
    (is (result/payment-required? result))
    (is (result/failed? result))))

(deftest result-created
  (let [ data {:dummy "dummy"}
        result (result/created data)]
    (is (result/created? result))
    (is (result/succeeded? result))))

(deftest if-let-test
  (result/if-let [r (result/success)]
    (do
      (is (result/succeeded? r))
      (println r))
      (is false))
  (result/if-let [result (result/failure)]
    (is (result/failed? result))
    (is true)))

(deftest on-success
  (is (result/succeeded?
    (result/on-success [result (result/success)]
      (println "This should be displayed")
      (is true)
      (is (result/succeeded? result))
      (result/success))))

  (testing "last expr is returned"
    (is (result/failed?
      (result/on-success [result (result/success)]
        (result/failure)))))

  (is (result/failed?
    (result/on-success [result (result/failure)]
      (println "This should *NOT* be displayed")
      (is false)
      (result/success)))))

(deftest enforce-let
  (testing "simple scenario"
    (is (result/succeeded?
      (result/enforce-let [r1 (result/success)]
        (result/success)))))

  (testing "simple tree"
    (is (result/succeeded?
      (result/enforce-let [r1 (result/success)
                           r2 (result/success)
                           r3 (result/success)]
        (result/succeeded? r1)
        (result/succeeded? r2)
        (result/succeeded? r3)
        r3))))

  (testing "No body"
    (is (result/failed?
      (result/enforce-let [r1 (result/failure "First failure")
                           r2 (result/success)
                           r3 (result/success)]
        (is false)))))

  (testing "no body failure"
    (let [result (result/enforce-let [r1 (result/success)
                                      r2 (result/success)
                                      r3 (result/failure {:last true})])]
      (is (result/failed? result))
      (is (:last result))))

  (testing "no body success"
    (let [result (result/enforce-let [r1 (result/success)
                                      r2 (result/success)
                                      r3 (result/success {:last true})])]
      (is (result/succeeded? result))
      (is (:last result)))))

(deftest async-enforce-let
  (is (result/succeeded?
    (<!! (result/async-enforce-let [r1 (result/success)]
           (result/success))))))

(deftest async-on-success
  (is (result/succeeded?
    (<!! (result/async-on-success [r1 (result/success)]
           (result/success))))))
