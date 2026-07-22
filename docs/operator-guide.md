# Operator guide — MUS

Human-gated filing only. Every `:filing/draft`/`:filing/submit`
proposal always pauses for a human market-entry operator's approval —
there is no rollout phase in which either auto-commits (see
`src/marketentry/phase.cljc`).

## Portal / channel

**PPO** (Procurement Policy Office) — `https://ppo.govmu.org/` —
Mauritius's public-procurement regulator. Since 28 September 2015 the
PPO operates an **e-Procurement System**
(`https://publicprocurement.govmu.org/`) where public-procurement
activities are carried out digitally; suppliers, contractors and
consultants are advised to register on it, as traditional
(paper-based) procurement is being gradually phased out.

Company registration itself runs through the **Corporate and Business
Registration Department (CBRD)** via the **Corporate and Business
Registration Integrated System (CBRIS)** — successful registration
yields a Certificate of Incorporation and a Business Registration
Number (BRN).

## Workflow

1. `:engagement/intake` — record the operator, portal, and fee terms
   for a new engagement. May auto-commit at phase 3 once the governor
   is clean (low real-world risk — no filing has happened yet).
2. `:jurisdiction/assess` — request the MUS evidence checklist. ALWAYS
   requires human approval, even when clean (governor `escalate?` is
   forced true for this operation regardless of phase).
3. `:filing/draft` — prepare the internal filing-draft record.
   Requires a completed assessment on file (`evidence-incomplete`
   otherwise). ALWAYS requires human approval.
4. `:filing/submit` — prepare the filing-submit record, the step that
   corresponds to an actual real-world PPO e-Procurement-facing
   action. ALWAYS requires human approval, and is independently
   checked against:
   - `cbris-registration-missing` — is the operator's Mauritian
     company actually CBRIS-registered (Certificate of Incorporation
     + BRN) when the engagement declares one is required?
   - `engagement-fee-mismatch` — does the claimed fee actually equal
     `base-fee + monthly-rate × monitoring-months`?
   - `eprocurement-registration-unverified` — has the operator's PPO
     e-Procurement System registration itself actually been
     independently verified, when the engagement declares
     verification is required?
   - `already-drafted` / `already-submitted` — refuses to double-file
     the same engagement.

Any HARD violation above is a hold NO approver can override — the
operator must fix the underlying engagement record (verify the CBRIS
registration, correct the fee, verify the e-Procurement registration)
before resubmitting, not approve past the governor.

## Required evidence checklist (per `src/marketentry/facts.cljc`)

- CBRIS company registration record (Certificate of Incorporation +
  Business Registration Number)
- PPO e-Procurement System registration record
- Mauritius-resident-director record
- Authorized-representative record
