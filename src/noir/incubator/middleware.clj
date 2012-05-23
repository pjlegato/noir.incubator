(ns noir.incubator.middleware
  "Helpful middleware functions that are incubating"
  (require
   [noir.incubator.logging :as log-config] 
   [clojure.tools.logging :as log]
   [clansi.core :as ansi]
   ))


;;;
;;; Logging middleware
;;;


;; We must initialize the Log4J backend once per namespace that will
;; be logging things.
(log-config/setup-default-logger!)

(defn- log4j-logger [status totaltime {:keys [request-method uri remote-addr] :as req}]
  "Log4J-enabled logformatter for logging middleware. Sends all log
  messages at \"info\" level to the Log4J logging infrastructure,
  unless status is >= 500, in which case they are sent as errors."
  (let [colorstatus (apply ansi/style
                           (str status)
                           (cond
                            (< status 300) [:grey] 
                            (>= status 500) [:bright :red] 
                            (>= status 400) [:red] 
                            :else [:yellow]))
        log-message (str remote-addr " - "
                         request-method " "
                         uri " "
                         "[Status: " colorstatus "] "
                         " (" totaltime " ms)"
                         ) ]

    (if (>= status 500) ;; TODO: doesn't use info, ever??
      (log/error log-message)
      (log/info log-message))))


(defn wrap-with-logger [handler logger]
  "Adds logging for requests using the given logger. The output shows
   the current time, the request method, uri, and total time spent
   processing the request."
  (fn [request]
    (let [start  (System/currentTimeMillis)
	  resp   (handler request)
	  status (:status resp)
	  finish (System/currentTimeMillis)
	  total  (- finish start)]
      (logger status total request)
      resp)))


(defn wrap-with-logging [handler]
  "Logging middleware; logs information about all requests with a default Log4J handler."
  (wrap-request-logging-with-formatter handler log4j-logger))

;;;
;;; End logging middleware
;;;