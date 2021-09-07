.DEFAULT_GOAL := help

SHELL := /bin/bash
SDKMAN := $(HOME)/.sdkman/bin/sdkman-init.sh
CURRENT_USER_NAME := $(shell whoami)

JAVA_VER := 11.0.11.hs-adpt
MAVEN_VER := 3.8.2

build-deps-check:
	@source $(SDKMAN)
SDKMAN_EXISTS := @printf "sdkman"
ifndef SDKMAN_VERSION
	@curl -s "https://get.sdkman.io?rcupdate=false" | bash
	@source $(SDKMAN)
	ifndef SDKMAN_VERSION
		SDKMAN_EXISTS := @echo "SDKMAN_VERSION is undefined" && exit 1
	endif
endif

# make sure java is installed
JAVA_EXISTS	:= @printf "java"
JAVA_WHICH	:= $(shell which java)
ifeq ($(strip $(JAVA_WHICH)),)
	@source $(SDKMAN) && echo N | sdk install java $(JAVA_VER) && sdk use java $(JAVA_VER)
	ifeq ($(strip $(JAVA_WHICH)),)
		JAVA_EXISTS := @echo "ERROR: java not found." && exit 1
	endif
endif

# make sure maven is installed
MAVEN_EXISTS := @printf "mvn"
MAVEN_WHICH	:= $(shell which mvn)
ifeq ($(strip $(MAVEN_WHICH)),)
	@source $(SDKMAN) && echo N | sdk install maven $(MAVEN_VER) && sdk use maven $(MAVEN_VER)
	ifeq ($(strip $(MAVEN_WHICH)),)
		JAVA_EXISTS := @echo "ERROR: mvn not found." && exit 1
	endif
endif

#help: @ List available tasks on this project
help:
	@clear
	@echo "Usage: make COMMAND"
	@echo
	@echo "Commands :"
	@echo
	@grep -E '[a-zA-Z\.\-]+:.*?@ .*$$' $(MAKEFILE_LIST)| tr -d '#' | awk 'BEGIN {FS = ":.*?@ "}; {printf "\033[32m%-9s\033[0m - %s\n", $$1, $$2}'

#check-env: @ Check environment variables and installed tools
check-env: build-deps-check

	@printf "\xE2\x9C\x94 "
	$(SDKMAN_EXISTS)
	@printf " "
	$(JAVA_EXISTS)
	@printf " "
	$(MAVEN_EXISTS)
	@printf "\n"

#build: @ Build project
build: check-env
	@source $(SDKMAN) && sdk use java $(JAVA_VER) && sdk use maven $(MAVEN_VER) && mvn clean package install -Dmaven.test.skip=true

#test: @ Run project tests
test: build
	@source $(SDKMAN) && sdk use java $(JAVA_VER) && sdk use maven $(MAVEN_VER) && mvn test