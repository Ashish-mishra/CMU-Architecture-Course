/******************************************************************************************************************
* File:RESTClientAPI.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class is used to provide access to a Node.js server. The server for this application is 
* Server.js which provides RESTful services to access a MySQL database.
*
* Parameters: None
*
* Internal Methods: None
*  String retrieveOrders() - gets and returns all the orders in the orderinfo database
*  String retrieveOrders(String id) - gets and returns the order associated with the order id
*  String sendPost(String Date, String FirstName, String LastName, String Address, String Phone) - creates a new 
*  order in the orderinfo database
*  String deleteOrder(String id) - deletes an existing order in the orderinfo database
*  String registerUser(String userName, String password) - registers a new user with the provided username and password
*  String loginUser(String username, String password) - logs in an existing user with the provided username and password
*  String logoutUser(String authToken) - logs out the current user
*
* External Dependencies: None
******************************************************************************************************************/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WSClientAPI
{
	private String token; // Attribute to hold the token

    // Getter for the token
    public String getToken() {
        return token;
    }
	    // Setter for the token
		public void setToken(String token) {
			this.token = token;
		}
	

	/********************************************************************************
	* Description: Gets and returns all the orders in the orderinfo database
	* Parameters: None
	* Returns: String of all the current orders in the orderinfo database
	********************************************************************************/
	public String retrieveOrders() throws Exception
	{
		// Set up the URL and connect to the node server
		String url = "http://localhost:3000/api/orders";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//Form the request header and instantiate the response code
		con.setRequestMethod("GET");
		con.setRequestProperty("Authorization", "Bearer " + token); 
		int responseCode = con.getResponseCode();

		//Set up a buffer to read the response from the server
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		//Loop through the input and build the response string.
		//When done, close the stream.
		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		in.close();

		return(response.toString());
	}
	
	/********************************************************************************
	* Description: Gets and returns the order based on the provided id from the
	*              orderinfo database.
	* Parameters: None
	* Returns: String of all the order corresponding to the id argument in the 
	*		   orderinfo database.
	********************************************************************************/
	public String retrieveOrders(String id) throws Exception
	{
		// Set up the URL and connect to the node server
		String url = "http://localhost:3000/api/orders/"+id;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//Form the request header and instantiate the response code
		con.setRequestMethod("GET");
		con.setRequestProperty("Authorization", "Bearer " + token); 
		int responseCode = con.getResponseCode();

		//Set up a buffer to read the response from the server
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		//Loop through the input and build the response string.
		//When done, close the stream.		
		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		in.close();

		return(response.toString());

	}

	/********************************************************************************
	* Description: Posts the new order to the orderinfo database
	* Parameters: None
	* Returns: String that contains the status of the POST operation
	********************************************************************************/
   	public String newOrder(String Date, String FirstName, String LastName, String Address, String Phone) throws Exception
	{
		// Set up the URL and connect to the node server		
		URL url = new URL("http://localhost:3000/api/orders");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// The POST parameters
		String input = "order_date="+Date+"&first_name="+FirstName+"&last_name="+LastName+"&address="+Address+"&phone="+Phone;

		//Configure the POST connection for the parameters
		conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept-Language", "en-GB,en;q=0.5");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-length", Integer.toString(input.length()));
        conn.setRequestProperty("Content-Language", "en-GB");
        conn.setRequestProperty("charset", "utf-8");
		conn.setRequestProperty("Authorization", "Bearer " + token); 
        conn.setUseCaches(false);
        conn.setDoOutput(true);

        // Set up a stream and write the parameters to the server
		OutputStream os = conn.getOutputStream();
		os.write(input.getBytes());
		os.flush();

		//Loop through the input and build the response string.
		//When done, close the stream.	
		BufferedReader in = new BufferedReader(new InputStreamReader((conn.getInputStream())));
		String inputLine;		
		StringBuffer response = new StringBuffer();

		//Loop through the input and build the response string.
		//When done, close the stream.		

		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		
		in.close();
		conn.disconnect();

		return(response.toString());
		
    } // newOrder

	/********************************************************************************
	* Description: Deletes the order based on the provided id from the
	*              orderinfo database.
	* Parameters: id - The ID of the order to delete
	* Returns: String - The response from the server after attempting to delete the order
	********************************************************************************/
	public String deleteOrder(String id) throws Exception {
		// Set up the URL for the DELETE request
		String url = "http://localhost:3000/api/orders/" + id;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// Specify that this is a DELETE request
		con.setRequestMethod("DELETE");
		con.setRequestProperty("Authorization", "Bearer " + token); 

		// Set up a buffer to read the response from the server
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		// Loop through the input and build the response string.
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close(); // When done, close the stream.

		return(response.toString());
	} // deleteOrder


	/********************************************************************************
	* Description: Registers a new user with the provided username and password.
	* Parameters: userName - The username of the new user
	*             password - The password for the new user
	* Returns: String - The response from the server after attempting to register the user
	********************************************************************************/
	public String registerUser(String userName, String password) throws Exception {
		// Set up the URL for the POST request to the user registration endpoint
		System.out.println("Registering user: " + userName + " with password: " + password	);

		String url = "http://localhost:3000/api/users/register";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// Specify that this is a POST request
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");

		// Enable sending data to the server
		con.setUseCaches(false);
		con.setDoOutput(true);

		// Create the JSON object to send
		String jsonInputString = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", userName, password);
		System.out.println(jsonInputString);

		// Write the JSON data to the request's output stream
		try(OutputStream os = con.getOutputStream()) {
			byte[] input = jsonInputString.getBytes("utf-8");
			os.write(input, 0, input.length);   
			os.flush();        
		}

		// Set up a buffer to read the response from the server
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		// Loop through the input and build the response string.
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close(); // When done, close the stream.

		return(response.toString());
	} // registerUser

	/********************************************************************************
	* Description: Issues a login token to existing users 
	* Parameters: userName - The username of the new user
	*             password - The password for the new user
	* Returns: String - authToken - The response from the server after attempting to login the user
	********************************************************************************/
	public String loginUser(String username, String password) throws IOException {
		URL url = new URL("http://localhost:3000/api/users/login"); 
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setDoOutput(true);

		// Create JSON payload with username and password
		String jsonInputString = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);

		// Write the JSON data to the request's output stream
		try (OutputStream os = con.getOutputStream()) {
			byte[] input = jsonInputString.getBytes("utf-8");
			os.write(input, 0, input.length);
			os.flush();        
		}

		// Set up a buffer to read the response from the server
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		// Loop through the input and build the response string.
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close(); // When done, close the stream.

		return(response.toString());
	} //loginUser

	/********************************************************************************
	* Description: Invalidates the login token for the current user
	* Parameters: authToken - The authentication token of the current user
	* Returns: String - The response from the server after attempting to logout the user
	********************************************************************************/
	public String logoutUser() throws IOException {
		URL url = new URL("http://localhost:3000/api/users/logout"); 
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST"); // Assuming logout is a POST request; adjust if necessary
		con.setRequestProperty("Content-Type", "application/json; utf-8");
		con.setRequestProperty("Accept", "application/json");
		//con.setRequestProperty("Authorization", "Bearer " + authToken); 
		con.setRequestProperty("Authorization", "Bearer " + token); 
		con.setDoOutput(true);

		// No need to send a JSON payload for logout, but the connection must be opened
		con.getOutputStream().close();

		// Set up a buffer to read the response from the server
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		// Read the server's response
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close(); // Close the BufferedReader

		return(response.toString());
	} // logoutUser
	
} // WSClientAPI