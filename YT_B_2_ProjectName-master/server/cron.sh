#!/bin/bash
# * * * * * /opt/server/cron.sh >/dev/null 2>&1
# running as user gitlab-deploy
DIR=/opt/server; cd $DIR

if [[ -f new_version ]]; then
    rm new_version

    # stop server if running
    if screen -list | grep -q "cyswapper"; then
        screen -p 0 -S cyswapper -X stuff '^C'
		sleep 1
        while screen -list | grep -q "cyswapper"; do
            echo "Waiting for server to stop..."
            sleep 1
        done
    fi

    # use new server binary
    mv -f server-linux-amd64 server
    #chmod a+x server

    # start new server
    screen -dmS cyswapper bash -c '/opt/server/server >> /opt/server/server.log 2>&1'
fi
