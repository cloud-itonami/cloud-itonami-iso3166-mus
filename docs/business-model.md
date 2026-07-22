# Business model — MUS

Independent Public-Sector Market-Entry & Procurement Compliance Service
for the Republic of Mauritius: an assistive actor that helps a market-
entry operator assemble, track, and (with a human's sign-off) file the
registration evidence a foreign operator needs to bid on Mauritian
public tenders.

## Who this serves

Foreign companies (and their local counsel/agents) preparing to bid on
Mauritian public-sector contracts, who need to track:

- **CBRIS company registration** — the Corporate and Business
  Registration Integrated System, the online channel through which the
  Corporate and Business Registration Department (CBRD, Ground Floor,
  7 Exchange Square, Wall Street, Ebene) registers companies.
  Successful registration yields a **Certificate of Incorporation**
  and a **Business Registration Number (BRN)**.
- **PPO e-Procurement System registration** — since 28 September 2015,
  the Procurement Policy Office (PPO) operates an e-Procurement System
  where public-procurement activities are carried out digitally.
  Suppliers, contractors and consultants are advised to register on
  this system, as traditional (paper-based) procurement is being
  gradually phased out.
- **Company formation requirements** — at least one director and one
  shareholder; at least one director must be a Mauritius resident; a
  registered office address in Mauritius is mandatory; a unique
  company name approved by the Registrar of Companies; a corporate
  bank account in Mauritius is mandatory.

## What this actor does

1. **Engagement intake** — normalize the operator's own case data
   (operator name, engagement fee terms). No new facts invented.
2. **Jurisdiction assessment** — hand back the MUS evidence checklist
   from `src/marketentry/facts.cljc`, always citing an official source
   (`ppo.govmu.org`). A jurisdiction not in the catalog gets NO
   checklist — the actor states plainly that it has no official
   spec-basis rather than guessing.
3. **Filing draft** — prepare the FILING-DRAFT record (an unsigned,
   internal book-of-record entry — not a real PPO e-Procurement System
   submission).
4. **Filing submit** — prepare the FILING-SUBMIT record. This is the
   step that corresponds to an actual real-world portal action, so it
   is the most tightly gated.

## Trust Controls

- **A false or fabricated regulatory-requirement claim is a HARD
  hold.** Every jurisdiction assessment must cite an official source
  from `marketentry.facts`; an assessment with no citation, or one that
  claims a `:spec-basis` this actor never verified, is rejected outright
  and no human can override it.
- **Any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off.** `:filing/draft` and `:filing/submit` proposals are
  NEVER auto-committed, at any rollout phase — a human market-entry
  operator makes the actual filing decision every time, even when the
  governor finds nothing wrong.
- **Independent re-verification, not trust-on-claim.** The governor
  independently recomputes the claimed engagement fee (`base-fee +
  monthly-rate × monitoring-months`) rather than trusting the claimed
  total, and independently checks the engagement's own
  `:has-cbris-registration?` / `:eprocurement-registration-verified?`
  facts rather than assuming a filing-draft or filing-submit proposal
  is accurate.
- **Missing CBRIS company registration is an unoverridable HARD hold**
  when the engagement declares it is required
  (`cbris-registration-missing` — the flagship check this vertical
  adds, grounded in the dossier's Certificate of Incorporation + BRN
  facts).
- **Unverified PPO e-Procurement System registration is an
  unoverridable HARD hold** when the engagement declares it is
  required (`eprocurement-registration-unverified`). This is
  Mauritius's corporate-number-equivalent check: the dossier documents
  no separate national tax-ID scheme distinct from CBRIS company
  registration, so this actor verifies e-Procurement System
  registration itself (mandatory since 28 September 2015) rather than
  inventing a distinct tax-ID check.
- **No invented tax-ID scheme.** The dossier gives no evidence of a
  Mauritius-specific tax-ID/VAT verification scheme separate from
  CBRIS company registration — this actor does not claim one.
- **Append-only audit ledger.** Every commit or hold decision writes
  exactly one immutable ledger fact — there is a complete, tamper-evident
  record of what was proposed, what the governor found, and what a human
  approved or rejected.

## Regulatory sources (all independently verified against `.govmu.org` domains)

- PPO (Procurement Policy Office), 8th Floor, Emmanuel Anquetil
  Building, Port Louis, Mauritius: `https://ppo.govmu.org/`
- Public Procurement Act 2006 (Act 33 of 2006), consolidated text:
  `https://ppo.govmu.org/Documents/PPA/Public%20Procurement%20Act21012025.pdf`
  (an earlier/original text is also at
  `https://ppo.govmu.org/SiteApplication/documents/PPA2006.pdf`)
- PPO e-Procurement System (mandatory registration since 28 September
  2015): `https://publicprocurement.govmu.org/`
- Corporate and Business Registration Department (CBRD), Ground Floor,
  7 Exchange Square, Wall Street, Ebene, Republic of Mauritius —
  registration via the Corporate and Business Registration Integrated
  System (CBRIS); this actor's dossier did not capture a specific
  webpage URL for the CBRD/CBRIS site itself, only its physical
  address, registration channel, and outputs (Certificate of
  Incorporation + BRN) — stated honestly rather than inventing a URL.
