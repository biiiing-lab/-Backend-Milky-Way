#!/bin/bash

cd /home/ubuntu/app  # 애플리케이션 디렉토리로 이동
./gradlew clean build -x test # 테스트 제외하고 빌드

# 빌드가 성공했는지 확인
if [ -f "build/libs/Milky_Way_Back-0.0.1-SNAPSHOT.jar" ]; then
    echo "Build successful, JAR file created."
else
    echo "Build failed, JAR file not found." >&2
    exit 1  # 빌드 실패 시 스크립트 종료
fi