#!/bin/bash
# insert privileges 
# chmod 755 scriptname.command
# init
function pause(){
   read -p "$*"
}

#java -jar /Users/hugosantos/Desktop/PD/Client/dist/Client.jar localhost 6000
java -jar /Users/hugosantos/Desktop/PD/Client/dist/Client.jar 10.65.134.215 6000

# call it
pause 'Press [Enter] key to continue...'