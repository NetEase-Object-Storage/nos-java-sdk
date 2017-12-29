CP="."
for JAR in lib/*jar ; do CP="$CP:$JAR" ; done
nohup java -cp $CP com.netease.cloud.services.nos.tools.SyncNOS > sync.log 2>&1 &
echo $! > sync.pid
