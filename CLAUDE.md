# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Educational Java project demonstrating HTTP client implementations and JSON parsing techniques using NASA's Near-Earth Objects (NEO) API data. Java 25 LTS, Maven, Jackson 3.x, Gson, JUnit 6.

## Build & Test Commands

```bash
make help               # List available tasks
make deps               # Check tools; auto-install mise (no root) + mise-pinned Java/Maven
make deps-maven         # Install Maven into ~/.local (act-runner fallback when mise is unavailable)
make deps-install       # Install Java and Maven via mise (reads .mise.toml)
make deps-act           # Install act into ~/.local/bin (no root)
make deps-gitleaks      # Install gitleaks into ~/.local/bin
make deps-trivy         # Install trivy into ~/.local/bin
make deps-docker        # Verify Docker is available (skipped under act)
make deps-check         # Show required tools and installation status
make build              # Build project (skips tests and OWASP dependency-check)
make test               # Run unit tests (fast)
make integration-test   # Run integration tests (WireMock-stubbed HTTP clients; *IT.java)
make lint               # Validate project configuration and check compiler warnings
make format             # Format Java sources with google-java-format
make format-check       # Verify Java sources are formatted
make secrets            # Scan for hardcoded secrets (gitleaks)
make trivy-fs           # Filesystem vulnerability/secret/misconfig scan
make mermaid-lint       # Validate Mermaid diagrams (requires Docker)
make check-toolchain-alignment  # Fail if the Java major disagrees across .java-version, .mise.toml, pom.xml
make static-check       # Composite fast quality gate (toolchain-alignment + format-check + lint + secrets + trivy-fs + mermaid-lint + deps-prune-check)
make clean              # Cleanup
make ci                 # Full CI pipeline (static-check, integration-test, coverage-check, build)
make ci-run             # Run GitHub Actions workflow locally using act
make coverage-generate  # Generate JaCoCo coverage report
make coverage-check     # Verify coverage meets 70% threshold
make coverage-open      # Open code coverage report
make cve-check          # OWASP CVE scan (slow, not part of normal workflow)
make vulncheck          # Alias for cve-check
make deps-prune         # Analyze declared-but-unused / used-but-undeclared dependencies
make deps-prune-check   # Fail build on declared-but-unused dependencies
make release VERSION=x.y.z  # Tag and push a release
make renovate-bootstrap # Install mise + Node for Renovate
make renovate-validate  # Validate Renovate configuration
make maven-settings-ossindex  # Create Maven settings for OSS Index credentials

# Run a single test class (raw mvn)
mvn -B test -Dtest=ClassName -Ddependency-check.skip=true
```

## Architecture

Two independent module areas under `src/main/java/`:

- **`http/client/`** — Five HTTP client implementations (Java HttpURLConnection, Java HttpClient, Apache HttpClient 5, OkHttp3, Retrofit with Gson converter) with shared models in `model/`
- **`jsonparse/`** — JSON processing examples organized by approach:
  - `treemodels/` — DOM-style parsing (Jackson JsonNode, Gson JsonElement)
  - `databinding/simple/` — POJO mapping with Jackson and Gson
  - `databinding/complex/` — Generated model classes for full NASA NEO response (separate `jackson/generated/` and `gson/generated/` packages)
  - `pathqueries/` — JsonPath and Jackson JsonPointer queries

### Notable design decisions

- **Jackson cross-major split** — `tools.jackson.core:jackson-databind` 3.x reuses the legacy `com.fasterxml.jackson.core:jackson-annotations` 2.x coordinate (Jackson 3 didn't move the annotations package). The `jsonparse/databinding/complex/jackson/generated/` POJOs import `com.fasterxml.jackson.annotation.*` — this is intentional, not a migration leftover.

## Testing

Three-layer test pyramid:

| Layer | Target | Discovery | Runtime |
|-------|--------|-----------|---------|
| Unit | `make test` | `*Test.java` via `maven-surefire-plugin` | seconds |
| Integration | `make integration-test` | `*IT.java` via `maven-failsafe-plugin` (activated by the `integration-test` Maven profile) | seconds (WireMock in-process) |
| E2E | _N/A_ | Library/demo project — no deployable unit | — |

> **No `e2e` CI job by design.** This project has no service to hit, so `integration-test` (`*IT.java` with WireMock-stubbed upstream) is the canonical end-of-pipeline test. Per `/ci-workflow` skill, the `e2e` requirement does not apply to libraries/demos with no deployable unit.

JUnit 6 tests in `src/test/java/` mirror the main source structure. Legacy unit tests invoke `main()` methods (these make live HTTP requests and are fragile). New `*IT.java` tests use [WireMock](https://wiremock.org/) to stub upstream HTTP services — see `OkHttpClientIT.java` as the reference pattern. Coverage enforced at 70% via JaCoCo plugin (`jacoco:check`).

## Static Analysis

`make static-check` is the composite fast quality gate and runs in CI:

- `check-toolchain-alignment` — fails fast if the Java major disagrees across `.java-version`, `.mise.toml`, and `pom.xml` (runs first; see Upgrade Backlog note on the untracked Java pin)
- `format-check` — [google-java-format](https://github.com/google/google-java-format) drift detection
- `lint` — `mvn validate` + `mvn compile` with `failOnWarning=true`
- `secrets` — [gitleaks](https://github.com/gitleaks/gitleaks) scan for hardcoded secrets
- `trivy-fs` — [Trivy](https://github.com/aquasecurity/trivy) filesystem scan for vuln/secret/misconfig (CRITICAL/HIGH fails build; MEDIUM informational)
- `mermaid-lint` — [mermaid-cli](https://github.com/mermaid-js/mermaid-cli) validates Mermaid blocks in `README.md` and `CLAUDE.md` (Docker-based)
- `deps-prune-check` — fails the build on declared-but-unused Maven dependencies (`mvn dependency:analyze-only -DfailOnWarning`)

Run `make format` to auto-apply google-java-format. `cve-check` (OWASP dependency-check) is kept separate — it runs on release tags (`v*`) because of its long runtime.

## Key Config

- **pom.xml** — maven-enforcer-plugin requires Maven 3.6.3+ and Java 25+; JaCoCo 70% threshold; compiler `failOnWarning` enabled; Jackson 3.x (`tools.jackson.core`); OWASP dependency-check bound to build lifecycle (skip with `-Ddependency-check.skip=true`); Failsafe plugin activated via `-P integration-test` profile
- **.mise.toml** — single source of truth for Java (Temurin 25 LTS), Maven 3.9.15, and aqua-backed pins for `act`, `gitleaks`, `trivy`; auto-installed by `make deps`. CI provisions the toolchain via [`jdx/mise-action`](https://github.com/jdx/mise-action). The Makefile's matching `_VERSION` constants are derived at parse time via `$(shell awk ...)` so the curl fallbacks (`deps-maven` / `deps-act` / `deps-gitleaks` / `deps-trivy`) used by `act` runners without mise read the same version mise installs locally.
- **.java-version** — secondary source of truth (IDE integration); `.mise.toml` is authoritative for build/CI. NOTE: the `.mise.toml` `java` pin (`temurin-25.0.3+9.0.LTS`) is **not auto-bumped by Renovate** — the mise LTS value format is not version-comparable by either the `custom.regex` manager or Renovate's native `mise` manager (empirically verified: both report `unsupported/unversioned value`). Java is therefore bumped manually across all three files (`.java-version`, `.mise.toml`, `pom.xml`); the `make check-toolchain-alignment` guard (first step of `static-check`) fails the build if any of the three drifts, so a partial bump cannot silently split the toolchain.
- **.nvmrc** — Node version for Renovate tooling (`make renovate-bootstrap`)
- **renovate.json** — Automated dependency PRs with automerge on all update types. `platformAutomerge` is intentionally `false` (not a deviation): the branch ruleset requires the `ci-pass` check, and GitHub-native auto-merge can complete in the ~1s window before `ci-pass` registers — landing a red bump. With `platformAutomerge: false` Renovate merges via its own run after re-confirming green. A `pinDigests: false` rule scoped to `custom.regex`+`docker` keeps `config:best-practices`' `docker:pinDigests` from colliding with (and silently dropping) the `MERMAID_CLI_VERSION` bump. `RENOVATE_VERSION` in the Makefile tracks the `npm` datasource (not `github-releases`) because Renovate's GitHub releases run ~8 versions ahead of npm publishes; `npx renovate@<ver>` resolves via npm. Detection: `make renovate-validate` failing with `npm error notarget No matching version found for renovate@<ver>` means the datasource is mismatched.
- **CI pipeline** — `changes` (path-filter detector via `dorny/paths-filter`) → `static-check` → `test` + `integration-test` + `build` (parallel); `cve-check` on release tags, weekly schedule (Mon 06:00 UTC), and manual dispatch, with NVD database cache; `ci-pass` gate aggregates all required jobs for branch protection (skipped doc-only jobs count as pass). Required GitHub secrets (set in Settings > Secrets and variables > Actions): `NVD_API_KEY`, `OSS_INDEX_USER`, `OSS_INDEX_TOKEN` — without `NVD_API_KEY` the tag-gated `cve-check` will hit NVD rate limits and fail.

## Upgrade Backlog

Items genuinely waiting on upstream — Renovate cannot bump these until the upstream channel makes them available.

- [ ] **CRITICAL — Renovate is NOT running (verified 2026-06-29).** Last `app/renovate` PR was #337 on 2026-04-01; the Dependency Dashboard (issue #287) froze at 2026-04-04; zero `renovate/*` branches exist while ~9 routine updates are available. The Mend GitHub App appears uninstalled/suspended — dependency bumps since April have been landing **manually** via `/project-review` and `/ship-it` sessions, not the bot. **Fix is external** (cannot be done from the repo): reinstall/reactivate at <https://github.com/apps/renovate> and check repo status at developer.mend.io. **Until reactivated, all `renovate.json` config is inert** (incl. `platformAutomerge:false`, the `pinDigests:false` rule, the renovate self-throttle, and all automerge/grouping) and every "Renovate will bump it" note below is on hold — bump manually. Available drop-ins as of 2026-06-29: jackson 3.1.4→3.2.0, junit 6.0.3→6.1.1, okhttp 5.3.2→5.4.0, trivy 0.70.0→0.71.2, act 0.2.87→0.2.89, maven 3.9.15→3.9.16, mermaid-cli 11.14.0→11.15.0, dependency-check-maven 12.2.1→12.2.2, jacoco 0.8.14→0.8.15, maven-enforcer-plugin 3.6.2→3.6.3.
- [ ] `jdk.incubator.vector` is still an incubator module as of Java 25 (still incubating in JEP 510 / 8th-round). The `--add-modules jdk.incubator.vector` flag in the `cve-check` recipe is required because OWASP dependency-check's bundled Lucene uses the Vector API. Drop the flag once Vector is promoted out of incubator (expected Java 26+).
- [ ] `maven.compiler.failOnWarning=true` will make JDK deprecation warnings upgrade-blocking on future LTS bumps (Java 29+). Plan a targeted deprecation audit before each LTS bump.
- [ ] JsonPath 3.1 not yet released — latest GA is `3.0.0` (2026-02-22). Watch release notes for behavioural changes that may affect `jsonparse/pathqueries/` when it ships.
- [ ] Pre-release deps awaiting GA: SLF4J 2.1.0 (alpha), `maven-compiler-plugin` 4.0.0 (currently `beta-4`), `maven-resources-plugin` 4.0.0 (currently `beta-1`). Adopt after GA. SLF4J 2.0.x is on latest GA. (JUnit 6.1.x reached GA — 6.1.1 — so it is no longer upstream-blocked; Renovate will bump the pom's 6.0.3 via the JUnit group.)

## Skills

Use the following skills when working on related files:

| File(s) | Skill |
|---------|-------|
| `Makefile` | `/makefile` |
| `renovate.json` | `/renovate` |
| `README.md` | `/readme` |
| `.github/workflows/*.{yml,yaml}` | `/ci-workflow` |

When spawning subagents, always pass conventions from the respective skill into the agent's prompt.
