(ns webapp.core
  (:require [hiccup.page :as page :refer [include-css]]
            [hiccup.core :refer [h]]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clj-http.client :as client]
            [overtone.at-at :as at-at]))

(def netzfreq-url "http://www.netzfrequenz.info/json/aktuell.json")

(def netzfreq-stub (-> "dev-resources/netzfrequenz.txt"
                       (clojure.java.io/reader)
                       (cheshire.core/parsed-seq true)
                       (atom)))

(def call-ps (volatile! nil))
(def ps-pool (at-at/mk-pool))
(def freqs   (atom []))

(defn html-body
  [title & contents]
  (page/html5
   [:head
    [:title (h title)]
    (include-css "https://fonts.googleapis.com/css?family=Roboto:300,300italic,700,700italic")
    (include-css "https://cdnjs.cloudflare.com/ajax/libs/normalize/3.0.3/normalize.css")
    (include-css "assets/milligram.min.css")]
   (into [:body] contents)))

(defn page
  [title & contents]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (apply html-body title contents)})

;;; /byebye

(defn byebye
  [request]
  (page "A webapp"
        [:h1 "Goodbye!"]
        [:p "Byebye"]
        [:p [:a {:href "/"} "take me back"]]))

; (defn- calling []
;   (at-at/every 1000 #(println "hallo") ps-pool :fixed-delay true))
;
; (defn start-calling
;   ([] vswap! call-ps start-calling)
;   ([ps]
;    (at-at/stop @ps)
;    (calling)))

; (defn start-calling []
;   (at-at/stop @ps)
;   ())

(defn scheduled-fn []
  (swap! freqs conj (call-netzfreq)))
  ; (println "I am cool!"))

; (defn- calling []
;   (at-at/every 1000 #(println "I am cool!") ps-pool :fixed-delay true))

(defn- calling []
  (at-at/every 1000 #'scheduled-fn ps-pool :fixed-delay true))


(defn- stop-ps [ps]
  (when ps (at-at/stop ps))
  nil)

(defn stop-calling []
  (vswap! call-ps stop-ps))

; (defn start-calling []
;   (vswap! call-ps #((at-at/stop %) (calling))))

(defn start-calling []
  (vswap! call-ps #(do (stop-ps %) (calling))))

;; API call functions
(defn call-netzfreq []
  (:body (clj-http.client/get netzfreq-url {:as :json})))

(defn call-netzfreq-stub []
  (let [v (first @netzfreq-stub)]
    (vswap! netzfreq-stub rest)
    v))

(defn standard
  [request]
  (page "A webapp"
        (prn request)
        [:h1 (str (call-netzfreq))]
        [:p "This is the standard response page"]
        [:p [:a {:href "/byebye"} "Bye!"]]))

(defroutes handler
  (GET "/" request
       (standard request))
  (GET "/byebye" request
       (byebye request)))

(def handler-with-middleware
  (-> handler
      (wrap-defaults site-defaults)))
