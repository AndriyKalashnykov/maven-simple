[![test](https://github.com/AndriyKalashnykov/maven-simple/actions/workflows/ci.yml/badge.svg)](https://github.com/AndriyKalashnykov/maven-simple/actions/workflows/ci.yml)
[![Hits](https://hits.sh/github.com/AndriyKalashnykov/maven-simple.svg?view=today-total&style=plastic)](https://hits.sh/github.com/AndriyKalashnykov/maven-simple/)
[![License: MIT](https://img.shields.io/badge/License-MIT-brightgreen.svg)](https://opensource.org/licenses/MIT)

# Maven based Java project for general purpose testing

## Pre-requisites

- [sdkman](https://sdkman.io/install)

    Install and use JDK

    ```bash
    sdk install java 21-tem
    sdk use java 21-tem
    ```
- [Apache Maven](https://maven.apache.org/install.html)

    Install Apache Maven

    ```bash
    sdk install maven 3.9.9
    sdk use maven 3.9.9
    ```
- [`GNU Make`](https://www.gnu.org/software/make/)

## [HTTP clients in Java](https://github.com/AndriyKalashnykov/maven-simple/tree/main/src/main/java/http/client)

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

## Usage

```bash
make build              # Build project
make test               # Run tests
make coverage-generate  # Generate JaCoCo coverage report
make coverage-check     # Verify coverage meets minimum threshold (>70%)
make coverage-open      # Open coverage report in browser
make cve-check          # OWASP dependency vulnerability scan
make update-deps        # Update dependencies to latest releases
make check-env          # Verify pre-requisites are installed
```

### OWASP CVE Check

`make cve-check` scans dependencies for known vulnerabilities using two data sources:

- **[NVD](https://nvd.nist.gov/)** — NIST National Vulnerability Database. Without an API key, requests are rate-limited and the scan may fail with a 429 error. Request a free key at https://nvd.nist.gov/developers/request-an-api-key
- **[OSS Index](https://ossindex.sonatype.org/)** — Sonatype's vulnerability database, provides additional coverage beyond NVD. Authentication is now required — without credentials the analyzer is skipped. Register for a free account at https://ossindex.sonatype.org/user/register and get your API token from account settings.

```bash
export NVD_API_KEY=your-key-here
export OSS_INDEX_USER=your-email@example.com
export OSS_INDEX_TOKEN=your-token-here
make cve-check
```

OSS Index credentials are read from env vars via Maven settings. If you don't have `~/.m2/settings.xml`, create one:

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

### Help

```bash
make help
```
