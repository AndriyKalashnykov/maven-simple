[![CI](https://github.com/AndriyKalashnykov/maven-simple/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/AndriyKalashnykov/maven-simple/actions/workflows/ci.yml)
[![Hits](https://hits.sh/github.com/AndriyKalashnykov/maven-simple.svg?view=today-total&style=plastic)](https://hits.sh/github.com/AndriyKalashnykov/maven-simple/)
[![License: MIT](https://img.shields.io/badge/License-MIT-brightgreen.svg)](https://opensource.org/licenses/MIT)
[![Renovate enabled](https://img.shields.io/badge/renovate-enabled-brightgreen.svg)](https://app.renovatebot.com/dashboard#github/AndriyKalashnykov/maven-simple)

# Maven Simple

Educational Java 21 project demonstrating HTTP client implementations and JSON parsing techniques using NASA's Near-Earth Objects (NEO) API data. Built with Maven and tested with JUnit 4.

## Quick Start

```bash
make deps      # verify Java and Maven are installed
make build     # build the project
make test      # run all tests
```

## Prerequisites

| Tool | Version | Purpose |
|------|---------|---------|
| [GNU Make](https://www.gnu.org/software/make/) | 3.81+ | Build orchestration |
| [JDK](https://adoptium.net/) | 21+ | Java runtime and compiler |
| [Maven](https://maven.apache.org/) | 3.9+ | Build and dependency management |
| [SDKMAN](https://sdkman.io/) | latest | Java/Maven version management (optional) |
| [Docker](https://www.docker.com/) | latest | Local CI via [act](https://github.com/nektos/act) (optional) |

Install all required dependencies:

```bash
make deps
```

## Available Make Targets

Run `make help` to see all available targets.

### Build & Run

| Target | Description |
|--------|-------------|
| `make build` | Build project |
| `make test` | Run project tests |
| `make lint` | Validate project configuration |
| `make clean` | Cleanup |

### Code Quality

| Target | Description |
|--------|-------------|
| `make coverage-generate` | Generate code coverage report |
| `make coverage-check` | Verify code coverage meets minimum threshold (>70%) |
| `make coverage-open` | Open code coverage report |
| `make cve-check` | Run OWASP dependency vulnerability scan |

### CI

| Target | Description |
|--------|-------------|
| `make ci` | Run full CI pipeline (lint, test, coverage, build) |
| `make ci-run` | Run GitHub Actions workflow locally using [act](https://github.com/nektos/act) |

### Dependencies

| Target | Description |
|--------|-------------|
| `make deps` | Check that required tools (java, mvn) are installed |
| `make deps-maven` | Install Maven if not present (for CI containers) |
| `make deps-install` | Install Java and Maven via SDKMAN |
| `make deps-act` | Install act for local CI |
| `make deps-updates` | Print project dependencies updates |
| `make deps-update` | Update project dependencies to latest releases |

### Utilities

| Target | Description |
|--------|-------------|
| `make env-check` | Check installed tools |
| `make maven-settings-ossindex` | Create Maven settings for OSS Index credentials |
| `make release VERSION=x.y.z` | Create a release |
| `make renovate-validate` | Validate Renovate configuration |
| `make help` | List available tasks |

## [HTTP Clients in Java](https://github.com/AndriyKalashnykov/maven-simple/tree/main/src/main/java/http/client)

Core Java:
* [HttpURLConnection](https://www.javatpoint.com/java-http-url-connection)
* [HttpClient](https://openjdk.java.net/groups/net/httpclient/intro.html)

Popular Libraries:
* [ApacheHttpClient](https://mkyong.com/java/apache-httpclient-examples/)
* [OkHttp](https://www.baeldung.com/guide-to-okhttp)
* [Retrofit](https://www.baeldung.com/retrofit)

## [JSON in Java](https://github.com/AndriyKalashnykov/maven-simple/tree/main/src/main/java/jsonparse/)

Examples of how to work with JSON using:
* [Jackson](https://github.com/FasterXML/jackson) — tree models, data binding (simple & complex), JsonPointer queries
* [Gson](https://github.com/google/gson) — tree models, data binding (simple & complex)
* [JsonPath](https://github.com/json-path/JsonPath) — path-based queries

## OWASP CVE Check

`make cve-check` scans dependencies for known vulnerabilities using two data sources:

- **[NVD](https://nvd.nist.gov/)** — NIST National Vulnerability Database.
  Without an API key, requests are rate-limited and the scan may fail with a 429 error.
  Request a free key at https://nvd.nist.gov/developers/request-an-api-key

- **[OSS Index](https://ossindex.sonatype.org/)** — Sonatype's vulnerability database, provides additional coverage beyond NVD.
  Authentication is required — without credentials the analyzer is skipped.
  Register for a free account at https://ossindex.sonatype.org/user/register and get your API token from account settings.

```bash
export NVD_API_KEY=your-key-here
export OSS_INDEX_USER=your-email@example.com
export OSS_INDEX_TOKEN=your-token-here
make cve-check
```

The NVD key is passed to Maven automatically via the Makefile. OSS Index credentials are read from env vars via `~/.m2/settings.xml`. If you don't have one, create it:

```xml
<settings>
    <servers>
        <server>
            <id>ossindex</id>
            <username>${env.OSS_INDEX_USER}</username>
            <password>${env.OSS_INDEX_TOKEN}</password>
        </server>
    </servers>
</settings>
```

## CI/CD

GitHub Actions runs on every push to `main`, tags `v*`, and pull requests.

| Job | Triggers | Steps |
|-----|----------|-------|
| **ci** | push, PR, tags | Lint, Test with coverage, Build |
| **cve-check** | push to main | OWASP dependency vulnerability scan |

[Renovate](https://docs.renovatebot.com/) keeps dependencies up to date with platform automerge enabled.
