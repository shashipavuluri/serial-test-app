#!/bin/sh
cat /etc/hosts
DEST=/tmp/cores
trap process_stop SIGTERM 

process_stop() {
    date >> $CAF_APP_LOG_DIR/stdout.log 2>&1 
    echo 'System exit!' >> $CAF_APP_LOG_DIR/stdout.log 2>&1 
    exit 0
}

echo 'Check the serial port'
echo $HOST_DEV1 >> $CAF_APP_LOG_DIR/stdout.log

echo $CAF_APP_LOG_DIR >> $CAF_APP_LOG_DIR/stdout.log
cp $CAF_APP_PATH $DEST -r
cp $CAF_APP_CONFIG_FILE $DEST/app
date >> $CAF_APP_LOG_DIR/stdout.log 2>&1 
sleep 20  
while true; do
	echo 'Starting the application' >> $CAF_APP_LOG_DIR/stdout.log
	cd $DEST/app; java -Xmx100m -Djava.library.path=./libs -Djava.net.preferIPv4Stack=true -XX:GCTimeRatio=4 -Djava.util.logging.config.file=logging.properties -jar iotsp-batch-store-manager-0.3.0.5-SNAPSHOT-all.jar -s -p  >> $CAF_APP_LOG_DIR/stdout.log 2>&1;
	echo 'Finished running the application' >> $CAF_APP_LOG_DIR/stdout.log
    sleep 100
done
