# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Educational Java project demonstrating HTTP client implementations and JSON parsing techniques using NASA's Near-Earth Objects (NEO) API data. Java 25 LTS, Maven, Jackson 3.x, Gson, JUnit 6.

## Build & Test Commands

```bash
make help               # List available tasks
make deps               # Check tools; auto-install mise (no root) + mise-pinned Java/Maven
make deps-maven         # Install Maven into ~/.local (CI fallback when setup-java is unavailable)
make deps-install       # Install Java and Maven via mise (reads .mise.toml)
make deps-act           # Install act into ~/.local/bin (no root)
make deps-gitleaks      # Install gitleaks into ~/.local/bin
make deps-trivy         # Install trivy into ~/.local/bin
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
make static-check       # Composite fast quality gate (format-check + lint + secrets + trivy-fs + mermaid-lint)
make clean              # Cleanup
make ci                 # Full CI pipeline (static-check, test, coverage-check, build)
make ci-run             # Run GitHub Actions workflow locally using act
make coverage-generate  # Generate JaCoCo coverage report
make coverage-check     # Verify coverage meets 70% threshold
make coverage-open      # Open code coverage report
make cve-check          # OWASP CVE scan (slow, not part of normal workflow)
make vulncheck          # Alias for cve-check
make deps-updates       # Print available dependency updates
make deps-update        # Update dependencies to latest releases
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

## Testing

Three-layer test pyramid:

| Layer | Target | Discovery | Runtime |
|-------|--------|-----------|---------|
| Unit | `make test` | `*Test.java` via `maven-surefire-plugin` | seconds |
| Integration | `make integration-test` | `*IT.java` via `maven-failsafe-plugin` (activated by the `integration-test` Maven profile) | seconds (WireMock in-process) |
| E2E | _N/A_ | Library/demo project — no deployable unit | — |

JUnit 6 tests in `src/test/java/` mirror the main source structure. Legacy unit tests invoke `main()` methods (these make live HTTP requests and are fragile). New `*IT.java` tests use [WireMock](https://wiremock.org/) to stub upstream HTTP services — see `OkHttpClientIT.java` as the reference pattern. Coverage enforced at 70% via JaCoCo plugin (`jacoco:check`).

## Static Analysis

`make static-check` is the composite fast quality gate and runs in CI:

- `format-check` — [google-java-format](https://github.com/google/google-java-format) drift detection
- `lint` — `mvn validate` + `mvn compile` with `failOnWarning=true`
- `secrets` — [gitleaks](https://github.com/gitleaks/gitleaks) scan for hardcoded secrets
- `trivy-fs` — [Trivy](https://github.com/aquasecurity/trivy) filesystem scan for vuln/secret/misconfig (CRITICAL/HIGH fails build; MEDIUM informational)
- `mermaid-lint` — [mermaid-cli](https://github.com/mermaid-js/mermaid-cli) validates Mermaid blocks in `README.md` (Docker-based)

Run `make format` to auto-apply google-java-format. `cve-check` (OWASP dependency-check) is kept separate — it runs on release tags (`v*`) because of its long runtime.

## Key Config

- **pom.xml** — maven-enforcer-plugin requires Maven 3.6.3+ and Java 25+; JaCoCo 70% threshold; compiler `failOnWarning` enabled; Jackson 3.x (`tools.jackson.core`); OWASP dependency-check bound to build lifecycle (skip with `-Ddependency-check.skip=true`); Failsafe plugin activated via `-P integration-test` profile
- **.mise.toml** — single source of truth for Java (Temurin 25 LTS), Maven 3.9.15, and aqua-backed pins for `act`, `gitleaks`, `trivy`; auto-installed by `make deps`. The Makefile's matching `_VERSION` constants are derived at parse time via `$(shell awk ...)` so the curl fallbacks in `deps-maven` / `deps-act` / `deps-gitleaks` / `deps-trivy` (used inside CI containers with setup-java but no mise) read the same version mise installs locally.
- **.java-version** — single source of truth consumed by GitHub Actions `setup-java`
- **.nvmrc** — Node version for Renovate tooling (`make renovate-bootstrap`)
- **renovate.json** — Automated dependency PRs with automerge on all update types
- **CI pipeline** — `changes` (path-filter detector via `dorny/paths-filter`) → `static-check` → `test` + `integration-test` + `build` (parallel); `cve-check` on release tags, weekly schedule (Mon 06:00 UTC), and manual dispatch, with NVD database cache; `ci-pass` gate aggregates all required jobs for branch protection (skipped doc-only jobs count as pass)

## Upgrade Backlog

Deferred items surfaced by `/upgrade-analysis` — revisit on the next review pass.

- [ ] `.mise.toml` Java tuple is not cleanly tracked by Renovate's `java-version` datasource; bump manually each Adoptium quarterly GA, or switch to `adoptium-java` datasource if Renovate stalls. (Currently pinned at `temurin-25.0.3+9.0.LTS`.)
- [ ] `jdk.incubator.vector` is still an incubator module as of Java 25 — the `--add-modules jdk.incubator.vector` flag in `cve-check` should be removed if/when the module is promoted out of incubator.
- [ ] `maven.compiler.failOnWarning=true` will make JDK deprecation warnings upgrade-blocking on future LTS bumps (Java 29+). Plan a targeted deprecation audit before each LTS bump.
- [ ] Retrofit 3.x ⇔ OkHttp 5.x is the only supported pairing — keep these bumped together. Renovate groups them separately; watch for version drift.
- [ ] JsonPath 3.0.0 is the first 3.x GA (January 2025); watch 3.1 release notes for behavioural changes that may affect `jsonparse/pathqueries/`.
- [ ] JUnit 6.1.0 (currently `6.1.0-RC1`), SLF4J 2.1.0 (alpha), `maven-compiler-plugin` 4.0.0 (currently `beta-4`), `maven-resources-plugin` 4.0.0 (beta), and WireMock 4.0.0 (beta) are all pre-release — adopt after GA.
- [ ] Jackson is on the deliberate cross-major split: `tools.jackson.core:jackson-databind` 3.x reuses the legacy `com.fasterxml.jackson.core:jackson-annotations` 2.x coordinate (Jackson 3 didn't move the annotations package). The `jsonparse/databinding/complex/jackson/generated/` POJOs import `com.fasterxml.jackson.annotation.*` — this is intentional, not a migration leftover.
- [ ] `dorny/paths-filter` v4.0.1 is now available; the project just adopted v3.0.2. v4 has shape changes — let Renovate open the major PR with migration notes attached, do not fast-follow.
- [ ] `RENOVATE_VERSION` tracks the `npm` datasource (not `github-releases`) because Renovate's GitHub releases run ~8 versions ahead of npm publishes (the consumable channel for `npx renovate@<ver>`). If Renovate ever flips publish cadence and the npm package catches up to GitHub, switching the datasource back to `github-releases` is fine. Detection: `make renovate-validate` failing with `npm error notarget No matching version found for renovate@<ver>` means the datasource is mismatched.
- [ ] `NVD_API_KEY` GitHub secret must be set before tagging a release or the tag-gated `cve-check` job will fail (NVD rate limit without a key). The job also runs weekly on schedule and via `workflow_dispatch`.
- [ ] `make deps-update` bulk-bumps dependencies; with Renovate as the source of truth, treat this target as manual-only — consider deleting or guarding with a "bypasses Renovate" warning.

## Skills

Use the following skills when working on related files:

| File(s) | Skill |
|---------|-------|
| `Makefile` | `/makefile` |
| `renovate.json` | `/renovate` |
| `README.md` | `/readme` |
| `.github/workflows/*.{yml,yaml}` | `/ci-workflow` |

When spawning subagents, always pass conventions from the respective skill into the agent's prompt.
