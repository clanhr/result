(ns result.core-test
  (:require [clojure.test :refer :all]
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

(deftest presence
  (let [result-ok (result/presence {:hello "world"})
        result-fail (result/presence nil)]
    (is (result/succeeded? result-ok))
    (is (result/failed? result-fail))))
