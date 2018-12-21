# How to

In order to perform the ETL, we used the following: 
- Groovy Programming language
- Spring Boot framework
- Hibernate ORM
- MySQL

The project assumes that a database with the name “tpc-di” has been created in MySQL. 

### Generate the source files (Batch 1)
In order to generate the source files, do the following steps in order :
- Go to the TPCDI website and download the TPC-DI toolkit
- Unzip the file
- Enter into “Tools” directory
- Execute java -jar DIGen.jar -sf 5

Download or clone this repository using git. 

#### Execute
Open IntelliJ, go to File-> Open, look for the location of the code and select the root folder of the downloaded repository. IntelliJ is going to take some minutes to setup all the environment. 

Once the project has been imported into Intellij, open the file “application.properties” in directory and change the file.location to the location of the data generated (Batch1).

Execute the project, the data will be loaded into the MySQl database. 

### Audit queries
You will have to add to the database the Audit and DiMessages tables to execute the audit queries and check the results. Please follow the TPC-DI instructions for this step.
