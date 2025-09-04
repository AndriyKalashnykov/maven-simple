.DEFAULT_GOAL := help

SHELL := /bin/bash
SDKMAN := $(HOME)/.sdkman/bin/sdkman-init.sh
CURRENT_USER_NAME := $(shell whoami)

JAVA_VER :=  21-tem
MAVEN_VER := 3.9.1

SDKMAN_EXISTS := @printf "sdkman"

#help: @ List available tasks on this project
help:
	@clear
	@echo "Usage: make COMMAND"
	@echo
	@echo "Commands :"
	@echo
	@grep -E '[a-zA-Z\.\-]+:.*?@ .*$$' $(MAKEFILE_LIST)| tr -d '#' | awk 'BEGIN {FS = ":.*?@ "}; {printf "\033[32m%-17s\033[0m - %s\n", $$1, $$2}'

build-deps-check:
	@. $(SDKMAN)
ifndef SDKMAN_DIR
	@curl -s "https://get.sdkman.io?rcupdate=false" | bash
	@source $(SDKMAN)
	ifndef SDKMAN_DIR
		SDKMAN_EXISTS := @echo "SDKMAN_VERSION is undefined" && exit 1
	endif
endif

	@. $(SDKMAN) && echo N | sdk install java $(JAVA_VER) && sdk use java $(JAVA_VER)
	@. $(SDKMAN) && echo N | sdk install maven $(MAVEN_VER) && sdk use maven $(MAVEN_VER)

#check-env: @ Check installed tools
check-env: build-deps-check

	@printf "\xE2\x9C\x94 "
	$(SDKMAN_EXISTS)
	@printf "\n"

#clean: @ Cleanup
clean:
	@ mvn clean

#test: @ Run project tests
test: build
	@. mvn test

#build: @ Build project
build:
	@ mvn package -Dmaven.test.skip=true

# mvn org.owasp:dependency-check-maven:12.1.3:check -DnvdApiKey=${NVD_API_KEY}
#cve-check: @ Run dependencies check for publicly disclosed vulnerabilities in application dependencies
cve-check:
	@mvn dependency-check:check # -DnvdApiKey==${NVD_API_KEY}

#coverage-generate: @ Generate code coverage report
coverage-generate:
	@ mvn jacoco:report

#coverage-check: @ Verify code coverage meets minimum threshold ( > 70%)
coverage-check:
	@ mvn jacoco:check

#coverage-open: @ Open code coverage report
coverage-open:
	@ xdg-open target/site/jacoco/index.html

