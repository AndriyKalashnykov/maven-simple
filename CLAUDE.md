# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Educational Java project demonstrating HTTP client implementations and JSON parsing techniques using NASA's Near-Earth Objects (NEO) API data. Java 21, Maven, JUnit 5.

## Build & Test Commands

```bash
make help               # List available tasks
make deps               # Check that required tools (java, mvn) are installed
make deps-maven         # Install Maven if not present (for CI containers)
make deps-install       # Install Java and Maven via SDKMAN
make deps-act           # Install act for local CI
make deps-check         # Show required tools and installation status
make build              # Build project (skips tests and OWASP dependency-check)
make test               # Run all tests
make lint               # Validate project configuration
make clean              # Cleanup
make ci                 # Full CI pipeline (lint, test, coverage, build)
make ci-run             # Run GitHub Actions workflow locally using act
make coverage-generate  # Generate JaCoCo coverage report
make coverage-check     # Verify coverage meets 70% threshold
make coverage-open      # Open code coverage report
make cve-check          # OWASP CVE scan (slow, not part of normal workflow)
make deps-updates       # Print available dependency updates
make deps-update        # Update dependencies to latest releases
make release VERSION=x.y.z  # Tag and push a release
make renovate-bootstrap # Install nvm and npm for Renovate
make renovate-validate  # Validate Renovate configuration

# Run a single test class (raw mvn)
mvn -B test -Dtest=ClassName -Ddependency-check.skip=true
```

## Architecture

Two independent module areas under `src/main/java/`:

- **`http/client/`** — Five HTTP client implementations (Java HttpURLConnection, Java HttpClient, Apache HttpClient 5, OkHttp3, Retrofit) with shared models in `model/`
- **`jsonparse/`** — JSON processing examples organized by approach:
  - `treemodels/` — DOM-style parsing (Jackson JsonNode, Gson JsonElement)
  - `databinding/simple/` — POJO mapping with Jackson and Gson
  - `databinding/complex/` — Generated model classes for full NASA NEO response (separate `jackson/generated/` and `gson/generated/` packages)
  - `pathqueries/` — JsonPath and Jackson JsonPointer queries

## Testing

JUnit 5 tests in `src/test/java/` mirror the main source structure. Tests typically invoke `main()` methods of example classes. Coverage enforced at 70% via JaCoCo plugin (`jacoco:check`).

## Key Config

- **pom.xml** — maven-enforcer-plugin requires Maven 3+ and Java 11+; JaCoCo 70% threshold; OWASP dependency-check bound to build lifecycle (skip with `-Ddependency-check.skip=true`)
- **renovate.json** — Automated dependency PRs with automerge on all update types
- **CI pipeline** — Multi-job: `lint` (+ Trivy SAST) → `tests` + `builds` (parallel); `cve-check` on push to main

## Skills

Use the following skills when working on related files:

| File(s) | Skill |
|---------|-------|
| `Makefile` | `/makefile` |
| `renovate.json` | `/renovate` |
| `README.md` | `/readme` |
| `.github/workflows/*.yml` | `/ci-workflow` |

When spawning subagents, always pass conventions from the respective skill into the agent's prompt.
