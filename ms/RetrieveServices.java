/******************************************************************************************************************
* File: RetrieveServices.java
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
* Internal Methods:
*  String retrieveOrders(String token) - gets and returns all the orders in the orderinfo database
*  String retrieveOrders(String token, String id) - gets and returns the order associated with the order id
*  boolean isTokenValid(Stirng token) - checks if the token is valid
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

// Class to retrieve orders from the databaseimport java.util.logging.Level;

public class RetrieveServices extends UnicastRemoteObject implements RetrieveServicesAI
{         
    // Get the registry entry for DeleteServices service
    // Set up the JDBC driver name and database URL
    static final String JDBC_CONNECTOR = "com.mysql.jdbc.Driver";  
    static final String DB_URL = Configuration.getJDBCConnection();

    // Set up the orderinfo database credentials
    static final String USER = "root";
    static final String PASS = Configuration.MYSQL_PASSWORD;

    private LoggerClient logger = new LoggerClient();

    // Do nothing constructor
    public RetrieveServices() throws RemoteException {}

    // Main service loop
    public static void main(String args[]) 
    { 	
    	// What we do is bind to rmiregistry, in this case localhost, port 1099. This is the default
    	// RMI port. Note that I use rebind rather than bind. This is better as it lets you start
    	// and restart without having to shut down the rmiregistry. 
        try 
        { 
            RetrieveServices obj = new RetrieveServices();
            Registry registry = Configuration.createRegistry();
            registry.bind("RetrieveServices", obj);

            String[] boundNames = registry.list();
            System.out.println("Registered services:");
            for (String name : boundNames) {
                System.out.println("\t" + name);
            }
        } catch (Exception e) {
            System.out.println("RetrieveServices binding err: " + e.getMessage()); 
            e.printStackTrace();
        } 
    } // main

    // This method will return all the entries in the orderinfo database
    public String retrieveOrders(String token) throws RemoteException
    {
        // Check if authToken is valid
        if (!isTokenValid(token)) {
            return "Invalid Auth Token";
        }

      	// Local declarations
        Connection conn = null;		// connection to the orderinfo database
        Statement stmt = null;		// A Statement object is an interface that represents a SQL statement.
        String ReturnString = "[";	// Return string. If everything works you get an ordered pair of data
        							// if not you get an error string
        logger.log(Level.INFO.getName(), "Retriving All Orders");
        try
        {
            // Here we load and initialize the JDBC connector. Essentially a static class
            // that is used to provide access to the database from inside this class.
            Class.forName(JDBC_CONNECTOR);
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // Here we create the queery Execute a query. Not that the Statement class is part
            // of the Java.rmi.* package that enables you to submit SQL queries to the database
            // that we are connected to (via JDBC in this case).
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM orders";
            ResultSet rs = stmt.executeQuery(sql);

            //Extract data from result set
            while(rs.next())
            {
                //Retrieve by column name
                int id  = rs.getInt("order_id");
                String date = rs.getString("order_date");
                String first = rs.getString("first_name");
                String last = rs.getString("last_name");
                String address = rs.getString("address");
                String phone = rs.getString("phone");

                ReturnString = ReturnString +"{order_id:"+id+", order_date:"+date+", first_name:"+first+", last_name:"
                               +last+", address:"+address+", phone:"+phone+"}";
            }
            ReturnString = ReturnString +"]";

            //Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
            stmt.close(); 
            conn.close();
        } catch(Exception e) {
            logger.log(Level.SEVERE.getName(), "ERROR Retriving All Orders: " + e);
            ReturnString = e.toString();
        } 
        
        return(ReturnString);

    } //retrieve all orders

    // This method will returns the order in the orderinfo database corresponding to the id
    // provided in the argument.
    public String retrieveOrders( String token, String orderid) throws RemoteException
    {
        // Check if authToken is valid
        if (!isTokenValid(token)) {
            return "Invalid Auth Token";
        }

      	// Local declarations
        Connection conn = null;		// connection to the orderinfo database
        Statement stmt = null;		// A Statement object is an interface that represents a SQL statement.
        String ReturnString = "[";	// Return string. If everything works you get an ordered pair of data
        							// if not you get an error string
        logger.log(Level.INFO.getName(), "Retriving Orders By ID: " + orderid);
        try
        {
            // Here we load and initialize the JDBC connector. Essentially a static class
            // that is used to provide access to the database from inside this class.
            Class.forName(JDBC_CONNECTOR);

            //Open the connection to the orderinfo database
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // Here we create the queery Execute a query. Not that the Statement class is part
            // of the Java.rmi.* package that enables you to submit SQL queries to the database
            // that we are connected to (via JDBC in this case).
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM orders where order_id=" + orderid;
            ResultSet rs = stmt.executeQuery(sql);

            // Extract data from result set. Note there should only be one for this method.
            // I used a while loop should there every be a case where there might be multiple
            // orders for a single ID.

            while(rs.next())
            {
                //Retrieve by column name
                int id  = rs.getInt("order_id");
                String date = rs.getString("order_date");
                String first = rs.getString("first_name");
                String last = rs.getString("last_name");
                String address = rs.getString("address");
                String phone = rs.getString("phone");

                ReturnString = ReturnString +"{order_id:"+id+", order_date:"+date+", first_name:"+first+", last_name:"
                               +last+", address:"+address+", phone:"+phone+"}";
            }

            ReturnString = ReturnString +"]";

            //Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
            stmt.close(); 
            conn.close();
        } catch(Exception e) {
            logger.log(Level.SEVERE.getName(), "ERROR Retriving Orders By ID: " + orderid + " " + e);
            ReturnString = e.toString();
        } 

        return(ReturnString);

    } //retrieve order by id

    // Method to check if token is valid or not
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
           return obj.isTokenValid(token);
       } catch (Exception e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
       return false;
   }

} // RetrieveServices

