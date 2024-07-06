/******************************************************************************************************************
* File: DeleteServicesAI.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class provides the abstract interface for the create micro services, CreateServices.
* The implementation of these abstract interfaces can be found in the CreateServices.java class.
* The micro services are partitioned as Create, Retrieve, Update, Delete (CRUD) service packages. Each service 
* is its own process (eg. executing in a separate JVM). It would be a good practice to follow this convention
* when adding and modifying services. Note that services can be duplicated and differentiated by IP
* and/or port# they are hosted on. For this assignment, create and retrieve services have been provided and are
* services are hosted on the local host, on the default RMI port (1099). 
*
* Parameters: None
*
* Internal Methods:
*  String deleteOrder(String token, String id) - deletes an existing order in the orderinfo database
*
* External Dependencies: None
******************************************************************************************************************/

import java.rmi.*;
		
public interface DeleteServicesAI extends java.rmi.Remote
{
	/*******************************************************
	* Deletes an existing order from the table.
	* Returns an OK message or an error string.
	*******************************************************/
	String deleteOrder(String token, String orderID) throws RemoteException;
}