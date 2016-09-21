FROM fiware/iotbroker

RUN apt-get update
RUN wget --no-check-certiﬁcate https://raw.githubusercontent.com/fgalan/ oauth2-example-orion-client/master/token_script.sh

RUN curl -s -d "{\"username\": \”username@yourdomain.com \”, \"password\":\"yourpassword\"}" -H "Content-type: application/json" https://orion.lab.fiware.org/token
