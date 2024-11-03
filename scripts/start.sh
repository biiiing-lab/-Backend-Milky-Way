#!/usr/bin/env bash

PROJECT_ROOT="/home/ubuntu/app"
JAR_FILE="$PROJECT_ROOT/build/libs/Milky_Way_Back-0.0.1-SNAPSHOT.jar"

APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

if [ -f "$JAR_FILE" ]; then
    echo "$TIME_NOW > Starting $JAR_FILE" >> $DEPLOY_LOG
    nohup java -jar $JAR_FILE > $APP_LOG 2> $ERROR_LOG &
    CURRENT_PID=$!
    echo "$TIME_NOW > Started process with PID $CURRENT_PID" >> $DEPLOY_LOG
else
    echo "$TIME_NOW > Error: $JAR_FILE not found" >> $DEPLOY_LOG
fi
