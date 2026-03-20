.DEFAULT_GOAL := help

SHELL := /bin/bash
SDKMAN := $(HOME)/.sdkman/bin/sdkman-init.sh

JAVA_VER := 21-tem
MAVEN_VER := 3.9.9

# Detect macOS for 'open' vs 'xdg-open'
UNAME_S := $(shell uname -s 2>/dev/null)
ifeq ($(UNAME_S), Darwin)
	OPEN_CMD := open
else
	OPEN_CMD := xdg-open
endif

.PHONY: help check-env build-deps-check clean test build cve-check \
	coverage-generate coverage-check coverage-open print-deps-updates update-deps

#help: @ List available tasks on this project
help:
	@clear
	@echo "Usage: make COMMAND"
	@echo
	@echo "Commands :"
	@echo
	@grep -E '[a-zA-Z\.\-]+:.*?@ .*$$' $(MAKEFILE_LIST)| tr -d '#' | awk 'BEGIN {FS = ":.*?@ "}; {printf "\033[32m%-18s\033[0m - %s\n", $$1, $$2}'

build-deps-check:
	@if [ ! -f "$(SDKMAN)" ]; then \
		echo "Installing SDKMAN..."; \
		curl -s "https://get.sdkman.io?rcupdate=false" | bash; \
	fi
	@. $(SDKMAN) && echo N | sdk install java $(JAVA_VER) && sdk use java $(JAVA_VER)
	@. $(SDKMAN) && echo N | sdk install maven $(MAVEN_VER) && sdk use maven $(MAVEN_VER)

#check-env: @ Check installed tools
check-env: build-deps-check
	@printf "\xE2\x9C\x94 sdkman\n"

#clean: @ Cleanup
clean:
	@mvn clean

#test: @ Run project tests
test:
	@mvn test -Ddependency-check.skip=true

#build: @ Build project
build:
	@mvn package install -Dmaven.test.skip=true -Ddependency-check.skip=true

#cve-check: @ Run dependencies check for publicly disclosed vulnerabilities in application dependencies
cve-check:
	@mvn dependency-check:check $(if $(NVD_API_KEY),-DnvdApiKey=$(NVD_API_KEY))

#coverage-generate: @ Generate code coverage report
coverage-generate:
	@mvn test -Ddependency-check.skip=true jacoco:report

#coverage-check: @ Verify code coverage meets minimum threshold ( > 70%)
coverage-check:
	@mvn jacoco:check

#coverage-open: @ Open code coverage report
coverage-open:
	@$(OPEN_CMD) ./target/site/jacoco/index.html

#print-deps-updates: @ Print project dependencies updates
print-deps-updates:
	@mvn versions:display-dependency-updates

#update-deps: @ Update project dependencies to latest releases
update-deps: print-deps-updates
	@mvn versions:use-latest-releases
	@mvn versions:commit
