(defproject clanhr/result "0.2.0"
  :description "FIXME: write description"
  :url "https://github.com/clanhr/result/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-beta2"]]
  :profiles {:1.7 {:dependencies [[org.clojure/clojure "1.7.0-beta2"]]}}
  :aliases {"all" ["with-profile" "dev:1.7" "test"]})
