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

### Register a new user
* Select option 5
* Provide user's first and last name
* Provide password and re-enter password for verification

### Login and get credentials for already registered users
* Select option 6
* Provide pre-registered username and password to get auth token

### Delete an order by order_id
* Select option 4
* To delete an order by ID, enter the order id

### Authentication and authorization
* Without authorization token, all rest api endpoints will return 401 response code
* To obtain authorization tokem, user need to be registered using option 5 and then subsequently need to login using option 6
* Authorization token is valid only in current session. Once OrdersUI client is exit using option X, token is also marked as invalid. On next execution of clientUI, login needs to be done again.

### Logging
* Logger method declared in REST.js and used in all api handlers
* Generated logs are appended in on-disk file ws.log
* Logger method can be passed “LEVEL” and message to be logged. This can help in distinguishing errors and info messages.


## Micro Services:
### Cleanup
* docker volume rm ms_db
* docker volume create ms_db

### Start the Micro-Service servers
* docker-compose -f stack-ms.yml build
* docker-compose -f stack-ms.yml up

### Start the MicroService Client
* docker-compose -f stack-ms.yml exec client java OrdersUI

### Register a new user
* Select option 5
* Provide user's first and last name
* Provide password and re-enter password for verification

### Login and get credentials for already registered users
* Select option 6
* Provide pre-registered username and password to get auth token

### Delete an order by order_id
* Select option 4
* To delete an order by ID, enter the order id

### Authentication and authorization
* A new Authorisation service handles register/login/logout methods
* Without authorization token, all rest api endpoints will return "Invalid token"
* To obtain authorization tokem, user need to be registered using option 5 and then subsequently need to login using option 6
* Authorization token is valid only in current session. Once OrdersUI client is exit using option X, token is also marked as invalid. On next execution of clientUI, login needs to be done again.

### Logging
* A new logger service provides a RMI log() method and  used in all existing services
* Generated logs are appended in on-disk file ms.log
* Logger method can be passed “LEVEL” and message to be logged. This can help in distinguishing errors and info messages.