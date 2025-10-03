.DEFAULT_GOAL := help

SHELL := /bin/bash
SDKMAN := $(HOME)/.sdkman/bin/sdkman-init.sh
CURRENT_USER_NAME := $(shell whoami)

JAVA_VER :=  21-tem
MAVEN_VER := 3.9.1

SDKMAN_EXISTS := @printf "sdkman"

IS_DARWIN := 0
IS_LINUX := 0
IS_FREEBSD := 0
IS_WINDOWS := 0
IS_AMD64 := 0
IS_AARCH64 := 0
IS_RISCV64 := 0

# Platform and architecture detection
ifeq ($(OS), Windows_NT)
	IS_WINDOWS := 1
	# Windows architecture detection using PROCESSOR_ARCHITECTURE
	ifeq ($(PROCESSOR_ARCHITECTURE), AMD64)
		IS_AMD64 := 1
	else ifeq ($(PROCESSOR_ARCHITECTURE), x86)
		# 32-bit x86 - you might want to add IS_X86 := 1 if needed
		IS_AMD64 := 0
	else ifeq ($(PROCESSOR_ARCHITECTURE), ARM64)
		IS_AARCH64 := 1
	else
		# Fallback: check PROCESSOR_ARCHITEW6432 for 32-bit processes on 64-bit systems
		ifeq ($(PROCESSOR_ARCHITEW6432), AMD64)
			IS_AMD64 := 1
		else ifeq ($(PROCESSOR_ARCHITEW6432), ARM64)
			IS_AARCH64 := 1
		else
			# Default to AMD64 if unable to determine
			IS_AMD64 := 1
		endif
	endif
else
	# Unix-like systems - detect platform and architecture
	UNAME_S := $(shell uname -s)
	UNAME_M := $(shell uname -m)

	# Platform detection
	ifeq ($(UNAME_S), Darwin)
		IS_DARWIN := 1
	else ifeq ($(UNAME_S), Linux)
		IS_LINUX := 1
	else ifeq ($(UNAME_S), FreeBSD)
		IS_FREEBSD := 1
	else
		$(error Unsupported platform: $(UNAME_S). Supported platforms: Darwin, Linux, FreeBSD, Windows_NT)
	endif

	# Architecture detection
	ifneq (, $(filter $(UNAME_M), x86_64 amd64))
		IS_AMD64 := 1
	else ifneq (, $(filter $(UNAME_M), aarch64 arm64))
		IS_AARCH64 := 1
	else ifneq (, $(filter $(UNAME_M), riscv64))
		IS_RISCV64 := 1
	else
		$(error Unsupported architecture: $(UNAME_M). Supported architectures: x86_64/amd64, aarch64/arm64, riscv64)
	endif
endif

#help: @ List available tasks on this project
help:
	@clear
	@echo "Usage: make COMMAND"
	@echo
	@echo "Commands :"
	@echo
	@grep -E '[a-zA-Z\.\-]+:.*?@ .*$$' $(MAKEFILE_LIST)| tr -d '#' | awk 'BEGIN {FS = ":.*?@ "}; {printf "\033[32m%-18s\033[0m - %s\n", $$1, $$2}'

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
	@ mvn test -Ddependency-check.skip=true

#build: @ Build project
build:
	@ mvn package install -Dmaven.test.skip=true -Ddependency-check.skip=true

# mvn org.owasp:dependency-check-maven:12.1.3:check -DnvdApiKey=${NVD_API_KEY}
#cve-check: @ Run dependencies check for publicly disclosed vulnerabilities in application dependencies
cve-check:
	@mvn dependency-check:check # -DnvdApiKey==${NVD_API_KEY}

#coverage-generate: @ Generate code coverage report
coverage-generate:
	@ mvn test -Ddependency-check.skip=false jacoco:report

#coverage-check: @ Verify code coverage meets minimum threshold ( > 70%)
coverage-check:
	@ mvn jacoco:check

#coverage-open: @ Open code coverage report
coverage-open:
	@ $(if $(filter 1,$(IS_DARWIN)),open,xdg-open) ./target/site/jacoco/index.html

#print-deps-updates: @ Print project dependencies updates
print-deps-updates:
	@ mvn versions:display-dependency-updates

#update-deps: @ Update project dependencies to latest releases
update-deps: print-deps-updates
	@ mvn versions:use-latest-releases
	@ mvn versions:commit