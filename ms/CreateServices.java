/******************************************************************************************************************
* File: CreateServices.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class provides the concrete implementation of the create micro services. These services run
* in their own process (JVM).
*
* Parameters: None
*
* Internal Methods:
*  String newOrder() - creates an order in the ms_orderinfo database from the supplied parameters.
*
* External Dependencies: 
*	- rmiregistry must be running to start this server
*	= MySQL
	- orderinfo database 
******************************************************************************************************************/
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;

// Class to create a new order
public class CreateServices extends UnicastRemoteObject implements CreateServicesAI
{ 
    // Set up the JDBC driver name and database URL
    static final String JDBC_CONNECTOR = "com.mysql.jdbc.Driver";  
    static final String DB_URL = Configuration.getJDBCConnection();

    // Set up the orderinfo database credentials
    static final String USER = "root";
    static final String PASS = Configuration.MYSQL_PASSWORD;

    private LoggerClient logger = new LoggerClient();

    // Do nothing constructor
    public CreateServices() throws RemoteException {}

    // Main service loop
    public static void main(String args[]) 
    { 	
    	// What we do is bind to rmiregistry, in this case localhost, port 1099. This is the default
    	// RMI port. Note that I use rebind rather than bind. This is better as it lets you start
    	// and restart without having to shut down the rmiregistry. 
        try 
        { 
            CreateServices obj = new CreateServices();

            Registry registry = Configuration.createRegistry();
            registry.bind("CreateServices", obj);

            String[] boundNames = registry.list();
            System.out.println("Registered services:");
            for (String name : boundNames) {
                System.out.println("\t" + name);
            }
        } catch (Exception e) {
            System.out.println("CreateServices binding err: " + e.getMessage()); 
            e.printStackTrace();
        } 
    } // main


    // This method add the entry into the ms_orderinfo database
    public String newOrder(String token, String idate, String ifirst, String ilast, String iaddress, String iphone) throws RemoteException
    {
      	// Local declarations
        System.out.println("Creating new order...");
        Connection conn = null;		                 // connection to the orderinfo database
        Statement stmt = null;		                 // A Statement object is an interface that represents a SQL statement.
        String ReturnString = "Order Created";	     // Return string. If everything works you get an 'OK' message
        							                 // if not you get an error string
        logger.log(Level.INFO.getName(), "Creating new Order");
        try
        {
            // Check if token passed is valid
            if(!isTokenValid(token)) {
                return "Invalid token";
            }
            
            // Here we load and initialize the JDBC connector. Essentially a static class
            // that is used to provide access to the database from inside this class.
            Class.forName(JDBC_CONNECTOR);

            //Open the connection to the orderinfo database
            try {
                conn = DriverManager.getConnection(DB_URL,USER,PASS);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Here we create the queery Execute a query. Not that the Statement class is part
            // of the Java.rmi.* package that enables you to submit SQL queries to the database
            // that we are connected to (via JDBC in this case).
            stmt = conn.createStatement();
            String sql = "INSERT INTO orders(order_date, first_name, last_name, address, phone) VALUES (\""+idate+"\",\""+ifirst+"\",\""+ilast+"\",\""+iaddress+"\",\""+iphone+"\")";

            // execute the update
            stmt.executeUpdate(sql);

            // clean up the environment
            stmt.close();
            conn.close();
            stmt.close(); 
            conn.close();

        } catch(Exception e) {
            logger.log(Level.SEVERE.getName(), "ERROR Creating new Order: " + e);
            ReturnString = e.toString();
        } 
        
        return(ReturnString);
    } //retrieve all orders

    // This method checks if the token is valid
    public boolean isTokenValid(String token) {
       // Get the registry entry for DeleteServices service
       Properties registry = null;
       registry = new Properties();
       try {
        registry.load(new FileReader("registry.properties"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       String entry = registry.getProperty("AuthServices");
       String host = entry.split(":")[0];
       String port = entry.split(":")[1];
   
       try {
           // Get the RMI registry
           Registry reg = LocateRegistry.getRegistry(host, Integer.parseInt(port));
           AuthServicesAI obj = (AuthServicesAI) reg.lookup("AuthServices");
           return obj.validateToken(token);
       } catch (Exception e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
       return false;
   }

} // RetrieveServices