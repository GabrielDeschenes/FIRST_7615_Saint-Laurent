#link to correct JDK directory
#!/bin/bash

[[ "$(whoami)" == "Patbot" ]] && javaHome="C:\Users\Public\frc2019\jdk" || javaHome="/home/jon/frc2019/jdk"

cd src && ./gradlew build generateVsCodeConfig -Dorg.gradle.java.home="$javaHome" &> buildlog.txt
if [[ "$(tail -n 2 buildlog.txt)" == *"SUCCESS"* ]]
then
	echo -e "\c"
else
	while read line;do
		if [[ "$line" == *"FAILURE"* ]]
		then 
			break;
		fi
		[[ "$line" != *"Task"* ]] && echo $line >> newLog.txt
	done < buildlog.txt
	tput setaf 6
	cat newLog.txt
fi
#[ -f buildlog.txt ] && rm buildlog.txt
[ -f newLog.txt ]   && rm newLog.txt
