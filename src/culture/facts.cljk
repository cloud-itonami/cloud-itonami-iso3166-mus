(ns culture.facts
  "Country-level regional-culture catalog for Mauritius (MUS) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"MUS"
   [{:culture/id "mus.dish.dalpuri"
     :culture/name "Dalpuri"
     :culture/name-local "Dholl puri"
     :culture/country "MUS"
     :culture/kind :dish
     :culture/summary "Cooked flatbread dish stuffed with yellow split peas, served with pea curry and tomato sauce, per the Mauritian cuisine article."
     :culture/url "https://en.wikipedia.org/wiki/Mauritian_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mus.dish.gato-piment"
     :culture/name "Gato Piment"
     :culture/name-local "Gateau piment"
     :culture/country "MUS"
     :culture/kind :dish
     :culture/summary "Chilli fritters made of split peas combined with chilli, per the Mauritian cuisine article."
     :culture/url "https://en.wikipedia.org/wiki/Mauritian_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mus.dish.rougail"
     :culture/name "Rougail"
     :culture/country "MUS"
     :culture/kind :dish
     :culture/summary "Tomato sauce cooked with onions, garlic, chillies, ginger and spices, typically eaten with fish, meat and vegetables, per the Mauritian cuisine article."
     :culture/url "https://en.wikipedia.org/wiki/Mauritian_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mus.beverage.green-island-rum"
     :culture/name "Green Island Rum"
     :culture/country "MUS"
     :culture/kind :beverage
     :culture/summary "Rum locally manufactured in Mauritius, usually mixed with cold Sprite and a piece of lemon, per the Mauritian cuisine article."
     :culture/url "https://en.wikipedia.org/wiki/Mauritian_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mus.beverage.alouda"
     :culture/name "Alouda"
     :culture/country "MUS"
     :culture/kind :beverage
     :culture/summary "Sweet, cold Mauritian beverage made with milk, tukmaria (basil seeds) and slices of coloured agar-agar jelly, per the Mauritian cuisine article."
     :culture/url "https://en.wikipedia.org/wiki/Mauritian_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mus.heritage.aapravasi-ghat"
     :culture/name "Aapravasi Ghat"
     :culture/country "MUS"
     :culture/kind :heritage
     :culture/summary "Immigration depot in Port Louis, Mauritius, where about half a million Indian indentured labourers passed through between 1849 and 1923; designated a UNESCO World Heritage Site in 2006."
     :culture/url "https://en.wikipedia.org/wiki/Aapravasi_Ghat"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mus.heritage.le-morne-brabant"
     :culture/name "Le Morne Brabant"
     :culture/country "MUS"
     :culture/kind :heritage
     :culture/summary "Peninsula and basaltic monolith at the southwestern tip of Mauritius, inscribed on the UNESCO World Heritage List in 2008 for its cultural significance related to slavery and maroon history."
     :culture/url "https://en.wikipedia.org/wiki/Le_Morne_Brabant"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-mus culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "MUS"))
                 " MUS entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
