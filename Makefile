.DEFAULT_GOAL := help

APP_NAME   := maven-simple
CURRENTTAG := $(shell git describe --tags --abbrev=0 2>/dev/null || echo "dev")

SHELL      := /bin/bash
SDKMAN     := $${SDKMAN_DIR:-$$HOME/.sdkman}/bin/sdkman-init.sh

# === Tool Versions (pinned) ===
JAVA_VER    := 21-tem
MAVEN_VER   := 3.9.9
ACT_VERSION := 0.2.86

# Detect macOS for 'open' vs 'xdg-open'
OPEN_CMD := $(if $(filter Darwin,$(shell uname -s)),open,xdg-open)

# Semver regex for release validation
SEMVER_RE := ^[0-9]+\.[0-9]+\.[0-9]+$$

#help: @ List available tasks
help:
	@echo "Usage: make COMMAND"
	@echo "Commands :"
	@grep -E '[a-zA-Z\.\-]+:.*?@ .*$$' $(MAKEFILE_LIST)| tr -d '#' | awk 'BEGIN {FS = ":.*?@ "}; {printf "\033[32m%-18s\033[0m - %s\n", $$1, $$2}'

#deps: @ Check that required tools (java, mvn) are installed
deps:
	@command -v java >/dev/null 2>&1 || { echo "Error: java is not installed"; exit 1; }
	@command -v mvn >/dev/null 2>&1 || { echo "Error: mvn is not installed"; exit 1; }
	@echo "All required dependencies are available"

#deps-maven: @ Install Maven if not present (for CI containers)
deps-maven:
	@command -v mvn >/dev/null 2>&1 || { \
		echo "Installing Maven $(MAVEN_VER)..."; \
		curl -fsSL "https://archive.apache.org/dist/maven/maven-3/$(MAVEN_VER)/binaries/apache-maven-$(MAVEN_VER)-bin.tar.gz" | tar xz -C /opt; \
		ln -sf "/opt/apache-maven-$(MAVEN_VER)/bin/mvn" /usr/local/bin/mvn; \
	}

#deps-install: @ Install Java and Maven via SDKMAN
deps-install:
	@if [ ! -f "$(SDKMAN)" ]; then \
		echo "Installing SDKMAN..."; \
		curl -s "https://get.sdkman.io?rcupdate=false" | bash; \
	fi
	@. $(SDKMAN) && \
		echo N | sdk install java $(JAVA_VER) && sdk use java $(JAVA_VER) && \
		echo N | sdk install maven $(MAVEN_VER) && sdk use maven $(MAVEN_VER)

#deps-act: @ Install act for local CI
deps-act: deps
	@command -v act >/dev/null 2>&1 || { echo "Installing act $(ACT_VERSION)..."; \
		curl -sSfL https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash -s -- -b /usr/local/bin v$(ACT_VERSION); \
	}

#env-check: @ Check installed tools
env-check: deps-install
	@printf "\xE2\x9C\x94 sdkman\n"

#clean: @ Cleanup
clean:
	@mvn clean -q

#build: @ Build project
build: deps
	@mvn -B install -Dmaven.test.skip=true -Ddependency-check.skip=true

#test: @ Run project tests
test: deps
	@mvn -B test -Ddependency-check.skip=true

#lint: @ Validate project configuration
lint: deps
	@mvn -B validate -Ddependency-check.skip=true

#ci: @ Run full CI pipeline (lint, build, test, coverage)
ci: deps lint build test coverage-generate coverage-check
	@echo "=== CI Complete ==="

#ci-run: @ Run GitHub Actions workflow locally using act
ci-run: deps-act
	@act push --container-architecture linux/amd64 \
		--artifact-server-path /tmp/act-artifacts \
		$(if $(NVD_API_KEY),--secret NVD_API_KEY=$(NVD_API_KEY)) \
		$(if $(OSS_INDEX_USER),--secret OSS_INDEX_USER=$(OSS_INDEX_USER)) \
		$(if $(OSS_INDEX_TOKEN),--secret OSS_INDEX_TOKEN=$(OSS_INDEX_TOKEN))

#release: @ Create a release (usage: make release VERSION=x.y.z)
release: deps
	@if [ -z "$(VERSION)" ]; then \
		echo "Error: VERSION is required (e.g., make release VERSION=1.0.0)"; \
		exit 1; \
	fi
	@if ! echo "$(VERSION)" | grep -qE '$(SEMVER_RE)'; then \
		echo "Error: VERSION must be valid semver (e.g., 1.0.0)"; \
		exit 1; \
	fi
	@echo "Releasing version $(VERSION) (current: $(CURRENTTAG))..."
	@echo -n "Proceed? [y/N] " && read ans && [ "$${ans:-N}" = y ] || { echo "Aborted."; exit 1; }
	@mvn -B versions:set -DnewVersion=$(VERSION) -DgenerateBackupPoms=false
	@mvn -B clean install -Ddependency-check.skip=true
	@git add -A
	@git commit -m "release: cut $(VERSION)"
	@git tag v$(VERSION)
	@git push origin v$(VERSION)
	@git push
	@echo "Release $(VERSION) complete."

#cve-check: @ Run OWASP dependency vulnerability scan
cve-check: deps
	@mvn -B dependency-check:check $(if $(NVD_API_KEY),-DnvdApiKey=$(NVD_API_KEY))

#coverage-generate: @ Generate code coverage report
coverage-generate: deps
	@mvn -B test -Ddependency-check.skip=true jacoco:report

#coverage-check: @ Verify code coverage meets minimum threshold (>70%)
coverage-check: deps
	@mvn -B jacoco:check

#coverage-open: @ Open code coverage report
coverage-open:
	@$(OPEN_CMD) ./target/site/jacoco/index.html

#deps-updates: @ Print project dependencies updates
deps-updates: deps
	@mvn -B versions:display-dependency-updates

#deps-update: @ Update project dependencies to latest releases
deps-update: deps-updates
	@mvn -B versions:use-latest-releases
	@mvn -B versions:commit

.PHONY: help deps deps-maven deps-install deps-act deps-updates deps-update \
	env-check clean build test lint ci ci-run release \
	cve-check coverage-generate coverage-check coverage-open
