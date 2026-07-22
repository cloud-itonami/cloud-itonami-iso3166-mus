# cloud-itonami-iso3166-mus

**`:implemented`** for **MUS** (Mauritius). Flagship check
`cbris-registration-missing` (CBRIS company registration via the
Corporate and Business Registration Department), second hard check
`eprocurement-registration-unverified` (PPO e-Procurement System
registration, mandatory since 28 September 2015).

Independent Public-Sector Market-Entry & Procurement Compliance Service:
a MarketEntry-LLM advisor sealed behind a langgraph-clj `StateGraph`,
censored by a 7-check Market-Entry Compliance Governor, with an
append-only audit ledger and a 0→3 phase rollout gate. See
`docs/business-model.md` for the Trust Controls this actor enforces and
`docs/operator-guide.md` for the human-operator workflow.

```
clojure -M:dev:test   # run the full test suite
clojure -M:lint       # clj-kondo, errors fail
clojure -M:dev:run    # demo driver (marketentry.sim)
```

Regulatory grounding (verified against `.govmu.org` government domains —
see `src/marketentry/facts.cljc` for full citations):

- **PPO** — Procurement Policy Office, 8th Floor, Emmanuel Anquetil
  Building, Port Louis, Mauritius, the public procurement regulator.
  `https://ppo.govmu.org/`
- **Procurement law** — Public Procurement Act 2006 (Act 33 of 2006).
- **PPO e-Procurement System** — mandatory registration for suppliers,
  contractors and consultants since 28 September 2015; traditional
  (paper-based) procurement is being gradually phased out.
  `https://publicprocurement.govmu.org/`
- **CBRD / CBRIS** — the Corporate and Business Registration
  Department, via the Corporate and Business Registration Integrated
  System; online company registration yields a Certificate of
  Incorporation and a Business Registration Number (BRN).
- **Company formation** — at least one director and one shareholder;
  at least one director must be a Mauritius resident; a registered
  office address in Mauritius is mandatory; a unique company name
  approved by the Registrar of Companies; a corporate bank account in
  Mauritius is mandatory.

## Market-entry compliance actor

The Actors pattern (containment + independent Governor + append-only
audit ledger, per skill `build-actor`), same architecture as sibling
`cloud-itonami-iso3166-ago` / `-dji` / `-mli` / `-stp`:

- `src/marketentry/facts.cljc` — the spec-basis catalog, grounded ONLY
  in the verified `.govmu.org` research dossier; a jurisdiction not in
  `catalog` has no spec-basis, full stop.
- `src/marketentry/governor.cljc` — the Market-Entry Compliance
  Governor, 7 HARD checks in priority order (spec-basis,
  evidence-incomplete, `cbris-registration-missing` [flagship],
  engagement-fee mismatch, `eprocurement-registration-unverified`,
  confidence/actuation gate, double-draft/double-submit).
- `src/marketentry/store.cljc` — `MemStore` (dev/test default) and a
  `DatomicStore` built on `langchain-store.core` (`ls/enc`/`ls/dec*`/
  `ls/read-stream`/`ls/append-blob!`), never a hand-rolled codec.
- `src/marketentry/registry.cljc` — pure filing-draft/filing-submit
  record construction + the engagement-fee ground-truth recompute.
- `src/marketentry/marketentryllm.cljc` — the contained, deterministic
  advisor (never commits directly).
- `src/marketentry/operation.cljc` — the langgraph-clj StateGraph
  wiring advise → govern → decide → (commit | request-approval | hold).
- `src/marketentry/phase.cljc` — 0→3 rollout gate; `:filing/draft`/
  `:filing/submit` never auto-commit at any phase.

AGPL-3.0-or-later.

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Mauritius:

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
