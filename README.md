# ClanHR's Result Library [![Build Status](https://app.travis-ci.com/clanhr/result.svg?branch=master)](https://app.travis-ci.com/clanhr/result)

[![Clojars Project](http://clojars.org/clanhr/result/latest-version.svg)](http://clojars.org/clanhr/result)

This component represents a way to represent function return values, with a success/failure semantic. Also has several useful macros for dealing with functions that use this component. Example of usage:

```clojure
(defn foo [] (result/success {:some-data "Hello"}))
(defn notgood [] (result/failure "Not good"))

(if (result/succeeded? foo)
  (println "ok")
  (println "nok"))
```

The following macros will only run the *body* if the results succeed. If any result fails, that result will be the value of the expression.

```clojure
(result/if-let [r1 foo]
  (println "ok")
  (println "nok"))

(result/on-success [r1 foo]
  (println "ok"))

(result/enforce-let [r1 notgood
                     r2 foo])
  (println "notgoof will be returned"))

(result/enforce-let [r1 notgood
                     r2 foo
                     r2 (result/success)])
```
