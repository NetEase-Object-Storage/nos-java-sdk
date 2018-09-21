PWD=`pwd`
jar=nos-sdk-java-1.2.1.jar
CP="$jar"
baseDir=$(cd "$(dirname "$0")"; pwd)
for JAR in $baseDir/third-party/* ; do CP="$CP:$JAR" ; done
java -cp $baseDir/lib/$CP com.netease.cloud.services.nos.tools.noscmd $*
