(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Mauritian public-procurement law, whether a CBRIS-registered
  Mauritian company is actually on file, whether a claimed engagement
  fee actually equals base + months x rate, whether the engagement's
  PPO e-Procurement System registration has been independently
  verified for a filing that requires it, or when a draft stops being
  a draft and becomes a real-world portal submission, so this MUST be
  a separate system able to *reject* a proposal and fall back to HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints; this is the MUS iso3166
  running implementation of that governor, mirroring cloud-itonami-
  iso3166-ago).

  This blueprint's own text (docs/business-model.md Trust Controls:
  'any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off'; 'a false or fabricated regulatory-requirement claim
  is a HARD hold') names exactly the checks below.

  Seven checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them. The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one?
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file?
    3. CBRIS registration missing  -- for `:filing/submit`, when the
                                       engagement declares
                                       `:requires-cbris-registration?
                                       true`, INDEPENDENTLY verify
                                       `:has-cbris-registration?` is
                                       true. FLAGSHIP genuinely new
                                       check for the iso3166 family
                                       (grep-verified absent as a
                                       governor check function name
                                       fleet-wide at build time).
                                       Grounded in the dossier: online
                                       registration via CBRIS (the
                                       Corporate and Business
                                       Registration Integrated System,
                                       administered by the Corporate
                                       and Business Registration
                                       Department) yields a Certificate
                                       of Incorporation and a Business
                                       Registration Number (BRN) --
                                       Mauritius's own
                                       company-existence record.
    4. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    5. e-Procurement registration
       unverified                  -- for `:filing/submit`, when the
                                       engagement declares
                                       `:requires-eprocurement-
                                       registration? true`,
                                       INDEPENDENTLY check
                                       `:eprocurement-registration-
                                       verified?`. CONDITIONAL on the
                                       engagement's own ground truth.
                                       Grounded in the dossier: since
                                       28 September 2015 the PPO
                                       operates an e-Procurement
                                       System where public-procurement
                                       activities are carried out
                                       digitally; suppliers,
                                       contractors and consultants are
                                       advised to register on this
                                       system, as traditional
                                       (paper-based) procurement is
                                       being gradually phased out.
                                       Deliberately distinct from
                                       check 3: check 3 asks whether
                                       the operator's Mauritian
                                       COMPANY exists (CBRIS/BRN),
                                       this check asks whether that
                                       operator has actually
                                       registered on the e-Procurement
                                       PORTAL it must transact
                                       through. This is Mauritius's
                                       corporate-number-equivalent
                                       check: the dossier documents no
                                       separate national tax-ID scheme
                                       distinct from CBRIS company
                                       registration, so this actor
                                       verifies e-Procurement System
                                       registration (rather than
                                       inventing a distinct tax-ID
                                       check) as the required second
                                       hard check.
    6. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real portal package and submitting a real portal
  registration are the two real-world actuation events this actor
  performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence must actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(CBRIS登録/e-Procurement登録/居住取締役/代理人確認等)が充足していない状態での提案"}]))))

(defn- cbris-registration-missing-violations
  "For `:filing/submit`, when the engagement declares
  `:requires-cbris-registration? true`, INDEPENDENTLY verify
  `:has-cbris-registration?` is true -- the flagship genuinely new
  check this vertical adds. CONDITIONAL on the engagement's own
  `:requires-cbris-registration?` ground truth (most Mauritius public
  tenders require a CBRIS-registered Mauritian company on file; a
  pure-foreign-market engagement may not)."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:requires-cbris-registration? e))
                 (not (true? (:has-cbris-registration? e))))
        [{:rule :cbris-registration-missing
          :detail (str subject " はCBRIS登録済みのモーリシャス法人(Certificate of Incorporation + BRN)を要するが未確認 -- 提出提案は進められない")}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- eprocurement-registration-unverified-violations
  "For `:filing/submit`, when the engagement declares
  `:requires-eprocurement-registration? true`, INDEPENDENTLY check
  `:eprocurement-registration-verified?` -- CONDITIONAL on the
  engagement's own ground truth. This is Mauritius's
  corporate-number-equivalent check: the dossier documents no separate
  national tax-ID scheme distinct from CBRIS company registration, so
  this actor verifies PPO e-Procurement System registration itself
  (mandatory since 28 September 2015) rather than inventing a distinct
  tax-ID check."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:requires-eprocurement-registration? e))
                 (not (true? (:eprocurement-registration-verified? e))))
        [{:rule :eprocurement-registration-unverified
          :detail (str subject " はPPO e-Procurement Systemへの登録確認を要するが未確認 -- 提出提案は進められない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (cbris-registration-missing-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (eprocurement-registration-unverified-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
