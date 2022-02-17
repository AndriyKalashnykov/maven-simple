[![test](https://github.com/AndriyKalashnykov/maven-simple/actions/workflows/test.yml/badge.svg)](https://github.com/AndriyKalashnykov/maven-simple/actions/workflows/test.yml)
[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2FAndriyKalashnykov%2Fmaven-simple&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
# Maven based Java project for general purpose testing</br>

## Pre-requisites

- [sdkman](https://sdkman.io/install)

    Install and use JDK 17

    ```bash
    sdk install java 17.0.2-tem
    sdk use java 17.0.2-tem
    ```
- [Apache Maven](https://maven.apache.org/install.html)

    Install Apache Maven 3.8.4

    ```bash
    sdk install maven 3.8.4
    sdk use maven 3.8.4
    ```
- [`GNU Make`](https://www.gnu.org/software/make/)


**This [package](https://github.com/AndriyKalashnykov/maven-simple/tree/main/src/main/java/http/client) has example code** for a few HTTP clients in Java

CORE JAVA:
* HttpURLConnection
* HttpClient

POPULAR LIBRARIES:
* ApacheHttpClient
* OkHttp
* Retrofit

This [package](https://github.com/AndriyKalashnykov/maven-simple/tree/main/src/main/java/jsonparse/) contains examples 
of how to work with JSON in Java using 
* [Jackson](https://github.com/FasterXML/jackson) 
* [Gson](https://github.com/google/gson)

### Help

```bash
make help
```

![make-help](./images/help.png)
