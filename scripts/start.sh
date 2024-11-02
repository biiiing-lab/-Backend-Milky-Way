#!/usr/bin/env bash

PROJECT_ROOT="/home/ubuntu"
JAR_FILE="$PROJECT_ROOT/Milky_Way_Back-0.0.1-SNAPSHOT.jar "

APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

# 이전 프로세스 종료
CURRENT_PID=$(pgrep -f $JAR_FILE)
if [ -n "$CURRENT_PID" ]; then
    echo "$TIME_NOW > 이전 프로세스 종료: $CURRENT_PID" >> $DEPLOY_LOG
    kill -9 $CURRENT_PID
fi

# jar 파일 실행
echo "$TIME_NOW > $JAR_FILE 파일 실행" >> $DEPLOY_LOG
nohup java -jar $JAR_FILE > $APP_LOG 2> $ERROR_LOG &

CURRENT_PID=$(pgrep -f $JAR_FILE)
echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다." >> $DEPLOY_LOG