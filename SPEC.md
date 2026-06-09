# SPEC.md — Agent Control MVP

> What to build. How to work lives in [AGENTS.md](AGENTS.md).
> Visual source of truth:
> [app/doc/design/agent-control-flow.png](app/doc/design/agent-control-flow.png)
> — screens 1–8. **Read it before any UI work.** Dark theme, purple accent;
> Approval = red chips, Needs Input = blue chips, Running/Completed = green,
> Blocked = red.

## 1. Product

**Problem:** Developers kick off autonomous coding agents on their machine but
lose visibility once they step away. They need to approve risky actions and
answer agent questions from their phone.

**Persona:** Developer / tech lead monitoring agent jobs away from their desk.

**Goal:** A polished, demoable Android prototype: monitor jobs, inspect agent
reasoning, resolve review requests (two types), and watch agents continue.

**Core journey:**
1. Open app → **Dashboard** of active jobs
2. Tap blocked job → **Job Detail** (Overview / Agents / Activity tabs)
3. Tap an agent → **Agent Inspector** bottom sheet
4. Review a pending item → **Approval Detail** → resolve by type
5. Return → agent continues or revises; job unblocks when nothing pending
6. Alternate path: Dashboard → **Requests Center** → Approval Detail

## 2. Terminology & domain rules

| Term | Definition |
|------|------------|
| Job | One goal in one repo; one Dashboard card |
| Agent | Worker on a job; many per job |
| Review request | Anything blocking an agent until the user acts |
| Type: Approval | Permission for a risky action — Reject / Approve |
| Type: Needs input | Pick one option (+ optional guidance) — Pause/Reject / Continue |

**Rules (testable):**
- Job status is **Blocked** iff ≥1 of its review requests is `PENDING`;
  otherwise **Running** (or **Completed**).
- Request statuses: `PENDING`, `APPROVED`, `REJECTED`, `INPUT_SELECTED`.
- **Continue** on a needs-input request requires a selected option
  (server rejects otherwise; button disabled in UI).
- Resolving a request: status updates, agent unblocks (or revises on reject),
  an activity event is appended, request moves to History (never deleted).
- Resolutions happen **only** on Approval Detail — list screens link, never act.
- Multiple pending requests per job allowed, mixed types; resolving one keeps
  the job Blocked if others remain.

## 3. Screens

Mock screen ↔ feature: 1 Dashboard · 2–4 Job Detail tabs · 5 Inspector ·
6 Requests Center · 7–8 Approval Detail (two variants).

### Dashboard (mock 1)
- Purpose: at-a-glance jobs; is anything waiting on me?
- States: loading / content / error / empty ("No active jobs")
- Per mock: top bar "Agent Control" + **Requests (N)** pill button (top-right);
  **"Jobs"** section header; **no summary strip**. Job cards — title with
  status chip top-right, "Repo: <name>" subtitle, "<N> agents" left +
  "Step X of Y" right, purple progress bar, and (when blocked) a
  **Pending requests** row with count chips: `1 Approval` (red) ·
  `1 Needs Input` (blue)
- **Bottom nav: Dashboard | Requests** (persists on Dashboard, Job Detail,
  and Requests Center per mock)
- Interactions: tap card → Job Detail; tap Requests pill or bottom-nav
  Requests → Requests Center; pull-to-refresh
- Out of scope: search, filters, job creation

### Job Detail (mock 2–4) — tabs: Overview | Agents | Activity
- Shared header per mock: metadata card with uppercase **label-over-value**
  grid in two columns — REPO / STATUS on row 1, PROGRESS / STARTED on row 2
  (progress bar under the Progress value), UPDATED on row 3 — exact layout
  per mock
- Overview: "Review requests (N)" header; one card per pending request —
  agent name + type chip (Approval red / Needs Input blue),
  "Action: <title>", risk chip + `[Review]` button. Footer stats per mock:
  "**N** agents on this job · **N** pending requests"
- Agents: rows with name, status chip (**Completed** / Blocked / Running),
  one-liner ("Waiting on delete approval" / "Completed plan"), chevron;
  tap → Inspector
- Activity: reverse-chronological timeline — purple dot + time on the left,
  event text on the right (per mock 4)
- After a resolution: card leaves Overview, agent row updates, events appear;
  job stays Blocked if other requests pending

### Agent Inspector (mock 5) — bottom sheet over dimmed Job Detail
- Per mock: agent name + status chip; "Role: <role>"; "Job: <job title>";
  **Currently:** one-liner; pending-request card (type chip + risk chip,
  "Action: <title>", `[Review]`); **Why:** reasoning paragraph;
  **Recent actions:** bulleted agent-scoped events with timestamps
- `[Review]` dismisses sheet → Approval Detail; reopening after a reject shows
  revised plan
- Presentation-only; state derives from Job Detail's repository Flow

### Requests Center (mock 6) — tabs + history preview
- Top bar: back + "Requests (N)"
- **Mock ambiguity — documented decision:** mock 6 shows a tab labeled
  "Needs Input" whose content includes BOTH request types, plus a
  "History (preview)" section below the pending list. Treat the label as a
  mock typo: tabs are **Pending | History**. Keep the
  **History (preview)** section (1 most-recent resolved row) at the bottom of
  the Pending tab, per mock.
- Pending tab grouped by job (title, "Repo: <name>", count chip). Rows:
  agent name + type chip + risk chip, title, `[Review]`
- History rows: agent + type chip + outcome chip — Approved / Rejected /
  Input selected (+ chosen option); feedback preview when present; chevron
- Empty states: "All caught up" / "No history yet"
- Nav label is **Requests** (per mock); code may say ApprovalCenter

### Approval Detail (mock 7–8) — full screen, shared shell + swapped body
- Shell header per mock: screen title = request title; metadata card grid —
  **Type** (colored by type), **Risk** (colored), **Agent**, **Job**,
  **Repo**, **Requested**
- Variant A — Approval: **Proposed action**, **Why**, **Affected files**
  (path list), **Feedback (optional)** multiline with **0/500 counter**,
  bottom buttons: **Reject** (outlined, red) · **Approve** (filled, purple)
- Variant B — Needs input: **Question**, **Why**, **Your selection** radio
  group (single select), **Additional guidance (optional)** with **0/500
  counter**, bottom buttons: **Pause / Reject** (outlined) ·
  **Continue with selection** (filled; disabled until an option is selected)
- Submit → repository → pop back; all screens update via shared Flows

## 4. Navigation

```
Dashboard ── job card ──► JobDetail(jobId) ── review ──► ApprovalDetail(requestId)
Dashboard ── badge ─────► RequestsCenter ── review ────► ApprovalDetail(requestId)
JobDetail ── agent row ─► InspectorSheet ── review ────► ApprovalDetail(requestId)
```
Single activity, typed routes with `jobId` / `requestId` args.
**Bottom nav** (Dashboard | Requests) on Dashboard, Job Detail, and Requests
Center per mock; hidden on Approval Detail.

## 5. Data & architecture

```
AgentNetworkApi (interface)        getSnapshot() / submitReviewResolution()
   └─ FakeAgentNetworkApi          delay(300), wraps FakeAgentServer,
                                   returns NetworkResult<SyncPayloadDto>
AgentRepository                    refresh, observe*, submitReviewResolution,
                                   seedIfEmpty
Room (source of truth for UI)     jobs, agents, review_requests (status +
                                   type indexed), activity_events; FK CASCADE
ViewModel → StateFlow → Compose
```

- UI observes Room Flows only. Mutations are **network-first**: server returns
  a full updated snapshot → upsert Room → Flows emit everywhere.
- `NetworkResult`: `Success` / `HttpError(code,msg)` / `NetworkError(cause)`.
- Entities: design `review_requests` for BOTH types from the start —
  common fields + nullable `proposedAction`/`affectedFiles` and
  `question`/`options`. No migrations.
- Entity→Domain mapping: thin extension functions; ViewModels never see
  entities or DTOs.
- Wiring: explicit `AppContainer` in Application. Backend swap path:
  `RetrofitAgentNetworkApi : AgentNetworkApi`, change one binding.

**Seed data (specified once, deterministic):**
- Jobs: **Migrate auth to OAuth2** / `my-app-backend` / Blocked / 3 agents;
  **Fix checkout bug** / `shop-app` / Running; **Add dark mode** / `shop-app` /
  Running.
- Job-1 agents: Architect (Done) · Coder (Blocked — **Approval:** delete
  `legacy/auth/`, High) · Test (Blocked — **Needs input:** choose test scope,
  Medium; options: Smoke only / Critical flows / Full integration).
- History: one earlier Approved request on job 1.
- Server behavior on resolve: update status, unblock agent (reject → revised
  plan text), append activity events per §2 rules.

## 6. Slices

**Slice 1 — Foundation.** Deps (Room+KSP, Navigation, lifecycle-VM-compose,
coroutines), all entities/DAOs/`AgentDatabase` v1, `AppContainer` stub.
Tests: none (schema). Manual: app builds, placeholder launches.

**Slice 2 — Fake network + repository.** DTOs, `NetworkResult`,
`AgentNetworkApi`, `FakeAgentServer` (seed + resolution rules),
`FakeAgentNetworkApi`, `DefaultAgentRepository` (refresh / observeJobs /
observeJobDetail / observeRequestsCenter / observeRequestDetail /
submitReviewResolution / seedIfEmpty). Include a **failure hook** on the fake
api (`failNextCall: Boolean` debug flag) so error states are testable and
demoable on demand.
Tests (the core of the day): server — both types seeded, approve, reject,
continue-requires-option, blocked-derivation; repository (in-memory Room) —
refresh populates, resolution updates request+agent+activity, pending→history
flow movement, job stays Blocked with another pending request.
Manual: none.

**Slice 3 — Shell.** Nav graph + typed routes, theme/palette per mock, shared
components (status chip, risk chip, type chip, loading/error/empty),
`seedIfEmpty()` on launch, placeholder screens.
Tests: none. Manual: navigate placeholders; relaunch → no reseed.

**Slice 4 — Dashboard.** ViewModel + UiState, summary strip, job cards,
Requests badge, pull-to-refresh.
Tests: loading→content, error state, pending counts/breakdown.
Manual: 3 jobs; blocked job shows breakdown; both nav paths work.

**Slice 5 — Job Detail + Inspector.** Tabbed screen, shared header per mock,
inspector sheet, review links.
Tests: JobDetail state mapping (header, pending list, blocked derivation);
inspector mapping (blocked shows Review, idle hides).
Manual: tabs populated; sheet opens with reasoning; Review routes correctly
by type.

**Slice 6 — Requests Center + Approval Detail.** Both tabs grouped by job;
detail shell + two variant bodies; submit → pop back.
Tests: pending/history partition + grouping; detail VM — Continue disabled
until selection, approve/reject/continue call repository correctly, error
surfaces on failure.
Manual: reject Coder approval w/ feedback → activity + revised plan; resolve
Test needs-input → job flips Running; verify propagation on all screens;
History shows outcomes.

**Slice 7 — Polish + demo.** Error/empty sweep on all screens, content
descriptions, missing edge-case VM tests, finalize seed narrative, full
rehearsal (both entry paths), **record backup screen capture**, and the
survival drill: (a) rotate on Approval Detail with draft feedback typed —
text survives; (b) background → `adb shell am kill <pkg>` → relaunch from
recents — selected option and resolved requests survive; (c) trip
`failNextCall` → submit fails → error shown, input intact → retry succeeds.

## 7. Cut list (in order)
1. Pull-to-refresh  2. History tab richness (keep one row)
3. Inspector recent-actions section  4. Activity tab (keep events in History
only)  5. History (preview) section on Pending tab

## 8. Deferred intentionally
Real API/Retrofit (interface swap ready) · Hilt · auth · push/WebSocket ·
diff viewer · dependency graph · read-only history detail · WorkManager sync.

## 9. Time budget (5 h)
| Block | Est. | Clock stop |
|-------|------|------------|
| Plan, docs, project scaffold | 0:40 | 0:40 |
| Slices 1–2 | 1:20 | 2:00 |
| Slices 3–4 | 0:55 | 2:55 |
| Slices 5–6 | 1:25 | 4:20 |
| Slice 7 | 0:40 | 5:00 |

Overrun >10 min on any block → cut per §7. Never borrow from Slice 7.