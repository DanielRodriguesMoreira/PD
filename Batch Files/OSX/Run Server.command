#!/bin/bash
# init
function pause(){
   read -p "$*"
}

#java -jar ..\Server\dist\Server.jar daniel 225.10.10.10 6000
java -jar "/Users/hugosantos/Desktop/PD/Server/dist/Server.jar" Server01 localhost 6000 /Users/hugosantos/Desktop/PD/Server/login.txt /Users/hugosantos/Desktop/PD/Server

# call it
pause 'Press [Enter] key to continue...'