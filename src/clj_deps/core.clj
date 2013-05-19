
(ns clj-deps.core)

(defmulti fetch-project :source)

(defmethod fetch-project :github
  [project]
  )


(def project
  {:source :github
   :name "rodnaph/diallo"})

