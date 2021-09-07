
# Install java/maven through sdkman

JAVA_VER=11.0.11.hs-adpt
MAVEN_VER=3.8.2

export SDKMAN_DIR="$HOME/.sdkman"

if [ -z ${SDKMAN_DIR+x} ] || [[ ! -d "$SDKMAN_DIR" ]] ; then
    iecho "sdkman not detected, installing it"
    curl -s "https://get.sdkman.io?rcupdate=false" | bash
fi

# Bring 'sdk' function into scope
source "$SDKMAN_DIR/bin/sdkman-init.sh"

echo N | sdk install java $JAVA_VER
echo N | sdk install maven $MAVEN_VER

sdk current java | grep "$JAVA_VER" || sdk install java $JAVA_VER
sdk current maven | grep "$MAVEN_VER" || sdk install java $MAVEN_VER

echo $JAVA_HOME
java -version
mvn -version
