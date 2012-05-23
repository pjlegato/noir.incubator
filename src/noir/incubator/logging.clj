(ns noir.incubator.logging
  "Logging infrastructure"
  (require
   [clj-logging-config.log4j :as log-config]))

;; TODO: rename according to development/testing/production mode
(def base-log-name "logs/noir.log")

;; The generation of the calling class, line numbers, etc. is
;; extremely slow, and should be used only in development mode or for
;; debugging. Production code should not log that information if
;; performance is an issue. See
;; http://logging.apache.org/log4j/companions/extras/apidocs/org/apache/log4j/EnhancedPatternLayout.html
;; for information on which fields are slow.
;;
(def debugging-log-prefix-format "%d %l [%p] : %throwable%m%n")
(def production-log-prefix-format "%d [%p] : %throwable%m%n")


;; Some basic logging adapters.
;; Many other logging infrastructures are possible. There are syslog
;; adapters, network socket adapters, etc.
;; For more information, see:
;; - https://github.com/malcolmsparks/clj-logging-config
;; - http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/AppenderSkeleton.html for different log destinations
;; - http://logging.apache.org/log4j/companions/extras/apidocs/org/apache/log4j/EnhancedPatternLayout.html for prefix formatting options
;;
;; TODO: Make a custom layout class that colorizes the log level. Maybe this can be done in a filter.
;;
(def rotating-logger
  "This logging adapter rotates the logfile nightly at about midnight."
  (org.apache.log4j.DailyRollingFileAppender.
   (org.apache.log4j.EnhancedPatternLayout. production-log-prefix-format)
   base-log-name
   ".yyyy-MM-dd"))

(def appending-logger
  "This logging adapter simply appends new log lines to the existing logfile."
  (org.apache.log4j.FileAppender.
   (org.apache.log4j.EnhancedPatternLayout. production-log-prefix-format)
   base-log-name
   true))

;; TODO: Auto-detect production vs. development mode, and reduce the log level in production mode.
;; TODO: Provide hooks for the user's Noir application to select the desired logging adapter.
(defn setup-default-logger!
  "Initializes the logger for the current namespace. This must be
  called once per namespace that plans to log things."
  []
  (log-config/set-logger! (str *ns*)
                          :level :debug
                          :out appending-logger))

