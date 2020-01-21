#!/bin/bash

[[ "$(whoami)" == "Patbot" ]] && javaHome="C:\Users\Public\frc2019\jdk" || javaHome="/home/jon/frc2019/jdk"

cd src && ./gradlew deploy  -PteamNumber=7615 --offline  -Dorg.gradle.java.home="$javaHome" --console plain --warning-mode none
