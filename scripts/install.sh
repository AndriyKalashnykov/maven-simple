#!/bin/bash

#set -x

LAUNCH_DIR=$(pwd); SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"; cd $SCRIPT_DIR; cd ..; SCRIPT_PARENT_DIR=$(pwd);

cd $SCRIPT_DIR

# Install java/maven with sdkman
# ./install.sh latest .hs-adpt 11. latest no no
#./install.sh 11.0.10.hs-adpt .hs-adpt 11. latest no no
# 11.0.11.hs-adpt,11.0.10.hs-adpt,11.0.9.hs-adpt

export SDKMAN_DIR="$HOME/.sdkman"

if [ -z ${SDKMAN_DIR+x} ] || [[ ! -d "$SDKMAN_DIR" ]] ; then
    echo "sdkman not detected, installing it"
    curl -s "https://get.sdkman.io?rcupdate=false" | bash
else
  echo "Sdkman! already installed."
fi
# Bring 'sdk' function into scope
. "$SDKMAN_DIR/bin/sdkman-init.sh"

JAVA_VER=${1:-"latest"}
JAVA_VER_DIST=${2:-".hs-adpt"}
JAVA_VER_MAJOR=${3:-"11."}
MAVEN_VER=${4:-"latest"}
SET_JAVA_VER_DEFAULT=${5:-no}
SET_MAVEN_VER_DEFAULT=${6:-no}

# get latest available sdkman java version
LATEST_SDKMAN_JAVA_VER=$(sdk ls java | grep "$JAVA_VER_DIST" | grep "$JAVA_VER_MAJOR" -m1 | cut -c 62-)

delete_for_each() {
  versions=$*;
  for version in $versions
  do
    sdk uninstall java "$version"
  done
}

install_for_each() {
  versions=$*;
  for version in $versions
  do
    install_java_using_sdk "$version"
  done
}

# install a candidate using a partial version match
sdk_install() {
    local install_type=$1
    local requested_version=$2
    local prefix=$3
    local suffix="${4:-"\\s*"}"
    local full_version_check=${5:-".*-[a-z]+"}

    echo "${install_type}" "${requested_version}"

    if [ "${requested_version}" = "none" ]; then return; fi
    # Blank will install latest stable version
    if [ "${requested_version}" = "lts" ] || [ "${requested_version}" = "default" ]; then
         requested_version=""
    elif echo "${requested_version}" | grep -oE "${full_version_check}" > /dev/null 2>&1; then
        echo "install_type: ${install_type} requested_version: ${requested_version}"
    else
#        local regex="${prefix}\\K[0-9]+\\.[0-9]+\\.[0-9]+${suffix}"
#        local regex="${prefix}\\K([0-9a-zA-Z]+\.)?([0-9a-zA-Z\\-]+\.)?([0-9a-zA-Z\\-]+)${suffix}"
        local regex="${prefix}\\K(([0-9a-zA-Z]+\.)?([0-9a-zA-Z\-]+\.)?([0-9a-zA-Z\-]+)[\w.-]*\d)${suffix}"
        echo $regex

        local version_list="$(. ${SDKMAN_DIR}/bin/sdkman-init.sh && sdk list ${install_type} 2>&1 | grep -oP "${regex}" | tr -d ' ' | sort -rV)"
        echo ${version_list}

        if [ "${requested_version}" = "latest" ] || [ "${requested_version}" = "current" ]; then
            requested_version="$(echo "${version_list}" | head -n 1)"
        else
            set +e
            requested_version="$(echo "${version_list}" | grep -E -m 1 "^${requested_version//./\\.}([\\.\\s]|$)")"
            set -e
        fi
        if [ -z "${requested_version}" ] || ! echo "${version_list}" | grep "^${requested_version//./\\.}$" > /dev/null 2>&1; then
            echo -e "Version $2 not found. Available versions:\n${version_list}" >&2
            exit 1
        fi
    fi

    echo $requested_version

#if [ "${install_type}" == "java" ]; then
#      # install java
#      if [ "$(sdk list java | grep -v "local only" | grep "$requested_version" | grep -v "sdk install" | grep -v "installed" | wc -l)" == "1" ]; then
#        echo $SET_JAVA_VER_DEFAULT | sdk install java $requested_version
#      fi
#
#      # use java $requested_version in this shell
#      if [ "$(sdk current java | grep -c "$requested_version")" != "1" ]; then
#        sdk use java $requested_version
#      fi
#
#      #  set java $requested_version as default
#      if [ "$SET_JAVA_VER_DEFAULT" == "yes" ]; then
#        sdk default java $requested_version
#      fi
#fi
#
#if [ "${install_type}" == "maven" ]; then
#    # install maven
#    if [ "$(sdk list maven | grep -v "local only" | grep "$requested_version" | grep -v "*" | grep -v "+" | wc -l)" == "1" ]; then
#      echo $SET_MAVEN_VER_DEFAULT | sdk install maven $requested_version
#    fi
#
#    # use maven $requested_version in this shell
#    if [ "$(sdk current maven | grep -c "$requested_version")" != "1" ]; then
#      sdk use maven $requested_version
#    fi
#
#    # set maven $requested_version as default
#    if [ "$SET_MAVEN_VER_DEFAULT" == "yes" ]; then
#      sdk default maven $requested_version
#    fi
#fi
#
#if [ "${install_type}" != "java" ] && [ "${install_type}" != "maven" ]; then
#    echo "Installing candidate: $install_type"
#    . ${SDKMAN_DIR}/bin/sdkman-init.sh && sdk install "${install_type}" "${requested_version}"
#fi
}




#vl=$(grep -oP "(([0-9a-zA-Z]+\.)?([0-9a-zA-Z\-]+\.)?([0-9a-zA-Z\-]+)[\w.-]*\d)" $SCRIPT_DIR/gradle | tr -d ' ' | sort -rV)
#echo $vl
#vl2=$(echo $vl | grep -oP "([\w.-]*\d)")
#echo $vl2

#rv=$($vl | grep -E -m 1 "^${requested_version//./\\.}([\\.\\s]|$)")
#echo $rv

#\s*\s/([0-9a-zA-Z]+\.)?([0-9a-zA-Z\\-]+\.)?([0-9a-zA-Z\\-]+)\s*\s

#sdk_install java "$JAVA_VER" "\s${JAVA_VER_MAJOR}*\s" "(\\.[a-z0-9]+)?${JAVA_VER_DIST}\\s*"
sdk_install maven "$MAVEN_VER" "\s3.*\s" ""
#sdk_install gradle "latest" "\s\s" "\s\s"

#sdk list maven | grep -oP "\s*\s\K[0-9]+\.[0-9]+\.[0-9]+\s*\s" | tr -d ' ' | sort -rV

#versions_to_delete=$(sdk list java | grep "local only" | cut -c 62-)
#java_versions_to_install=$(sdk list java | grep -v "local only" | grep "hs-adpt" | grep -v "sdk install" | grep -v "installed" | cut -c 62-)
#delete_for_each "$versions_to_delete"
#install_for_each "$java_versions_to_install"

#sdk current
#echo $JAVA_HOME
#java -version
#mvn -version

cd $LAUNCH_DIR