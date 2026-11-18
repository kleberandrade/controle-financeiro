# Contributing

Use JDK 21 and the checked-in Maven wrapper. Do not commit databases, credentials, IDE-private configuration or generated packages. Use synthetic fixtures only.

Run `./mvnw spotless:apply verify` before submitting a change. Tests should describe user-observable outcomes, use temporary databases and prove failure paths as well as successful writes. Add a regression test before fixing a reproduced core bug. UI work also requires opening the real application and checking both languages, empty/error states and compact layout. Record exactly which platforms were checked.

Keep financial calculations in `FluxoCaixa`, validation in the domain and parsing boundaries, persistence behind `FinanceRepository`, and component rendering in `view`. Use `BigDecimal`, never binary floating-point, for money. Do not silently round input or swallow persistence failures. Maintain one transaction for an entire import.

Once released, do not edit a deployed migration. Add a numbered migration with recovery and upgrade tests; the current small V1 runner must be extended to handle it explicitly. Unknown newer database versions must remain protected. Back up a closed database before testing upgrades.

Keep Portuguese/English labels paired. Language changes must not clear user data or editor fields. Preserve keyboard focus, label associations, 44-pixel language targets and the table's readable alternatives to charts. Do not render imported text as Swing HTML.

Preserve Kleber's authorship, academic context, MIT license, historical screenshot and third-party notices. Update README examples, changelog and lesson references when behavior or pinned versions change. Do not publish packages, deploy services, edit unrelated articles or send newsletters as part of an ordinary contribution.

Use focused commits explaining the problem, the change and its verification. Include reproduction steps and known limitations in pull requests.
