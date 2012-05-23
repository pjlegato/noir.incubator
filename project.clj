(defproject noir.incubator "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :plugins [[lein-swank "1.4.4"]
            [lein-git-deps "0.0.1-SNAPSHOT"]]

  :dependencies [[org.clojure/clojure "1.3.0"]

                 ;; Logging infrastructure
                 [org.slf4j/slf4j-log4j12 "1.6.4"]
                 [org.clojure/tools.logging "0.2.3"]
                 [clj-logging-config "1.9.6"]
                 [org.clojars.pjlegato/clansi "1.3.0"]
                 
                 ])