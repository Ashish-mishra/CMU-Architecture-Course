/******************************************************************************************************************
* File:OrdersUI.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class is the console for the an orders database. This interface uses a webservices or microservice
* client class to update the orderinfo MySQL database. 
*
* Parameters: None
*
* Internal Methods: None
*
* External Dependencies (one of the following):
*	- RESTClientAPI - this class provides a restful interface to a node.js webserver (see Server.js and REST.js).
*	- ms_client - this class provides access to micro services vis-a-vis remote method invocation
*
******************************************************************************************************************/

import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.Console;

public class OrdersUI
{
	public static void main(String args[])
	{
		boolean done = false;						// main loop flag
		boolean error = false;						// error flag
		char    option;								// Menu choice from user
		Console c = System.console();				// Press any key
		String  date = null;						// order date
		String  first = null;						// customer first name
		String  last = null;						// customer last name
		String  address = null;						// customer address
		String  phone = null;						// customer phone number
		String  orderid = null;						// order ID
		String 	response = null;					// response string from REST 
		Scanner keyboard = new Scanner(System.in);	// keyboard scanner object for user input
		DateTimeFormatter dtf = null;				// Date object formatter
		LocalDate localDate = null;					// Date object
		WSClientAPI api = new WSClientAPI();	// RESTful api object
		//String authToken = null; 					// JWT token for user authentication

		/////////////////////////////////////////////////////////////////////////////////
		// Main UI loop
		/////////////////////////////////////////////////////////////////////////////////

		while (!done)
		{	
			// Here, is the main menu set of choices

			System.out.println( "\n\n\n\n" );
			System.out.println( "Orders Database User Interface: \n" );
			System.out.println( "Select an Option: \n" );
			System.out.println( "1: Retrieve all orders in the order database." );
			System.out.println( "2: Retrieve an order by ID." );
			System.out.println( "3: Add a new order to the order database." );				
			System.out.println( "4: Delete an order by ID." );				
			System.out.println( "5: Register a new user." );				
			System.out.println( "6: Login and get credentials" );				
			System.out.println( "X: Logout and Exit\n" );
			System.out.print( "\n>>>> " );
			option = keyboard.next().charAt(0);	
			keyboard.nextLine();	// Removes data from keyboard buffer. If you don't clear the buffer, you blow 
									// through the next call to nextLine()

			//////////// option 1 ////////////

			if ( option == '1' )
			{
				// Here we retrieve all the orders in the order database

				System.out.println( "\nRetrieving All Orders::" );
				try
				{
					response = api.retrieveOrders();
					System.out.println(response);

				} catch (Exception e) {

					System.out.println("Request failed:: " + e);

				}

				System.out.println("\nPress enter to continue..." );
				c.readLine();

			} // if

			//////////// option 2 ////////////

			if ( option == '2' )
			{
				// Here we get the order ID from the user

				error = true;

				while (error)
				{
					System.out.print( "\nEnter the order ID: " );
					orderid = keyboard.nextLine();

					try
					{
						Integer.parseInt(orderid);
						error = false;
					} catch (NumberFormatException e) {

						System.out.println( "Not a number, please try again..." );
						System.out.println("\nPress enter to continue..." );

					} // if

				} // while

				try
				{
					response = api.retrieveOrders(orderid);
					System.out.println(response);

				} catch (Exception e) {

					System.out.println("Request failed:: " + e);
					
				}

				System.out.println("\nPress enter to continue..." );
				c.readLine();

			} // if

			//////////// option 3 ////////////

			if ( option == '3' )
			{
				// Here we create a new order entry in the database

				dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				localDate = LocalDate.now();
				date = localDate.format(dtf);

				System.out.println("Enter first name:");
				first = keyboard.nextLine();

				System.out.println("Enter last name:");
				last = keyboard.nextLine();
		
				System.out.println("Enter address:");
				address = keyboard.nextLine();

				System.out.println("Enter phone:");
				phone = keyboard.nextLine();

				System.out.println("Creating the following order:");
				System.out.println("==============================");
				System.out.println(" Date:" + date);		
				System.out.println(" First name:" + first);
				System.out.println(" Last name:" + last);
				System.out.println(" Address:" + address);
				System.out.println(" Phone:" + phone);
				System.out.println("==============================");					
				System.out.println("\nPress 'y' to create this order:");

				option = keyboard.next().charAt(0);

				if (( option == 'y') || (option == 'Y'))
				{
					try
					{
						System.out.println("\nCreating order...");
						response = api.newOrder(date, first, last, address, phone);
						System.out.println(response);

					} catch(Exception e) {

						System.out.println("Request failed:: " + e);

					}

				} else {

					System.out.println("\nOrder not created...");
				}

				System.out.println("\nPress enter to continue..." );
				c.readLine();

				option = ' '; //Clearing option. This incase the user enterd X/x the program will not exit.

			} // if

			//////////// option 4 ////////////

			if ( ( option == '4' )) {
				System.out.println("\nEnter the Order ID you wish to delete:");
				String orderId = c.readLine(); // Assuming 'c' is your Console object

				// Assuming deleteOrder now returns a JSON string response
				try {
					String deleteResponse = api.deleteOrder(orderId);
					System.out.println(deleteResponse);
				} catch (Exception e) {
					System.out.println("Failed to delete the record from the server." + e);
				}

				System.out.println("\nPress enter to continue..." );
				c.readLine();

				option = ' '; // Clearing option

			} // if

			//////////// option 5 ////////////
			if (option == '5') {
				System.out.println("\nRegister a new user:");
				System.out.println("Enter userName:");
				String userName = keyboard.nextLine();
			
				char[] passwordArray;
				char[] verifyPasswordArray;
				do {
					System.out.println("Enter password:");
					passwordArray = System.console().readPassword();
					System.out.println("Retype password for verification:");
					verifyPasswordArray = System.console().readPassword();
					if (!Arrays.equals(passwordArray, verifyPasswordArray)) {
						System.out.println("Passwords do not match. Please try again.");
					}
				} while (!Arrays.equals(passwordArray, verifyPasswordArray));
			
				String password = new String(passwordArray);
				Arrays.fill(passwordArray, ' '); // Clear from memory for security
			
				// Assuming there's a method in api to register a new user
				try {
					String registerResponse = api.registerUser(userName, password);
					System.out.println(registerResponse);
				} catch (Exception e) {
					System.out.println("Failed to register a new user." + e);
				}
			
				System.out.println("\nPress enter to continue...");
				keyboard.nextLine(); // Assuming 'keyboard' is a Scanner reading System.in
			
				option = ' '; // Clearing option
			}
			
			// Option 6: User login
			if (option == '6') {
				System.out.println("Please enter your username:");
				String userName = System.console().readLine();

				System.out.println("Please enter your password:");
				char[] passwordArray = System.console().readPassword();

				String password = new String(passwordArray);
				Arrays.fill(passwordArray, ' '); // Clear from memory for security

				try {
					// Assuming api.loginUser returns a JWT token as a String
					System.out.println("sending login request with username: " + userName + " and password: " + password);

					String loginResponse = api.loginUser(userName, password);
					System.out.println("Login successful." + loginResponse);

					// Save the JWT token in memory for subsequent API calls
					// Assuming there's a global variable 'authToken' to store the JWT token
					//authToken = loginResponse.toJSON().getString("Token");
					// Regular expression to find the Token value
					Pattern pattern = Pattern.compile("\"Token\":\"([^\"]*)\"");
					Matcher matcher = pattern.matcher(loginResponse);

					if (matcher.find()) {
						String authToken = matcher.group(1); // Extract the Token value
						System.out.println("Extracted Token: " + authToken);
						api.setToken(authToken);
					} else {
						System.out.println("Token not found in the response.");
					}


				} catch (Exception e) {
					System.out.println("Failed to login." + e);
				}

				System.out.println("\nPress enter to continue...");
				keyboard.nextLine(); // Assuming 'keyboard' is a Scanner reading System.in

				option = ' '; // Clearing option
			}


			// Option 7: User logout
			if (option == '7') {
				String authToken = api.getToken();
				if (authToken != null && !authToken.isEmpty()) {
					try {
						// Assuming api.logoutUser takes the JWT token and invalidates it on the server
						api.logoutUser();
						System.out.println("Logout successful. You have been logged out.");

						// Clear the authToken variable after successful logout
						api.setToken("");
					} catch (Exception e) {
						System.out.println("Failed to logout." + e);
					}
				} else {
					System.out.println("You are not logged in.");
				}

				System.out.println("\nPress enter to continue...");
				keyboard.nextLine(); // Assuming 'keyboard' is a Scanner reading System.in

				option = ' '; // Clearing option
			}
			//////////// option X ////////////

			if ( ( option == 'X' ) || ( option == 'x' ))
			{
				// Here the user is done, so we set the Done flag and halt the system

				done = true;
				System.out.println( "\nDone...\n\n" );

			} // if

		} // while

  	} // main

} // OrdersUI
