#!/bin/bash

# set -x

LAUNCH_DIR=$(pwd); SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"; cd $SCRIPT_DIR; cd ..; SCRIPT_PARENT_DIR=$(pwd);

cd $SCRIPT_DIR

# Install java/maven through sdkman
# ./install.sh 11.0.11.hs-adpt 3.8.2 no no

JAVA_VER=${1:-11.0.11.hs-adpt}
MAVEN_VER=${2:-3.8.1}
SET_JAVA_VER_DEFAULT=${3:-no}
SET_MAVEN_VER_DEFAULT=${4:-no}

export SDKMAN_DIR="$HOME/.sdkman"

if [ -z ${SDKMAN_DIR+x} ] || [[ ! -d "$SDKMAN_DIR" ]] ; then
    iecho "sdkman not detected, installing it"
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

sdk current java | grep "$JAVA_VER" || echo $SET_JAVA_VER_DEFAULT | sdk install java $JAVA_VER
sdk current maven | grep "$MAVEN_VER" || echo $SET_MAVEN_VER_DEFAULT | sdk install maven $MAVEN_VER

if [ "$SET_JAVA_VER_DEFAULT" == "yes" ]; then
  sdk default java $JAVA_VER
fi

if [ "$SET_MAVEN_VER_DEFAULT" == "yes" ]; then
  sdk default maven $MAVEN_VER
fi

sdk current
echo $JAVA_HOME
java -version
mvn -version

cd $LAUNCH_DIR