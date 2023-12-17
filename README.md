## Info
We have a relational MySQL database with a schematic (country-city, language by country). And there is a frequent request from the city that is slowing down. The solution is to put all the data that is requested frequently into Redis (in memory storage type key-value). 
It is necessary to compare the speed of obtaining the same data from MySQL and Redis.

## Technologies
The project used technologies:
* MySQL
* Hibernate 
* Docker
* Workbench
* Maven


## Results
![image](https://github.com/ViperAnna/hibernate_FinalProject/assets/122033767/54d809d8-accc-41a9-9993-b8c1c4bae32b)
