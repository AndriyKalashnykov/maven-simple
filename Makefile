.DEFAULT_GOAL := help

SHELL  				:= /bin/bash
SDKMAN				:= $(HOME)/.sdkman/bin/sdkman-init.sh
CURRENT_USER_NAME	:= $(shell whoami)

JAVA_VERSION 		:= 	11.0.11.hs-adpt
MAVEN_VERSION		:= 	3.8.1

build-deps-check:
	@echo "User: $(CURRENT_USER_NAME)"
	@source $(SDKMAN)
SDKMAN_EXISTS	:= @printf "sdkman"
ifndef SDKMAN_VERSION
	SDKMAN_EXISTS := @echo "SDKMAN_VERSION is undefined" && exit 1
endif

# make sure java is installed
JAVA_EXISTS	:= @printf "java"
JAVA_WHICH	:= $(shell which java)
ifeq ($(strip $(JAVA_WHICH)),)
	JAVA_EXISTS := @echo "ERROR: java not found." && exit 1
endif

# make sure maven is installed
MAVEN_EXISTS := @printf "mvn"
MAVEN_WHICH	:= $(shell which mvn)
ifeq ($(strip $(MAVEN_WHICH)),)
	JAVA_EXISTS := @echo "ERROR: mvn not found." && exit 1
endif

#help: @ List available tasks on this project
help:
	@clear
	@echo "Usage: make COMMAND"
	@echo
	@echo "Commands :"
	@echo
	@grep -E '[a-zA-Z\.\-]+:.*?@ .*$$' $(MAKEFILE_LIST)| tr -d '#' | awk 'BEGIN {FS = ":.*?@ "}; {printf "\033[32m%-18s\033[0m - %s\n", $$1, $$2}'

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
build:
	@