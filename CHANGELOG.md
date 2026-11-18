# Changelog

## 1.0.0 — 2026-11-17

- Preserve the academic Java Swing/JPA identity and MIT authorship while replacing obsolete NetBeans/Ant library paths with JDK 21 and a checksummed Maven wrapper.
- Upgrade EclipseLink to Jakarta Persistence, replace the hard-coded MySQL root connection with embedded H2 and a versioned, retryable initial schema.
- Replace double/Date money and dates with validated BigDecimal/LocalDate, short-lived entity managers, explicit transactions and propagated errors.
- Complete entry editing/deletion, persistent categories, month/year filters, monthly budgets, category charts and safe CSV exchange with atomic import validation.
- Add PT-BR/EN labels, local circular flags, adaptive Swing layout, background operations, empty/loading/error states and accessible controls.
- Add temporary-database tests, formatter/lint/coverage verification, CI, native application-image packaging and current screenshots.
- Refresh English documentation and prepare an unpublished Portuguese teaching revision with five practical exercises.

## 0.1 — 2019

Original Unisal Campinas classroom application by Kléber de Oliveira Andrade using Java Swing, JPA/EclipseLink, MySQL, JCalendar and JFreeChart. Historical sources and generated artifacts remain in Git history.
