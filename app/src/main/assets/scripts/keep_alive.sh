#!/system/bin/sh

while true
do
    sleep 60
    if ! pgrep -f "cn.nightrainmilkyway.tritium" > /dev/null
    then
        am start -n cn.nightrainmilkyway.tritium/.MainActivity
    fi
done
