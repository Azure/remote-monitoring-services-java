Starting Microservices on local environemnt
=====
### New & Existing Users
The new repository contains a **start** script and few other scripts to bootstrap the new users with the required cloud resources. These scripts are used to create azure resources like Cosmos DB, IoTHub, Azure Stream Analytics etc. The start script is located in *scripts / local / launch* folder under root directory of the repository. If you have cloned azure-iot-pcs-remote-monitoring-java repository, the scripts folder is present under services submodule (folder).

**Please Note:**
*These scripts are executable in **bash shell only**. On windows these scripts can be run manually using* *Git Bash shell or by using Windows Sub system for Linux. The instructions to enable WSL are available* *[here](https://docs.microsoft.com/en-us/windows/wsl/install-win10).*

#### Start script
This script checks if required environment variables are set on the local system. If the variables are set then one can open the IDE to start the microservices. If the variables are not set then this script will guide through the process of creating the new variables. It will then create different scripts under *scripts / local / launch / os / OS_TYPE /* which can be used to set environment variables on the machine.

For users who have already created the required azure resources, please set the envvironment variables on your machine so as to be accessible by the IDE. Alternatively, these variables can be set in the Edit configurations wizard of the IDE. Although not recommended, environment variables can also be set in application.conf file present under webservice package for each of the microservices.

**Please Note:**
*This script requires **Node.js** to execute, please install Node (version < 8.11.2) before using this script. Also, this script might require administartive privileges or sudo permission as it tries to install node packages, if they are not already installed. At times, the script might fail while installing npm packages. In such cases, please install npm package **iot-solutions** using following command using administartive privileges or sudo access.*

*npm install -g iot-solutions*
&nbsp; 

##### Usage:   
````
abc@pcs sh start.sh   
````
![start](https://user-images.githubusercontent.com/39531904/44435771-6ab08280-a566-11e8-93c9-e6f35e5df247.PNG)
 
#### Helper scripts
These scripts are located under helpers folder which is under the launch folder. The script create_azure_resources.sh can be independently called to create resources in the cloud. The script check_dependencies.sh checks if environment variables are set for a particular microservices.
##### Usage:
1) check environment variables for a microservice 
sh check_dependencies.sh <microservice_folder_name> 
2) create Azure resources 
sh create_azure_resources.sh
 
After creating the required azure resources, using start or create-azure-resources.sh, one should execute the following scripts present under *os/{linux / win / osx}* to set the environment variables. 
1) set-env-uri
2) set-env

**Please Note:**
*If you are using windows, you will have to execute these scripts in CMD shell. On OSX, these scripts are automatically run by the start script. For linux, the environment variables present in these scripts need to be set at global location, depending upon the flavour of linux you are using.* 

#### Recap of steps to create resources and set environment variables
1) Run start.sh
2) Run scripts under os folder. 

#### Walk through for importing new Solution in IDE
##### Intellji
This is our preferred editor for Java development.

The java repository has a similar structure to the dotnet repo and contains the same script(s). The scripts, described above, are located under scripts/local/launch folder.  

##### Steps to import launch settings
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

*SBT tool has been observed to fork number of processes equivalent to the number of CPUs. Hence, on dual core machines all the microservices may not be started parallelly.*
 
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
