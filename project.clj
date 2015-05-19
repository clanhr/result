(defproject clanhr/result "0.3.0"
  :description "Generic result representation"
  :url "https://github.com/clanhr/result"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-beta3"]]
  :profiles {:1.7 {:dependencies [[org.clojure/clojure "1.7.0-beta3"]]}}
  :aliases {"all" ["with-profile" "dev:1.7" "test"]})
