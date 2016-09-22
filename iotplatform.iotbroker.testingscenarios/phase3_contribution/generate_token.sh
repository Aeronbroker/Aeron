 #!/bin/bash
green="`tput setaf 2`"
reset="tput sgr0"
echo "${green}Please, insert your Fiware Username"
${reset}
read username
echo "${green}Please, insert your Fiware Password"
${reset}
read password
request=`curl -s -d "{\"username\": \"$username\", \"password\":\"$password\"}" -H "Content-type: application/json" https://orion.lab.fiware.org/token`
echo "${green}This is your token:"
${reset}
echo $request;