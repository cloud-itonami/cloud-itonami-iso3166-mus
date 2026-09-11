(ns marketentry.facts
  "Mauritius (MUS) market-entry catalog.

  Every fact under \"MUS\" below is grounded ONLY in a verified research
  dossier gathered by web search against `.govmu.org` government
  domains. No regulatory claim, requirement, or institutional detail
  beyond that dossier is stated here -- the same discipline
  `marketentry.governor`'s `spec-basis-violations` enforces
  downstream.

    - Procurement regulator: PPO -- Procurement Policy Office, 8th
      Floor, Emmanuel Anquetil Building, Port Louis, Mauritius.
      https://ppo.govmu.org/
    - Procurement law: Public Procurement Act 2006 (Act 33 of 2006).
      Consolidated text:
      https://ppo.govmu.org/Documents/PPA/Public%20Procurement%20Act21012025.pdf
      An earlier/original text is also at
      https://ppo.govmu.org/SiteApplication/documents/PPA2006.pdf
    - National e-procurement: since 28 September 2015 the PPO operates
      an e-Procurement System where public-procurement activities are
      carried out digitally; suppliers, contractors and consultants
      are advised to register on this system, as traditional
      (paper-based) procurement is being gradually phased out.
      Public-facing portal: https://publicprocurement.govmu.org/
    - Business/commercial registration authority: the Corporate and
      Business Registration Department (CBRD), Ground Floor, 7
      Exchange Square, Wall Street, Ebene, Republic of Mauritius.
      Registration is done online via the Corporate and Business
      Registration Integrated System (CBRIS). Successful registration
      yields a Certificate of Incorporation and a Business
      Registration Number (BRN).
    - Company formation requirements: at least one director and one
      shareholder required; at least one director must be a Mauritius
      resident; a registered office address in Mauritius is mandatory;
      a unique company name approved by the Registrar of Companies is
      required; a corporate bank account in Mauritius is mandatory.

  What this catalog deliberately does NOT claim: the dossier gives no
  page URL for the CBRD/CBRIS site itself (only its physical address,
  the online-registration channel's name, and its outputs -- a
  Certificate of Incorporation and a BRN), so `:rep-provenance` and
  `:corporate-number-provenance` below state that honestly rather than
  inventing a CBRD/CBRIS URL. The dossier also gives no evidence of a
  distinct national tax-ID scheme (e.g. a VAT/TAN number issued by the
  Mauritius Revenue Authority) separate from CBRIS company
  registration -- so, unlike `AGO`'s NIF check, this catalog does NOT
  add a separate tax-ID sub-map; the CBRIS/BRN facts already cover
  Mauritius's own company-existence record, and the second HARD check
  this actor's governor adds instead verifies PPO e-Procurement System
  registration (the dossier's OTHER explicitly-mandatory registration
  requirement)."
  )

(def catalog
  {"MUS" {:name "Mauritius"
          :owner-authority "PPO — Procurement Policy Office"
          :legal-basis "Public Procurement Act 2006 (Act 33 of 2006)"
          :national-spec "PPO e-Procurement System (mandatory registration for suppliers/contractors/consultants since 28 September 2015; paper-based procurement being gradually phased out) — publicprocurement.govmu.org"
          :provenance "https://ppo.govmu.org/"
          :required-evidence ["CBRIS company registration record (Certificate of Incorporation + Business Registration Number)"
                              "PPO e-Procurement System registration record"
                              "Mauritius-resident-director record"
                              "Authorized-representative record"]
          :rep-owner-authority "Registrar of Companies / Corporate and Business Registration Department (CBRD)"
          :rep-legal-basis "Company formation requires at least one director and one shareholder; at least one director must be a Mauritius resident; a registered office address in Mauritius is mandatory"
          :rep-provenance "Corporate and Business Registration Department, Ground Floor, 7 Exchange Square, Wall Street, Ebene, Republic of Mauritius — no specific webpage URL captured in this actor's verified research dossier for the CBRD site itself"
          :corporate-number-owner-authority "Corporate and Business Registration Department (CBRD), via the Corporate and Business Registration Integrated System (CBRIS)"
          :corporate-number-legal-basis "Online registration via CBRIS yields a Certificate of Incorporation and a Business Registration Number (BRN); a unique company name approved by the Registrar of Companies and a corporate bank account in Mauritius are also mandatory"
          :corporate-number-provenance "Corporate and Business Registration Department, Ground Floor, 7 Exchange Square, Wall Street, Ebene, Republic of Mauritius — no specific webpage URL captured in this actor's verified research dossier for the CBRD/CBRIS site itself"}
   ;; -- reference jurisdiction, reused verbatim from already-merged
   ;; sibling repos (cloud-itonami-iso3166-ago et al.), not a new claim --
   "USA" {:name "United States" :owner-authority "GSA/SAM.gov" :legal-basis "FAR" :national-spec "SAM.gov" :provenance "https://sam.gov/"
          :required-evidence ["EIN record" "SAM.gov registration record" "State business registration record" "SAM UEI verification record"]}})

(defn spec-basis [iso3] (get catalog iso3))
(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s) missing (remove catalog iso3s)]
     {:requested (count iso3s) :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note "R0 catalog seed"})))
(defn required-evidence-satisfied? [iso3 submitted]
  (when-let [{:keys [required-evidence]} (spec-basis iso3)]
    (= (count required-evidence) (count (filter (set submitted) required-evidence)))))
(defn evidence-checklist [iso3] (:required-evidence (spec-basis iso3) []))
(defn rep-spec-basis [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:rep-owner-authority sb)
      (select-keys sb [:rep-owner-authority :rep-legal-basis :rep-provenance]))))
(defn corporate-number-spec-basis [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:corporate-number-owner-authority sb)
      (select-keys sb [:corporate-number-owner-authority :corporate-number-legal-basis :corporate-number-provenance]))))
