TIME=$(date +"%m-%d-%y")
CLIENT_DIR="CalicoClient-nick"
SERVER_DIR="CalicoServer-nick"
WEBSTART_FILE="calicoclient-webstart.jnlp"
SERVER_CONF_FILE="server.properties"

SERVER_IP_ADDRESS="nautilus.ics.uci.edu"
SERVER_NAME="II Test"
SERVER_PORT="27060"
SERVER_CLIENT_PORT="27061"

SERVER_IP_ADDRESS_STUB="##SERVER_IP_ADDRESS##"
SERVER_PORT_STUB="##SERVER_PORT##"
SERVER_NAME_STUB="##SERVER_NAME##"
SERVER_CLIENT_PORT_STUB="##SERVER_CLIENT_PORT##"

if [ ! -d "../../$DIRECTORY/$CLIENT_DIR/trunk/calico3client-bugfixes" ]; then
    echo "Aborting... Calico client directory not found"
	exit 2
fi
#compile server
cd "../calico3server"
ant clean
ant dist
#copy distributables
mkdir "dist/calico3server-trunk/uploads"
mkdir "dist/calico3server-trunk/uploads/webstart"
mkdir "dist/calico3server-trunk/uploads/webstart/client"
cd "../deploymentScripts/files/webstart"
cp *.* "../../../calico3server/dist/calico3server-trunk/uploads/webstart/client"
cd "../server"
cp *.* "../../../calico3server/dist/calico3server-trunk"
cd "../serverWebPage"
cp -f *.* "../../../calico3server/dist/calico3server-trunk/admintpl"
cd "../serverConf"
cp -f *.* "../../../calico3server/dist/calico3server-trunk"
#compile and copy client
cd "../../../../$CLIENT_DIR/trunk/calico3client-bugfixes"
ant clean
ant webstart
cd "webstart/signed"
cp -r * "../../../../../$SERVER_DIR/calico3server/dist/calico3server-trunk/uploads/webstart/client"

#replace stubs in jnlp file
cd "../../../../../$SERVER_DIR/calico3server/dist/calico3server-trunk/uploads/webstart/client"
sed -i "s/$SERVER_IP_ADDRESS_STUB/$SERVER_IP_ADDRESS/" $WEBSTART_FILE
sed -i "s/$SERVER_NAME_STUB/$SERVER_NAME/" $WEBSTART_FILE
sed -i "s/$SERVER_PORT_STUB/$SERVER_PORT/" $WEBSTART_FILE
sed -i "s/$SERVER_CLIENT_PORT_STUB/$SERVER_CLIENT_PORT/" $WEBSTART_FILE
#replace stubs in conf file
cd "../../../"
sed -i "s/$SERVER_PORT_STUB/$SERVER_PORT/" $SERVER_CONF_FILE
sed -i "s/$SERVER_CLIENT_PORT_STUB/$SERVER_CLIENT_PORT/" $SERVER_CONF_FILE

#copy it to a distributable folder
mkdir "../../../deploymentScripts/CalicoServer_$TIME"
cp -r * "../../../deploymentScripts/CalicoServer_$TIME"



