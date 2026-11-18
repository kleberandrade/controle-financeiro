# Controle Financeiro

![Java 21](https://img.shields.io/badge/Java-21-176C55)
![Swing](https://img.shields.io/badge/UI-Swing-176C55)
![H2 2.5.252](https://img.shields.io/badge/H2-2.5.252-176C55)
[![Java verification](https://github.com/kleberandrade/controle-financeiro/actions/workflows/verify.yml/badge.svg)](https://github.com/kleberandrade/controle-financeiro/actions/workflows/verify.yml)
[![MIT license](https://img.shields.io/badge/license-MIT-176C55)](LICENSE)

An academic, local-first finance workbook in **Java Swing**, originally developed by **Kléber de Oliveira Andrade** for the third-semester Systems Analysis and Development course at **Unisal Campinas**. The modernization preserves Java, JPA/EclipseLink and the original editor → ledger → totals → chart workflow.

![Controle Financeiro desktop showing synthetic income, expenses, category bars and monthly budget](screenshots/desktop-pt.png)

*Actual macOS application capture. Every displayed transaction is synthetic. This is a local demonstration database, with no bank connection or online account.*

## What works

- Create, select, edit and delete income and expense entries. Deletion requires confirmation.
- Persistent categories; categories used by entries cannot be removed.
- Month/year navigation, exact income/expense/balance totals, category spending bars (top four categories) and monthly expense budgets, including overspending and no-limit states.
- UTF-8 CSV import with whole-file validation and one database transaction; month-specific CSV export with spreadsheet formula guards.
- Portuguese and English UI, locally drawn circular Brazil/UK flag buttons, accessible labels and 44 × 44 language targets. Changing language preserves entries and editor text; currency stays BRL.
- Background database/file operations, loading feedback, empty states and actionable validation messages. Compact windows stack the editor above the ledger with scrolling.

## Reproducible setup

Install a **JDK 21**. No NetBeans, MySQL, database server or root account is required. Maven **3.10.1** is pinned by the checked-in wrapper, with its SHA-256 checksum. The first build needs network access to Maven Central.

```sh
git clone https://github.com/kleberandrade/controle-financeiro.git
cd controle-financeiro
# Set JAVA_HOME to your JDK 21 installation, and add its bin directory to PATH.
./mvnw --batch-mode verify
java -jar target/controle-financeiro-1.0.0.jar
```

On Windows, use `mvnw.cmd --batch-mode verify`. On this project's macOS validation host, the JDK is `/opt/homebrew/opt/openjdk@21`:

```sh
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw verify
```

An empty workbook is the default. To open a **separate persistent synthetic demonstration**, use an explicit directory:

```sh
java -Dfinance.dataDir=./data/demo -jar target/controle-financeiro-1.0.0.jar --demo
```

`--demo` seeds five clearly labelled example entries only when that database has no entries, and sets the current month's expense budget to BRL 3,500.00. Running it again after deleting every entry intentionally seeds it again. Avoid `--demo` for your own workbook.

### Native desktop package

```sh
# JAVA_HOME must point to the full JDK (including jpackage).
./scripts/package.sh
```

This produces an application image with its own Java runtime under `target/distribution/`. On macOS, open `target/distribution/Controle Financeiro.app`. The destination must not already contain the same application; move the previous package aside before rebuilding. Build each OS's package on that OS. The macOS ARM64 image was built and opened locally; Windows/Linux native packaging, signing, notarization and installers have not been verified. Nothing is published by this script.

## A short walkthrough

1. Choose a month with the arrow buttons. Categories are shared between months.
2. Enter a description, type, positive amount (for example `1250.50` or `1250,50`), category and an ISO date such as `2026-11-10`. Do not use thousands separators.
3. Save. Selecting a row loads it into the editor; saving then updates that entry. “New / clear” starts a new entry. Dates determine which month's table contains an entry, even when entered from another month.
4. Set the expense budget. `0` means no limit. The balance is income minus expenses; remaining budget is budget minus expenses. Negative remaining budget is permitted and shown explicitly.
5. Export the displayed month, or import a validated CSV after reviewing the entry count. Reimporting a file creates duplicates; it does not replace or deduplicate entries.

All money uses `BigDecimal` and `DECIMAL(14,2)` with no silent rounding. Entry amounts range from BRL 0.01 to 999,999,999,999.99; budgets also allow zero. Dates use `LocalDate` (years 0001–9999), month keys use `YearMonth`, descriptions allow 160 characters and categories 60. Strings are trimmed at the domain boundary.

## CSV contract

[examples/transactions.csv](examples/transactions.csv) is a real importable example. Create any custom categories before importing. Standard categories are Casa, Alimentação, Transporte, Lazer, Salário and Outros.

```csv
date,type,description,category,amount
2026-11-01,RECEITA,Salário de exemplo,Salário,6200.00
2026-11-03,DESPESA,Aluguel de exemplo,Casa,1800.00
```

The header order is exact and language-independent. Type values are `RECEITA` and `DESPESA`; decimal amounts use a dot; dates are ISO. The parser supports RFC 4180 quoting, commas, quotes and multiline descriptions. Imports are limited to 5 MB and 10,000 records. Invalid headers, dates, types, values, column counts, malformed quoting or unknown categories leave the workbook unchanged.

Export prefixes potentially executable text (`=`, `+`, `-`, `@`, including leading whitespace) with an apostrophe. Existing leading apostrophes are doubled. The importer reverses these guards; this is an **application-specific CSV convention**. Keep the exported file as text when inspecting its raw contents; spreadsheet programs may display the guard. Importing arbitrary external files with apostrophe-prefixed formulas may remove their guard in storage, but every subsequent export guards them again. Never enable spreadsheet macros or external content to inspect an export.

## Storage, backups and migration

By default, H2 stores `ledger.mv.db` in `~/.controle-financeiro/`. Override only the directory with `-Dfinance.dataDir=/absolute/path`. The application creates it if needed. No TCP listener is started. The embedded database uses a local demo user named `ledger`, not a MySQL administrator. It has no application login or encryption; protect the directory with your OS account. This is an educational single-user workbook, not a production accounting system.

Close the application before copying the entire data directory for backup. Restore by opening a copy with `finance.dataDir`. CSV contains entries only: keep a full database backup to preserve categories and budgets.

`src/main/resources/db/V1__ledger.sql` owns the initial schema; `schema_version` records completion. H2 DDL auto-commits, so V1 uses idempotent statements and records its version last, allowing retry after interrupted initialization. ORM schema generation is disabled. Newer unknown schema versions are refused. There is no automatic downgrade, general-purpose migration engine, or automatic import of the historical MySQL schema. Export/transform old records to the documented CSV contract and verify totals on a copy before retiring an old database.

## Architecture and versions

- `model/`: validated financial entry, type enum, `BigDecimal`, `LocalDate`.
- `controller/FluxoCaixa`: pure month filtering, totals and category aggregation.
- `dao/FinanceRepository`: replaceable storage boundary. `LocalDatabase` manages H2 migrations, short-lived entity managers and resource-local transactions. Exceptions propagate; failed writes roll back.
- `csv/`: parsing, validation and reversible spreadsheet guards.
- `view/`: Swing editor/table, adaptive layout, asynchronous actions, currency formatting and locally drawn flags/charts. `App` owns startup, initialization, demo seeding and shutdown.

Validated dependency pins: EclipseLink **5.0.2**, Jakarta Persistence **3.2.0** (transitive), H2 **2.5.252**, FlatLaf **3.8**, Commons CSV **1.14.1**, Commons IO **2.22.0**, Commons Codec **1.23.0** and JUnit **6.1.3**. IO/Codec are explicitly managed to compatible current versions rather than retaining Commons CSV's older transitives. Java 21 remains the chosen LTS baseline; preview SDKs and EclipseLink 6 milestones are excluded. See [pom.xml](pom.xml) for all build pins.

## Verification and limits

```sh
./mvnw verify                 # formatting, compiler lint, tests, executable JAR, JaCoCo
./mvnw spotless:apply         # apply the pinned Java formatter
./mvnw dependency:tree        # inspect the resolved dependency graph
```

Eleven automated tests cover decimal arithmetic, validation, temporary H2 persistence across restarts, edits/deletes, category deletion, month/year isolation, budgets, category totals, atomic import rollback, CSV guards/quoting/limits and future schema refusal. Coverage is generated at `target/site/jacoco/index.html`; UI coverage is reported separately from the tested core. See [docs/VALIDATION.md](docs/VALIDATION.md) for observed checks and remaining platform/input automation limits. CI runs `verify` on JDK 21; the badge reports the actual workflow status.

The application is desktop software, not a mobile/web app. No device tests, bank integration, authentication, multi-user synchronization, automatic backup, production deployment or financial advisory functionality is claimed. JVM diagnostics are written to standard error; the UI does not expose database internals. Accessibility labels and table alternatives are present, but a complete screen-reader audit has not been performed.

## Teaching material and history

The original 2019 NetBeans/Ant implementation and generated JavaDocs remain available in [Git history](https://github.com/kleberandrade/controle-financeiro/tree/e42fefdb7b7c52b1f9a91bfba0a79b59d9e51134). The [historical screenshot](screenshots/sistema_financeiro.PNG) is retained for attribution and comparison; it is not the current application.

A concrete Portuguese lesson revision, with verified source references and exactly five exercises, is in [docs/LESSON.pt-BR.md](docs/LESSON.pt-BR.md). No associated Medium URL was found in the project, focused public search or Medium’s authenticated author-filtered search for “controle financeiro”. An editorial session was available, but the only search result was an unrelated fictional story; no original lesson could be identified safely. The revision has not been published; any later update must preserve the original article URL, language and authorship. No newsletter is sent.

## Contributing and license

Read [CONTRIBUTING.md](CONTRIBUTING.md), [CHANGELOG.md](CHANGELOG.md), [docs/WORK_LOG.md](docs/WORK_LOG.md) and [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md). The original **MIT license and 2019 authorship are preserved**. Dependency licenses retain their own terms.
