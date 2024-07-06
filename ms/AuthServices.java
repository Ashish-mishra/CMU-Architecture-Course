/******************************************************************************************************************
* File: AuthServices.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class provides the concrete implementation of the retrieve micro services. These services run
* in their own process (JVM).
*
* Parameters: None
*
** Internal Methods:
*  String register(String username, String password) - Registers an user with the system
*  String login(String username, String password ) - Generates an authentication token for the user
*  String logout() - Logs out the user from the system
*  boolean isTokenValid(String token) - Checks if the token is valid
*
* External Dependencies: 
*	- rmiregistry must be running to start this server
*	= MySQL
	- orderinfo database 
******************************************************************************************************************/
import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.rmi.registry.Registry;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/* 
 * Class to register, login and logout users.
 */
public class AuthServices extends UnicastRemoteObject implements AuthServicesAI
{ 
    // Set up the JDBC driver name and database URL
    static final String JDBC_CONNECTOR = "com.mysql.jdbc.Driver";  
    static final String DB_URL = Configuration.getJDBCConnection();

    // Set up the orderinfo database credentials
    static final String USER = "root";
    static final String PASS = Configuration.MYSQL_PASSWORD;

    //static String authToken = null;
    private static final Map<String, Boolean> authTokens = new HashMap<>();

    public static void setTokenStatus(String token, boolean isValid) {
        authTokens.put(token, isValid);
    }

    // Do nothing constructor
    public AuthServices() throws RemoteException {}

    // Main service loop
    public static void main(String args[]) 
    { 	
    	// What we do is bind to rmiregistry, in this case localhost, port 1099. This is the default
    	// RMI port. Note that I use rebind rather than bind. This is better as it lets you start
    	// and restart without having to shut down the rmiregistry. 
        try 
        { 
            AuthServices obj = new AuthServices();

            Registry registry = Configuration.createRegistry();
            registry.bind("AuthServices", obj);

            String[] boundNames = registry.list();
            System.out.println("Registered services:");
            for (String name : boundNames) {
                System.out.println("\t" + name);
            }

        } catch (Exception e) {
            System.out.println("AuthServices binding err: " + e.getMessage()); 
            e.printStackTrace();
        } 
    } 

    // Hashes the password using SHA-256
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
        byte[] byteData = md.digest();

        // Convert the byte to hex format
        StringBuilder hexString = new StringBuilder();
        for (byte aByteData : byteData) {
            String hex = Integer.toHexString(0xff & aByteData);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Registers a user with the system
    public String register(String username, String password) {
        try
        {
            //Open the connection to the orderinfo database
            Class.forName(JDBC_CONNECTOR);
            Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // System.out.println("Creating statement...");
            Statement stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM orders";
            ResultSet rs = stmt.executeQuery(sql);
      
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            String hashedPassword = hashPassword(password);
            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, hashedPassword); 
                preparedStatement.executeUpdate();
                return "User registered successfully.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Registration failed: " + e.getMessage();
        }
    }

    // Generates an authentication token for the user
    public String login(String username, String password) {
        try {
            Class.forName(JDBC_CONNECTOR);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return "Database driver not found.";
        }

        try
        {
            //Open the connection to the orderinfo database
            Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
           
            String query = "SELECT password FROM users WHERE username = ?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    boolean hasResult = rs.next();
                    System.out.println( " rs.getString(\"password\") = " + rs.getString("password") + " hashPassword(password) = " + hashPassword(password)+ "....");       

                    if (hasResult && rs.getString("password").equals(hashPassword(password))) { 
                        String authToken = UUID.randomUUID().toString();
                        System.out.println("Generated token: " + authToken);
                        setTokenStatus(authToken, true);
                        return authToken;
                    }
                    else {
                            System.out.println("Invalid password.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // Logs out the user from the system
    public String logout(String authToken) {
        if (authToken != null) {
            setTokenStatus(authToken, false);
            return "User logged out successfully.";
        } else {
            return "No user is logged in.";
        }
    }

    // Checks if the token is valid
    public boolean isTokenValid(String token) {
        System.out.println("Checking token: " + token);
        System.out.println("Token status: " + authTokens.getOrDefault(token, false));
        return authTokens.getOrDefault(token, false);
    }

} // AuthServices