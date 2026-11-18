# Validation record

Validated locally on 2026-11-17 (America/Sao_Paulo), macOS ARM64, using JDK 21 and the pinned Maven 3.10.1 wrapper. All records were synthetic. No external financial service or real user dataset was used.

## Automated checks

`JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./mvnw spotless:apply verify` completed successfully after the final implementation change.

- **11 tests, 0 failures, 0 errors, 0 skipped** (`FinanceTest`).
- Compiler lint enabled with `-Xlint:all,-serial`; no source warnings in the final build.
- Spotless uses Google Java Format 2.0.0 and checks formatting during `validate`.
- JaCoCo 0.8.16 reports line coverage: model **100%**, calculations **100%**, CSV **100%**, DAO/database **92.8%**. UI/startup code is not covered by the headless unit suite. Uncovered DAO lines include defensive database/close error paths; do not read these core percentages as whole-application coverage.
- Executable JAR produced: `target/controle-financeiro-1.0.0.jar` (approximately 13 MB).
- Final validated JAR SHA-256: `f9002143eb1dcd6a8e70481361d15c4aae39a904a2ca4edfb1dc16044a009c92`.
- Shade merges service providers and appends duplicate license/notice files. Remaining duplicate-manifest/about-file notices are packaging warnings, not ignored compilation errors.

The tests exercise exact cent values and money bounds; mandatory text and real calendar dates; month/year isolation; category expense aggregation; temporary file-backed H2 restarts; edits/deletes; categories and budgets; rollback when a later imported entry is invalid; malformed/header/type/date/amount/column-count CSV failures; quotes, commas, newlines and formula guards; 10,001-record rejection; stale edits; future schema refusal without deleting existing records; and the checked-in CSV example's BRL 4,400.00 balance after reopening.

Reports are generated at `target/surefire-reports/` and `target/site/jacoco/index.html`. These are intentionally untracked. Optional CRAP/duplication/mutation tools mentioned by the implementation guidance were not installed or exposed; no mutation-testing result is claimed.

## Real application checks

Opened the actual executable application, then built/opened a `jpackage --type app-image` macOS ARM64 bundle with its included Java runtime. The final QA image used the same validated JAR and an explicitly isolated temporary database. The distributable image has no hard-coded QA path or seed flag.

Observed through the real Swing window:

1. Five synthetic demo records; income BRL 6,200.00, expense BRL 2,314.40, balance BRL 3,885.60, budget BRL 3,500.00, remaining BRL 1,185.60 and usage 66%.
2. Portuguese/English controls and currency formatting, with circular local flags. Typed “Cafe de teste” and `12.50`, changed language and verified both values remained in the editor.
3. Saved the entry through the UI: six rows, expenses BRL 2,326.90, balance BRL 3,873.10 and budget remaining BRL 1,173.10.
4. Selected that row, edited the amount to BRL 20.00 and saved: expenses BRL 2,334.40 and balance BRL 3,865.60.
5. Selected the same synthetic row, confirmed deletion, and observed the original five rows and original totals again.
6. Attempted to save an empty amount and observed the specific input-validation dialog.
7. Navigated to December 2026: empty table, zero totals and no budget. Returned to November and Portuguese without changing the saved records.
8. Inspected the standard 1260×850 layout after adjusting the budget panel: editor, ledger, chart and all budget controls fit visibly. Saved current Portuguese, English and empty-month captures plus the validation dialog capture under `screenshots/`.

Initial automation attempts could not focus text fields in a background instance. Activating the final native instance resolved that issue; the successful create/edit/delete/language checks above supersede those attempts. A restart attempted before the previous test process released its file lock showed the expected startup error; after closing it and retrying, the same local database reopened successfully. No compatibility mode, remote database server or weakened file lock was enabled.

## Verified limits and follow-up

- The compact layout is implemented (stacked editor below 1080 px), with a minimum window of 820×640 and scrolling. The inspection tool's resize gesture did not change the window, so compact visual QA remains unverified.
- CSV core behavior is covered by tests, including the repository example; native file-picker import/export interactions were not automated end-to-end.
- Full screen-reader, high-DPI/multiple-monitor and keyboard-only audits were not performed. Native operating-system option-dialog buttons may follow the host locale.
- Windows/Linux runtime and native packaging, installers, signing and notarization were not tested. No mobile/web compatibility is claimed.
- Historical MySQL-to-H2 automatic migration is not implemented. V1 initialization and future-version protection are tested; arbitrary future schema upgrades require new explicit migrations and tests.
- This local academic database is not encrypted and has no application authentication, network listener, bank integration or multi-user synchronization.
- CI configuration is checked in and uses pinned action commits; local validation does not itself certify a remote workflow run. Consult GitHub Actions for its live result.
- GitHub description and seven topics were saved and verified. The Medium editorial account is available, but the author-filtered search for “controle financeiro” found only an unrelated fictional story. No original article URL is known, so the concrete lesson revision remains unpublished. No unrelated article was edited and no newsletter was sent.
- The two source commits are local. A direct push to `origin/master` was rejected by automatic approval review for lack of explicit authorization for that publication target; no remote CI run for these commits is claimed. Source publication is pending that approval.
- No application-store release, package publication, production backend or hosting deployment was performed.
