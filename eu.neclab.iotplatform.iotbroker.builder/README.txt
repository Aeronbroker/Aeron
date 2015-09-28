/*******************************************************************************
 * Copyright (c) 2014, NEC Europe Ltd.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by NEC Europe Ltd.
 * 4. Neither the name of the NEC nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY NEC ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL NEC BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

-----IoT Broker Relase 3.2.1-----

-----System Requirements-----------

 1 GHz 32-bit (x86) or 64-bit (x64) processor
 1 GB of system memory
 20 GB hard drive with at least 15 GB of available space
 Java 7 installed
 Maven 3 installed for compiling
 Internet connection for compiling with Maven (only the first time)
 OSGi Enviroment (Equinox and Felix tested). An example OSGi Enviroment can be found in the IoT Broker binary release.
---------------------------------------


-----Installation Guide-----------

The IoT Broker software in implemented in Java with Spring and Maven support. Before compiling the source code, please install two additional libraries inside your maven local repository.
For installing these libraries, use the command prompt and navigate to the "lib" folder inside the IoT Broker source folder. Then use the following commands to install the libraries:

mvn install:install-file -DgroupId=org.apache.httpcomponents -DartifactId=httpclient -Dversion=4.2.0-osgi -Dfile=httpclient-4.2.0-osgi.jar -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -DgroupId=org.apache.httpcomponents -DartifactId=httpcore -Dversion=4.2.0-osgi -Dfile=httpclient-4.2.0-osgi.jar -Dpackaging=jar -DgeneratePom=true

After that, you should be able to compile the IoT Broker source code without any problems. For compiling the full IoT Broker, navigate to the builder project "eu.neclab.iotbroker.builder" and use the mvn command "mvn clean package"

This command will generate a target folder with all the dependencies and the IoT Broker Osgi bundles that are needed for running the IoT Broker using an OSGi framework (like Equinox or Felix).

----------------------------------
Contacts:
Tobias Jacobs: tobias.jacob@neclab.eu
Raihan Ul-islam: raihan.ul-islam@neclab.eu
Salvatore Longo: salvatore.longo@neclab.eu