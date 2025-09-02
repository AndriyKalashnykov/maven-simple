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
	@grep -E '[a-zA-Z\.\-]+:.*?@ .*$$' $(MAKEFILE_LIST)| tr -d '#' | awk 'BEGIN {FS = ":.*?@ "}; {printf "\033[32m%-9s\033[0m - %s\n", $$1, $$2}'

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

#check-env: @ Check environment variables and installed tools
check-env: build-deps-check

	@printf "\xE2\x9C\x94 "
	$(SDKMAN_EXISTS)
	@printf "\n"

#build: @ Build project
build: check-env
	@. $(SDKMAN) && sdk use java $(JAVA_VER) && sdk use maven $(MAVEN_VER) && mvn clean package install -Dmaven.test.skip=true

#test: @ Run project tests
test: build
	@. $(SDKMAN) && sdk use java $(JAVA_VER) && sdk use maven $(MAVEN_VER) && mvn test

# mvn org.owasp:dependency-check-maven:12.1.3:check -DnvdApiKey=${NVD_API_KEY}
#dep-check: @ Run dependencies check - publicly disclosed vulnerabilities in application dependencies
dep-check:
	@mvn dependency-check:check # -DnvdApiKey==${NVD_API_KEY}