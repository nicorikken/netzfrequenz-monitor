(ns webapp.core
  (:require [hiccup.page :as page :refer [include-css]]
            [hiccup.core :refer [h]]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

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

(defn standard
  [request]
  (page "A webapp"
        (prn request)
        [:h1 "Hello!"]
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
