#!/usr/bin/env bash

PROJECT_ROOT="/home/ubuntu/app"
JAR_FILE="$PROJECT_ROOT/build/libs/Milky_Way_Back-0.0.1-SNAPSHOT-plain.jar"

PID=$(pgrep -f $JAR_FILE)

if [ -n "$PID" ]; then
    echo "Stopping process $PID..."
    kill $PID
else
    echo "No process found."
fi
