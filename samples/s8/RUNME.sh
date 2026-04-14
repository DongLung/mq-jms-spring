# This script compiles and runs the sample program in this directory.
#
# You may need to modify the application.properties file to get it to
# connect to your queue manager.

###### Cleanup from previous runs
# Kill any old instances of the application
ps -ef|grep gradle | grep sample8.Application | awk '{print $2}' | xargs kill -9 >/dev/null 2>&1

# and try to clear the queue (assuming it's a local queue manager)
echo "CLEAR QLOCAL(DEV.QUEUE.1)" | runmqsc -e QM1 >/dev/null 2>&1
######

# Now run the program. Build using the gradle wrapper in parent directory
cd ../..

. setmqenv -m QM1 -k
export PATH=$MQ_INSTALLATION_PATH/samp/bin:$PATH

./gradlew --configuration-cache -p samples/s8 bootRun 2>&1


