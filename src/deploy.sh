#!/bin/bash
export SUDO_ASKPASS=/usr/lib/ssh/x11-ssh-askpass

# sudo -A rm -rf /var/lib/tomcat10/webapps/app.war
# sudo -A cp ./build/libs/app.war /var/lib/tomcat10/webapps/app.war
scp ./build/libs/app.war deploy@154.59.225.141:/opt/tomcat/webapps/currency-exchange.war
