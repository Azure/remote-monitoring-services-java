Starting Microservices on local environment
=====
### Steps to create Azure resources
#### New Users
1) Run the [start.cmd or start.sh](https://github.com/Azure/remote-monitoring-services-java/blob/master/scripts/local/launch/) script (depending on your OS) located under launch *(scripts/local/launch)* folder.  **OR**
2) Clone [pcs-cli](https://github.com/azure/pcs-cli/) (a cli for RM), build it and run "*pcs -s local*" command to create cloud resource and set the required environment variables locally.
![start_new_java](https://user-images.githubusercontent.com/39531904/46707719-0918a600-cbf0-11e8-96d4-b5d89d957962.PNG)

> **Please Note:**
> 1. *If you have cloned azure-iot-pcs-remote-monitoring-java repository, the scripts folder is present under services submodule (folder).*
> 2. *The start script requires **Node.js** to execute,please install latest stable Node 8 (donot use Node 10) before using this script. Also, this script might require administartive privileges or sudo permission as it tries to install [pcs-cli](https://github.com/Azure/pcs-cli) a cli interface for remote-monitoring deployments.*
&nbsp;
> 3. *After creating the required azure resources through the script, please stop all the instances of your IDE and restart them. (Otherwise, the environment variables will not be picked up by microservices luanched from IDE)*
> 4. *For OS X the above script(s) may not be able to set the environment variables persistently. While provisioning, the script(s) also download a file containing environment variables. Please use this file to set the environemnt variables.*

#### Existing Users
For users who have already created the required azure resources, please do one of the following: 
1) Set the environment variables globally on your machine.
2) **Intellji:** Set the environment variables in the "Edit configurations" vizard of the IDE. (The steps for creating the configurations are given below.)

**Please Note:**
1) *Although not recommended, environment variables can also be set in appsettings.ini file present under WebService folder for each of the microservices.*
2) *Build tasks depend upon env variables. If env variables are being set in IDE configurations OR in appsettings file, the tasks may not work. You will have to build services separately by settings env variables.*

### Walk through for importing new Solution in IDE
##### Intellji

This is our preferred editor for Java development.

##### Steps to import project and create build/run configurations
1) Install SBT, SBT Executor & Scala plugins for Intellji
![intellji](https://user-images.githubusercontent.com/39531904/44321184-58fe9c00-a3fb-11e8-8d3e-4ff208139bac.png)
    * SBT&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.8.0 
    * SBT Executor&nbsp;&nbsp;&nbsp;1.2.1 
    * Scala&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2018.1.9
2)	Import the project using Intellji import project wizard.
Use “Import project from external model” and select "sbt".
![intellji](https://user-images.githubusercontent.com/39531904/44321254-ca3e4f00-a3fb-11e8-88c7-c442dd3a202e.png)
3) Select “sbt sources” and “Use sbt shell options”
Ensure your java version is 1.8 or less.
![intellji](https://user-images.githubusercontent.com/39531904/44321300-083b7300-a3fc-11e8-9e5c-e33ab4859179.png)
4) After import process has finished, the IDE starts “sbt dump” process. Please wait for this process to complete.
5) After the sbt dump process, you can create build OR run configuration, for running all the microservices. Please use Intellji “Edit Configuration” wizard. Select “sbt task” and then you can use compile OR run tasks.
![Intellji](https://user-images.githubusercontent.com/39531904/44321516-52712400-a3fd-11e8-9828-9daf43a3e43b.png)\
Environment variables can also be set in the Edit Configuration wizard under environment variables Tab.

The build.sbt file has been configured to run all the microservices parallelly. It will also start the microservices available only in .Net flavor (device-simulation, auth and ASA manager).

*SBT tool has been observed to fork number of processes equivalent to the number of CPUs. Hence, on dual core machines all the microservices may not be started in parallel.*

### Script Description
#### Start Script
The new repository contains a **start** script and few other scripts to bootstrap the new users with the required cloud resources. These scripts are used to create azure resources like Cosmos DB, IoTHub, Azure Stream Analytics etc. The start script is located in *scripts / local / launch* folder under root directory of the repository.

Structure of the microservices
=====
Each microservice comprises of following packages/folders. 
1) scripts 
2) webService  
3) service  
4) webservice under test  
5) service under Test

Description: 
1) Scripts  
The scripts folder is organized as follows\
i. **docker** sub folder for building docker containers of the current microservice.\
ii. **root** folder contains scripts for building and running services natively.\
&nbsp; 
![script folder structure](https://user-images.githubusercontent.com/39531904/44290937-10df4e00-a230-11e8-9cd4-a9c0644e166b.PNG "Caption")\
The docker build scripts require environment variables to be set up before execution. The run scripts can run both natively built and dockerized microservice. The run script under docker folder can also be independently used to pull and run published docker images. One can modify the tag and the account to pull different version or privately built docker images.
&nbsp; 

2) webservice  
It contains code for REST endpoints of the microservice.
&nbsp;  

3) service  
It contains business logic and code interfacing various SDKs. 
&nbsp;

4) webservice (test)  
It contains unit tests for the REST endpoints of the microservice. 
&nbsp; 

5) Service (test)
It contains unit tests for the business logic and code interfacing various SDKs.
&nbsp;  

6) Other Packages  
The microservice might contain other projects such as RecurringTaskAgent etc.
