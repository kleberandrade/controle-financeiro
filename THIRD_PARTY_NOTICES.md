# Authorship and third-party notices

Controle Financeiro was created by **Kléber de Oliveira Andrade** in 2019 for **Unisal Campinas**. Its MIT license remains in [LICENSE](LICENSE). Modernization does not replace that authorship.

The executable contains third-party libraries under their own licenses:

- EclipseLink 5.0.2 and Jakarta Persistence API 3.2.0: Eclipse project notices, including EPL-2.0 / EDL-1.0 terms as provided in their artifacts. See [EclipseLink](https://eclipse.dev/eclipselink/) and [Jakarta Persistence](https://jakarta.ee/specifications/persistence/3.2/).
- H2 2.5.252: [H2 license terms](https://h2database.github.io/html/license.html), offered under MPL-2.0 or EPL-1.0.
- FlatLaf 3.8: [Apache-2.0](https://github.com/JFormDesigner/FlatLaf/blob/main/LICENSE).
- Commons CSV 1.14.1, Commons IO 2.22.0 and Commons Codec 1.23.0: [Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0).
- ASM 9.10.1 (EclipseLink transitive runtime dependency): [BSD-3-Clause](https://asm.ow2.io/license.html).
- Maven Wrapper 3.3.4 scripts: Apache-2.0; source headers are retained.

JUnit, JaCoCo, Spotless, Google Java Format, Maven and their dependencies are build/test tools, not application code under the project's MIT license. Maven Shade appends duplicate library license/notice resources and merges service provider declarations into the executable. Source artifacts remain available from Maven Central. The native application image also contains OpenJDK runtime legal notices in its bundled runtime directory. Preserve those notices when redistributing.

The new flags, chart and conceptual cover are drawn locally from geometry. No external flag images, stock photography or third-party icon pack is embedded. The original screenshot is retained as historical project material.
