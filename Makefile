.DEFAULT_GOAL := help

APP_NAME   := maven-simple
CURRENTTAG := $(shell git describe --tags --abbrev=0 2>/dev/null || echo "dev")

SHELL := /bin/bash
export PATH := $(HOME)/.local/bin:$(PATH)

# === Tool Versions (pinned) ===
# Java and Maven pins live in .mise.toml; MAVEN_VER here only backs the
# deps-maven fallback used inside act/CI containers that lack mise.
# renovate: datasource=maven depName=org.apache.maven:apache-maven
MAVEN_VER        := 3.9.14
# renovate: datasource=github-releases depName=nektos/act extractVersion=^v(?<version>.*)$
ACT_VERSION      := 0.2.87
# renovate: datasource=github-releases depName=renovatebot/renovate extractVersion=^v(?<version>.*)$
RENOVATE_VERSION := 43.119.0
# renovate: datasource=github-releases depName=google/google-java-format extractVersion=^v(?<version>.*)$
GJF_VERSION      := 1.35.0
# renovate: datasource=github-releases depName=gitleaks/gitleaks extractVersion=^v(?<version>.*)$
GITLEAKS_VERSION := 8.30.1
# renovate: datasource=github-releases depName=aquasecurity/trivy extractVersion=^v(?<version>.*)$
TRIVY_VERSION    := 0.69.3
# renovate: datasource=docker depName=minlag/mermaid-cli
MERMAID_CLI_VERSION := 11.12.0

# File-derived versions (source of truth = idiomatic dotfiles)
NODE_VERSION := $(shell cat .nvmrc 2>/dev/null || echo 22)
JAVA_VERSION := $(shell cat .java-version 2>/dev/null || echo 21)

# Derived paths
GJF_JAR  := $(HOME)/.local/share/google-java-format-$(GJF_VERSION).jar
LOCAL_BIN := $(HOME)/.local/bin

# Detect macOS for 'open' vs 'xdg-open'
OPEN_CMD := $(if $(filter Darwin,$(shell uname -s)),open,xdg-open)

# Semver regex for release validation
SEMVER_RE := ^[0-9]+\.[0-9]+\.[0-9]+$$

#help: @ List available tasks
help:
	@echo "Usage: make COMMAND"
	@echo "Commands :"
	@grep -E '[a-zA-Z\.\-]+:.*?@ .*$$' $(MAKEFILE_LIST)| tr -d '#' | awk 'BEGIN {FS = ":.*?@ "}; {printf "\033[32m%-30s\033[0m - %s\n", $$1, $$2}'

#deps: @ Check required tools; auto-install mise (no root) and mise-pinned tools if missing
deps:
	@if [ -z "$$CI" ] && ! command -v mise >/dev/null 2>&1; then \
		echo "Installing mise (no root required, installs to ~/.local/bin)..."; \
		curl -fsSL https://mise.run | sh; \
		echo ""; \
		echo "mise installed. Activate it in your shell, then re-run 'make deps':"; \
		echo '  bash: echo '\''eval "$$(~/.local/bin/mise activate bash)"'\'' >> ~/.bashrc'; \
		echo '  zsh:  echo '\''eval "$$(~/.local/bin/mise activate zsh)"''  >> ~/.zshrc'; \
		exit 0; \
	fi
	@if [ -z "$$CI" ] && command -v mise >/dev/null 2>&1; then mise install; fi
	@command -v java >/dev/null 2>&1 || mise exec -- command -v java >/dev/null 2>&1 || { echo "Error: Java required. Run: make deps-install"; exit 1; }
	@command -v mvn  >/dev/null 2>&1 || mise exec -- command -v mvn  >/dev/null 2>&1 || { echo "Error: Maven required. Run: make deps-install"; exit 1; }
	@echo "All required dependencies are available"

#deps-maven: @ Install Maven into ~/.local (CI fallback when setup-java is unavailable)
deps-maven:
	@command -v mvn >/dev/null 2>&1 || { \
		echo "Installing Maven $(MAVEN_VER) into $(HOME)/.local..."; \
		mkdir -p $(HOME)/.local/opt $(LOCAL_BIN); \
		curl -fsSL "https://archive.apache.org/dist/maven/maven-3/$(MAVEN_VER)/binaries/apache-maven-$(MAVEN_VER)-bin.tar.gz" | tar xz -C $(HOME)/.local/opt; \
		ln -sf $(HOME)/.local/opt/apache-maven-$(MAVEN_VER)/bin/mvn $(LOCAL_BIN)/mvn; \
	}

#deps-install: @ Install Java and Maven via mise (reads .mise.toml)
deps-install:
	@if [ -z "$$CI" ]; then \
		command -v mise >/dev/null 2>&1 || { echo "Installing mise..."; curl -fsSL https://mise.run | sh; }; \
		mise install; \
		echo ""; \
		echo "Tools installed. If this is a fresh install, activate mise in your shell:"; \
		echo "  bash: echo 'eval \"\$$(~/.local/bin/mise activate bash)\"' >> ~/.bashrc"; \
		echo "  zsh:  echo 'eval \"\$$(~/.local/bin/mise activate zsh)\"'  >> ~/.zshrc"; \
	else \
		echo "CI environment detected; skipping mise install (toolchain provided by workflow)."; \
	fi

#deps-act: @ Install act for local CI (installs to ~/.local/bin, no root)
deps-act: deps
	@command -v act >/dev/null 2>&1 || { \
		echo "Installing act $(ACT_VERSION) into $(LOCAL_BIN)..."; \
		mkdir -p $(LOCAL_BIN); \
		curl -sSfL https://raw.githubusercontent.com/nektos/act/master/install.sh | bash -s -- -b $(LOCAL_BIN) v$(ACT_VERSION); \
	}

#deps-gitleaks: @ Install gitleaks if missing (installs to ~/.local/bin)
deps-gitleaks:
	@command -v gitleaks >/dev/null 2>&1 || { \
		echo "Installing gitleaks $(GITLEAKS_VERSION) into $(LOCAL_BIN)..."; \
		mkdir -p $(LOCAL_BIN); \
		OS=$$(uname -s | tr '[:upper:]' '[:lower:]'); \
		ARCH=$$(uname -m); case "$$ARCH" in aarch64) ARCH=arm64 ;; x86_64) ARCH=x64 ;; esac; \
		curl -fsSL "https://github.com/gitleaks/gitleaks/releases/download/v$(GITLEAKS_VERSION)/gitleaks_$(GITLEAKS_VERSION)_$${OS}_$${ARCH}.tar.gz" \
			| tar xz -C $(LOCAL_BIN) gitleaks; \
	}

#deps-trivy: @ Install trivy if missing (installs to ~/.local/bin)
deps-trivy:
	@command -v trivy >/dev/null 2>&1 || { \
		echo "Installing trivy $(TRIVY_VERSION) into $(LOCAL_BIN)..."; \
		mkdir -p $(LOCAL_BIN); \
		curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/install.sh \
			| sh -s -- -b $(LOCAL_BIN) v$(TRIVY_VERSION); \
	}

#deps-check: @ Show required tools and installation status
deps-check:
	@echo "--- Tool status ---"
	@for tool in java mvn node act mise gitleaks trivy; do \
		printf "  %-16s " "$$tool:"; \
		command -v $$tool >/dev/null 2>&1 && echo "installed" || echo "NOT installed"; \
	done
	@echo "--- mise ---"
	@command -v mise >/dev/null 2>&1 && mise --version || echo "  NOT installed (run: make deps-install)"

#clean: @ Cleanup
clean:
	@command -v mvn >/dev/null 2>&1 && mvn -B clean -q || rm -rf target

#build: @ Build project (skips tests and OWASP dependency-check)
build: deps
	@mvn -B package -Dmaven.test.skip=true -Ddependency-check.skip=true

#test: @ Run project tests (unit)
test: deps
	@mvn -B test -Ddependency-check.skip=true

#integration-test: @ Run integration tests (WireMock-stubbed HTTP clients; *IT.java)
integration-test: deps
	@mvn -B verify -P integration-test -Ddependency-check.skip=true

#lint: @ Validate project configuration and check compiler warnings
lint: deps
	@mvn -B validate -Ddependency-check.skip=true
	@mvn -B compile -Dmaven.compiler.failOnWarning=true -Ddependency-check.skip=true -q

$(GJF_JAR):
	@mkdir -p $(dir $@)
	@echo "Downloading google-java-format $(GJF_VERSION)..."
	@curl -fsSL -o $@ "https://github.com/google/google-java-format/releases/download/v$(GJF_VERSION)/google-java-format-$(GJF_VERSION)-all-deps.jar"

#format: @ Format Java sources with google-java-format
format: $(GJF_JAR)
	@find src -name '*.java' -print0 | xargs -0 java -jar $(GJF_JAR) --replace

#format-check: @ Verify Java sources are formatted (fails on drift)
format-check: $(GJF_JAR)
	@find src -name '*.java' -print0 | xargs -0 java -jar $(GJF_JAR) --dry-run --set-exit-if-changed

#secrets: @ Scan repository for hardcoded secrets (gitleaks)
secrets: deps-gitleaks
	@gitleaks detect --source . --verbose --redact --no-banner

#trivy-fs: @ Filesystem vulnerability/secret/misconfig scan (CRITICAL/HIGH fails build)
trivy-fs: deps-trivy
	@trivy fs --scanners vuln,secret,misconfig --severity CRITICAL,HIGH --exit-code 1 .
	@trivy fs --scanners vuln,secret,misconfig --severity MEDIUM --exit-code 0 .

#mermaid-lint: @ Validate Mermaid diagrams in Markdown (Docker; skipped under act)
mermaid-lint:
	@if [ "$$ACT" = "true" ]; then \
		echo "Skipping mermaid-lint under act (docker-in-docker unavailable)"; \
		exit 0; \
	fi; \
	command -v docker >/dev/null 2>&1 || { echo "Error: docker required for mermaid-lint"; exit 1; }; \
	grep -l '^```mermaid' README.md 2>/dev/null >/dev/null || { echo "No Mermaid blocks found; skipping."; exit 0; }; \
	tmpdir=$$(mktemp -d); \
	cp README.md $$tmpdir/; \
	docker run --rm -u $$(id -u):$$(id -g) -v $$tmpdir:/data minlag/mermaid-cli:$(MERMAID_CLI_VERSION) \
		-i /data/README.md -o /data/out.md >/dev/null 2>&1; \
	rc=$$?; rm -rf $$tmpdir; \
	if [ $$rc -ne 0 ]; then echo "Mermaid lint FAILED"; exit 1; fi; \
	echo "Mermaid lint OK"

#deps-prune: @ Analyze declared-but-unused / used-but-undeclared dependencies
deps-prune: deps
	@mvn -B dependency:analyze -Ddependency-check.skip=true

#deps-prune-check: @ Fail build on declared-but-unused dependencies
deps-prune-check: deps
	@mvn -B dependency:analyze-only -DfailOnWarning=true -Ddependency-check.skip=true

#static-check: @ Composite fast quality gate (format-check + lint + secrets + trivy-fs + mermaid-lint)
static-check: format-check lint secrets trivy-fs mermaid-lint
	@echo "=== static-check OK ==="

#vulncheck: @ Alias for cve-check (canonical target name)
vulncheck: cve-check

#cve-check: @ Run OWASP dependency vulnerability scan
cve-check: deps maven-settings-ossindex
	@MAVEN_OPTS="$${MAVEN_OPTS:-} --add-modules jdk.incubator.vector" \
		mvn -B dependency-check:check $$([ -n "$$NVD_API_KEY" ] && echo "-DnvdApiKey=$$NVD_API_KEY")

#coverage-generate: @ Generate code coverage report
coverage-generate: deps
	@mvn -B test -Ddependency-check.skip=true jacoco:report

#coverage-check: @ Verify code coverage meets minimum threshold (>70%)
coverage-check: deps
	@mvn -B jacoco:check -Ddependency-check.skip=true

#coverage-open: @ Open code coverage report
coverage-open:
	@$(OPEN_CMD) ./target/site/jacoco/index.html

#ci: @ Run full CI pipeline (static-check, test, integration-test, coverage-check, build)
ci: deps static-check test integration-test coverage-check build
	@echo "=== CI Complete ==="

#ci-run: @ Run GitHub Actions workflow locally using act
ci-run: deps-act
	@docker container prune -f 2>/dev/null || true
	@act push -W .github/workflows/ci.yml \
		--container-architecture linux/amd64 \
		--artifact-server-path /tmp/act-artifacts \
		--var ACT=true \
		$$([ -n "$$NVD_API_KEY" ] && echo "--secret NVD_API_KEY=$$NVD_API_KEY") \
		$$([ -n "$$OSS_INDEX_USER" ] && echo "--secret OSS_INDEX_USER=$$OSS_INDEX_USER") \
		$$([ -n "$$OSS_INDEX_TOKEN" ] && echo "--secret OSS_INDEX_TOKEN=$$OSS_INDEX_TOKEN")

#release: @ Create a release (usage: make release VERSION=x.y.z)
release: deps
	@if [ -z "$(VERSION)" ]; then echo "Error: VERSION is required (e.g., make release VERSION=1.0.0)"; exit 1; fi
	@if ! echo "$(VERSION)" | grep -qE '$(SEMVER_RE)'; then \
		echo "Error: VERSION must be valid semver (e.g., 1.0.0 → creates tag v1.0.0)"; exit 1; \
	fi
	@echo "Releasing version $(VERSION) (current: $(CURRENTTAG))..."
	@echo -n "Proceed? [y/N] " && read ans && [ "$${ans:-N}" = y ] || { echo "Aborted."; exit 1; }
	@mvn -B versions:set -DnewVersion=$(VERSION) -DgenerateBackupPoms=false
	@mvn -B clean install -Ddependency-check.skip=true
	@git add pom.xml
	@git commit -m "release: cut $(VERSION)"
	@git tag v$(VERSION)
	@git push origin v$(VERSION)
	@git push
	@echo "Release $(VERSION) complete."

#maven-settings-ossindex: @ Create Maven settings for OSS Index credentials (no-op without env)
maven-settings-ossindex:
	@if [ -n "$$OSS_INDEX_USER" ] && [ -n "$$OSS_INDEX_TOKEN" ]; then \
		mkdir -p ~/.m2 && \
		printf '<settings>\n  <servers>\n    <server>\n      <id>ossindex</id>\n      <username>%s</username>\n      <password>%s</password>\n    </server>\n  </servers>\n</settings>\n' \
			"$$OSS_INDEX_USER" "$$OSS_INDEX_TOKEN" > ~/.m2/settings.xml; \
	fi

#renovate-bootstrap: @ Install mise + Node for Renovate
renovate-bootstrap:
	@command -v mise >/dev/null 2>&1 || { \
		echo "Installing mise (no root required, installs to ~/.local/bin)..."; \
		curl -fsSL https://mise.run | sh; \
	}
	@command -v node >/dev/null 2>&1 || { \
		echo "Installing Node $(NODE_VERSION) via mise..."; \
		mise install node@$(NODE_VERSION); \
	}

#renovate-validate: @ Validate Renovate configuration
renovate-validate: renovate-bootstrap
	@[ -f renovate.json ] || { echo "Error: renovate.json not found"; exit 1; }
	@if [ -n "$$GH_ACCESS_TOKEN" ]; then \
		GITHUB_COM_TOKEN=$$GH_ACCESS_TOKEN npx --yes renovate@$(RENOVATE_VERSION) --platform=local; \
	else \
		echo "Warning: GH_ACCESS_TOKEN not set, some dependency lookups may fail"; \
		npx --yes renovate@$(RENOVATE_VERSION) --platform=local; \
	fi

#deps-updates: @ Print project dependencies updates
deps-updates: deps
	@mvn -B versions:display-dependency-updates

#deps-update: @ Update project dependencies to latest releases
deps-update: deps-updates
	@mvn -B versions:use-latest-releases
	@mvn -B versions:commit

.PHONY: help deps deps-maven deps-install deps-act deps-gitleaks deps-trivy deps-check \
	deps-updates deps-update deps-prune deps-prune-check \
	clean build test integration-test lint format format-check secrets trivy-fs mermaid-lint static-check \
	ci ci-run release vulncheck cve-check \
	coverage-generate coverage-check coverage-open \
	maven-settings-ossindex renovate-bootstrap renovate-validate
