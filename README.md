[![test](https://github.com/AndriyKalashnykov/maven-simple/actions/workflows/test.yml/badge.svg)](https://github.com/AndriyKalashnykov/maven-simple/actions/workflows/test.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-brightgreen.svg)](https://opensource.org/licenses/MIT)
[![Renovate enabled](https://img.shields.io/badge/renovate-enabled-brightgreen.svg)](https://app.renovatebot.com/dashboard#github/AndriyKalashnykov/maven-simple)
# Maven based Java project for general purpose testing</br>

## Pre-requisites

- [sdkman](https://sdkman.io/install)

    Install and use JDK 19

    ```bash
    sdk install java 19-tem
    sdk use java 19-tem
    ```
- [Apache Maven](https://maven.apache.org/install.html)

    Install Apache Maven 3.9.1

    ```bash
    sdk install maven 3.9.1
    sdk use maven 3.9.1
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

### Help

```bash
make help
```

![make-help](./images/help.png)
