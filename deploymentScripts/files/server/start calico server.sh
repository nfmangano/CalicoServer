#!/bin/bash
echo Starting Calico Server...
TIME=$(date +"%m-%d-%y_%H-%M")
java -jar -Xms1200m calico3server.jar > "server_$TIME.out" "2>&1"
#sleep 5
#echo Restoring last session data...
#curl --form "Filedata=@backup_auto.csb" --form submit=Submit http://localhost:27041/gui/backup/