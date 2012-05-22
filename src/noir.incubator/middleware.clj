(ns noir.incubator.middleware
  "Helpful middleware functions that are incubating"
  (require
   [noir.incubator.logging :as logging] ;; to ensure that logging is configured
   [clojure.tools.logging :as log]))


;;;
;;; Logging middleware
;;;

(defn wrap-request-logging-with-formatter [handler formatter]
  "Adds simple logging for requests.  The output shows the current time, the request method, uri,
   and total time spent processing the request."
  (fn [{:keys [request-method uri] :as req}]
    (let [start  (System/currentTimeMillis)
	  resp   (handler req)
	  status (:status resp)
	  finish (System/currentTimeMillis)
	  total  (- finish start)]
      (formatter request-method status uri total)
      resp)))

(defn- logformatter [reqmeth status uri totaltime]
  "Basic logformatter for logging middleware. Writes all log messages to stdout."
  (let [line (format "[%s] %s [Status: %s] %s (%dms)" (java.util.Date.)  reqmeth status uri totaltime)]
    (locking System/out (println line))))

(defn- log4j-formatter [reqmeth status uri totaltime]
  "Log4J-enabled logformatter for logging middleware. Sends all log
  messages at \"info\" level to the Log4J logging infrastructure."
  (let [line (format "%s [Status: %s] %s (%dms)" reqmeth status uri totaltime)]
    (log/info line)))

(defn wrap-request-logging [handler]
  "Provide a default loger with a default logformatter"
  (wrap-request-logging-with-formatter handler logformatter))

;;;
;;; End logging middleware
;;;