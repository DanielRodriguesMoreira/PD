#!/bin/bash
# init
function pause(){
   read -p "$*"
}

#java -jar ..\Server\dist\Server.jar daniel 225.10.10.10 6000
java -jar "/Users/hugosantos/Desktop/PD/Server/dist/Server.jar" Daniel 255.10.10.10 6000

# call it
pause 'Press [Enter] key to continue...'