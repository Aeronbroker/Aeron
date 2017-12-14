IoT Broker System Monitoring
---

Log Files
----

The IoT Broker is using the [pax logging system](https://ops4j1.jira.com/wiki/display/paxlogging/Pax+Logging).
The logs are stored in the *reportLog* folder, a subdirectory of *IoTBroker-runner*.

The logger is configured in  *fiwareRelease/iotbrokerconfig/bundleConfigurations/services* folder. An example of the *org.ops4j.pax.logging* file is shown below.

```
 log4j.rootLogger=INFO, ReportFileAppender, console
 #Console Appender 
 log4j.appender.console=org.apache.log4j.ConsoleAppender
 log4j.appender.console.layout=org.apache.log4j.PatternLayout
 log4j.appender.console.layout.ConversionPattern=%d{ISO8601} | %-5.5p | (%F:%M:%L) | %m%n
 #Solve the digerest Tomcat logger errors
 log4j.logger.org.apache.commons=WARN
 log4j.logger.org.apache.commons.beanutils=WARN
 log4j.logger.org.apache.struts=WARN
 #File Appender
 # ReportFileAppender - used to log messages in the report.log file.
 log4j.appender.ReportFileAppender=org.apache.log4j.FileAppender
 log4j.appender.ReportFileAppender.File=.//reportLog//report.log
 log4j.appender.ReportFileAppender.layout=org.apache.log4j.PatternLayout
 log4j.appender.ReportFileAppender.layout.ConversionPattern= %-4r [%t] %-5p %c %x - %m%n
```

In the example above, the log level is selected in the first line. The possible log levels are:

* log4j.rootLogger=INFO
* log4j.rootLogger=DEBUG

Monitoring Panel
----

The IoT Broker has a monitoring panel that is accessible via the "admin login" button on the index page web page. For login, the administrator should use the ADMIN credentials. These credentials are set in the configuration file *fiwareRelease/iotbrokerconfig/iotbroker/config/users.properties*. The default username and password are (ADMIN, admin). 

The Monitoring panel is accessed via an HTTPS connection based on an SSL certificate. In the *fiwareRelease/iotbrokerconfig/iotbroker/https* folder there is already a dummy key, but for security reason the admin should generate a private key containing a valid certificate. A simple way to generate one of these is to use Java's keytool utility located in the *$JAVA_HOME/bin directory*.

Example:
```
'keytool -genkey -alias admin -keyalg RSA -keystore ...\fiwareRelease\iotBroker\https\key.keystore'
