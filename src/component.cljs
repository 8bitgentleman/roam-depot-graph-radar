(ns radar-v3
  (:require
   [reagent.core :as r]
   [datascript.core :as d]
   [roam.datascript.reactive :as dr]
   [roam.util :as u]
   [blueprintjs.core :as bp]
   [clojure.pprint :as pp]))

(defonce bp-divider (r/adapt-react-class bp/Divider))
(defonce bp-callout (r/adapt-react-class bp/Callout))


(defn get-page
  "search through a block's children for all pages referenced"
  [parents]
  (->> parents
       (map :block/page)
      (map :node/title)
       
        
       ))

(defn get-pages []
  "Gets all pages"
  (flatten @(dr/q '[:find (pull ?e [:edit/time
                                    :node/title
                                    :block/page
                                    {:block/page ...}
                                    ])
                  :where 
                    [?e :edit/time]
                    ]))
  )

(defn radar-page [page-name]
    [:div (u/parse  (str "[[" page-name "]]")  )]        
)

(defn remove-duplicates-and-nil [lst]
  (->> lst
       (distinct)          ; Remove duplicates while preserving order
       (remove nil?)))     ; Remove nil values


(defn main [{:keys [block-uid]} & args]
    (let [radar (r/atom (take (int (last args)) (remove-duplicates-and-nil (get-page (reverse (sort-by :edit/time (get-pages)))))))]
      (pp/pprint (int (first args)))
      [bp-callout {
               :title (first args)
               :intent "primary"
               :icon "time"
               :style {:border "1px solid #106ba3"}
                }
        
        [bp-divider]
            (for [i @radar ]
              (radar-page i))
      ])
  
)


