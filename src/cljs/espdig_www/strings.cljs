(ns espdig-www.strings)

(def lang (atom :en-gb))

(def strings
  {:string/title "eSPOP!"
   :string/subtitle "eSports opinions from the Interwebz"
   :string/search-placeholder {:en-gb "Search"
                               :de-de "Suchen"}
   :string/recent-media "Recent Uploads"
   :string/search-results "Search Results"
   :string/no-matches "No matches"
   :string/data-error "Failed to retrieve application data."})

(defn get-string
  ""
  [keywd & add]
  (if (contains? strings keywd)
    (let [entry (keywd strings)
          phrase (if (map? entry) (@lang entry) entry)]
      (if add
        (clojure.string/join " " (concat [phrase] add))
        phrase))
    (do
      (println "Failed to find string " (str keywd))
      "## ERROR ##")))
