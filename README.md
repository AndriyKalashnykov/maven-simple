[![test](https://github.com/AndriyKalashnykov/maven-simple/actions/workflows/ci.yml/badge.svg)](https://github.com/AndriyKalashnykov/maven-simple/actions/workflows/ci.yml)
[![Hits](https://hits.sh/github.com/AndriyKalashnykov/maven-simple.svg?view=today-total&style=plastic)](https://hits.sh/github.com/AndriyKalashnykov/maven-simple/)
[![License: MIT](https://img.shields.io/badge/License-MIT-brightgreen.svg)](https://opensource.org/licenses/MIT)
# Maven based Java project for general purpose testing</br>

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
of how to work with  using 
* [Jackson](https://github.com/FasterXML/jackson) 
* [Gson](https://github.com/google/gson)

## Usage

Check pre-reqs:
```bash
make check-env
```

Run dependencies check for publicly disclosed vulnerabilities in application dependencies:
```bash
make cve-check
```

Update project dependencies to latest releases
```bash
mvn update-deps
```

### Help

```bash
make help
```

```text
Usage: make COMMAND

Commands :

help               - List available tasks on this project
check-env          - Check installed tools
clean              - Cleanup
test               - Run project tests
build              - Build project
cve-check          - Run dependencies check for publicly disclosed vulnerabilities in application dependencies
coverage-generate  - Generate code coverage report
coverage-check     - Verify code coverage meets minimum threshold ( > 70%)
coverage-open      - Open code coverage report
print-deps-updates - Print project dependencies updates
update-deps        - Update project dependencies to latest releases
```
