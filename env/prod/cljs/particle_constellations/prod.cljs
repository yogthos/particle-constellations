(ns particle-constellations.prod
  (:require
    [particle-constellations.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
