#!/bin/bash

# set -x

LAUNCH_DIR=$(pwd); SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"; cd $SCRIPT_DIR; cd ..; SCRIPT_PARENT_DIR=$(pwd);

cd $SCRIPT_DIR

# Install java/maven with sdkman
# ./install.sh 11.0.1.hs-adpt 3.8.1 no noq
# 11.0.11.hs-adpt,11.0.10.hs-adpt,11.0.9.hs-adpt

JAVA_VER=${1:-11.0.11.hs-adpt}
MAVEN_VER=${2:-3.8.2}
SET_JAVA_VER_DEFAULT=${3:-no}
SET_MAVEN_VER_DEFAULT=${4:-no}

export SDKMAN_DIR="$HOME/.sdkman"

if [ -z ${SDKMAN_DIR+x} ] || [[ ! -d "$SDKMAN_DIR" ]] ; then
    echo "sdkman not detected, installing it"
    curl -s "https://get.sdkman.io?rcupdate=false" | bash
else
  echo "Sdkman! already installed."
fi

if [ ! -z "$JAVA_VER" ]; then
  echo "Installing Java: $JAVA_VER"
else
    echo "Specify Java version"
    exit 1
fi

if [ ! -z "$MAVEN_VER" ]; then
   echo "Installing Maven: $MAVEN_VER"
else
    echo "Specify Maven version"
    exit 1
fi

# Bring 'sdk' function into scope
source "$SDKMAN_DIR/bin/sdkman-init.sh"

# install $JAVA_VER if not installed
if [ "$(sdk list java | grep -v "local only" | grep "$JAVA_VER" | grep -v "sdk install" | grep -v "installed" | wc -l)" == "1" ]; then
  echo $SET_JAVA_VER_DEFAULT | sdk install java $JAVA_VER
fi

# if not already set, use java $JAVA_VER in this shell
if [ "$(sdk current java | grep -c "$JAVA_VER")" != "1" ]; then
  sdk use java $JAVA_VER
fi

#  if needed, set $JAVA_VER as default
if [ "$SET_JAVA_VER_DEFAULT" == "yes" ]; then
  sdk default java $JAVA_VER
fi

# install $MAVEN_VER if not installed
if [ "$(sdk list maven | grep -v "local only" | grep "$MAVEN_VER" | grep -v "*" | wc -l)" == "1" ]; then
  echo $SET_MAVEN_VER_DEFAULT | sdk install maven $MAVEN_VER
fi

# if not already set, use maven $MAVEN_VER in this shell
if [ "$(sdk current maven | grep -c "$MAVEN_VER")" != "1" ]; then
  sdk use maven $MAVEN_VER
fi

# if needed, set $MAVEN_VER as default
if [ "$SET_MAVEN_VER_DEFAULT" == "yes" ]; then
  sdk default maven $MAVEN_VER
fi

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

#versions_to_delete=$(sdk list java | grep "local only" | cut -c 62-)
#java_versions_to_install=$(sdk list java | grep -v "local only" | grep "hs-adpt" | grep -v "sdk install" | grep -v "installed" | cut -c 62-)
#delete_for_each "$versions_to_delete"
#install_for_each "$java_versions_to_install"

sdk current
echo $JAVA_HOME
java -version
mvn -version

cd $LAUNCH_DIR