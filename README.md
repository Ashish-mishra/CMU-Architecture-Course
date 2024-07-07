This project contains the source code for the architecturally-compliant coding and trade-off assignment.

The assignment and installation instructions are given in "A3-2024" and "A3 Installation and Setup" pdf files respectively.

# Steps to run the assignments for WebService and MicroService systems on local machine:

## Web Services:
### Cleanup
* docker volume rm ws_db
* docker volumen create ws_db

### Start the WebService server
* docker-compose -f stack-ws.yml build
* docker-compose -f stack-ws.yml up

### Start the WebService Client
* cd ws
* javac *.java
* java OrdersUI 

## Micro Services:
### Cleanup
* docker volume rm ms_db
* docker volumen create ms_db

### Start the Micro-Service servers
* docker-compose -f stack-ms.yml build
* docker-compose -f stack-ms.yml up

### Start the MicroService Client
* docker-compose -f stack-ms.yml exec client java OrdersUI
