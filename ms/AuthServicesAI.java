/******************************************************************************************************************
* File: AuthServicesAI.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class provides the abstract interface for the retrieve micro service, RetrieveServices.
* The implementation of these abstract interfaces can be found in the RetrieveServices.java class.
* The micro services are partitioned as Create, Retrieve, Update, Delete (CRUD) service packages. Each service 
* is its own process (eg. executing in a separate JVM). It would be a good practice to follow this convention
* when adding and modifying services. Note that services can be duplicated and differentiated by IP
* and/or port# they are hosted on. For this assignment, create and retrieve services have been provided and are
* services are hosted on the local host, on the default RMI port (1099). 
*
* Parameters: None
*
* Internal Methods:
*  String register(String username, String password) - Registers an user with the system
*  String login(String username, String password ) - Generates an authentication token for the user
*  String logout() - Logs out the user from the system
*  boolean isTokenValid(String token) - Checks if the token is valid

* External Dependencies: None
******************************************************************************************************************/
import java.rmi.*;
		
public interface AuthServicesAI extends java.rmi.Remote
{
	/*******************************************************
	* Registers a new user with the system. 
	* The user name and password are taken as input
	*******************************************************/
	String register(String username, String password) throws RemoteException;

	/*******************************************************
	* Generates an authentication token for the user.
	* The user name and password are taken as input
	*******************************************************/	
	String login(String username, String password ) throws RemoteException;

	/*******************************************************
	 * Logs out the user from the system
	*******************************************************/
	String logout(String token) throws RemoteException;
	
	/*******************************************************
	 * Checks if the token is valid 
	*******************************************************/
	boolean isTokenValid(String token) throws RemoteException;
}