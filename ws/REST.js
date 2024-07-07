
/******************************************************************************************************************
* File:REST.js
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*   1.0 February 2018 - Initial write of assignment 3 for 2018 architectures course(ajl).
*
* Description: This module provides the restful webservices for the Server.js Node server. This module contains GET,
* and POST services.  
*
* Parameters: 
*   router - this is the URL from the client
*   connection - this is the connection to the database
*   md5 - This is the md5 hashing/parser... included by convention, but not really used 
*
* Internal Methods: 
*   router.get("/"... - returns the system version information
*   router.get("/orders"... - returns a listing of everything in the ws_orderinfo database
*   router.get("/orders/:order_id"... - returns the data associated with order_id
*   router.post("/order?"... - adds the new customer data into the ws_orderinfo database
*   router.delete("/orders/:order_id"... - deletes the data associated with order_id)
*   router.post("/api/users/register"... - registers a new user
*   router.post("/api/users/login"... - logs in a user
*   router.post("/api/users/logout"... - logs out a user
* External Dependencies: mysql
*
******************************************************************************************************************/

var mysql   = require("mysql");     //Database
const secretKey = 'yourSecretKey'; // Use a secure, environment-specific key
let activeTokens = {};
const bcrypt = require('bcrypt');
const crypto = require('crypto');
const fs = require('fs');
const path = require('path');

// Specify the log file path
const logFilePath = path.join(__dirname, 'ws.log');
// Create a write stream for the log file
const logStream = fs.createWriteStream(logFilePath, { flags: 'a' });

// Function to log messages with different levels
function logMessage(level, message) {
    const timestamp = new Date().toISOString();
    logStream.write(`[${timestamp}] [${level.toUpperCase()}] ${message}\n`);
}

// Middleware to check if the user is authenticated
function isAuthenticated(req, res, next) {
    console.log("Checking Authentication for user ");
    // logMessage('info', 'Authenticating user ');
    const authHeader = req.headers.authorization;
    let token;
    if (authHeader) {
        token = authHeader.split(" ")[1]; // Extract token from the Authorization header
    }

    if (token && activeTokens[token]) {
        // Token is active, proceed to the next middleware or route handler
        console.log('Authentication successful for user ' + extractUsername(token));
        logMessage('error', 'Authentication successful for user ' + extractUsername(token));
        next();
    } else {
        // Token is missing, inactive, or invalid
        logMessage('error', 'Authentication failed. Please login to access the resources');
        res.status(401).json({"Error": true, "Message": "Unauthorized"});
    }
}

// Assuming username is available and secretKey is defined
function generateToken(username, secretKey) {
    logMessage('debug', 'Generating Token');
    const timestamp = new Date().getTime(); // Current time
    const toBeHashed = `${username}${timestamp}${secretKey}`;
    const hash = crypto.createHash('sha256').update(toBeHashed).digest('hex');
    const tokenWithUsername = `${username}:${Buffer.from(hash).toString('base64')}`;
    return tokenWithUsername;
}

function extractUsername(tokenWithUsername) {
    const [username] = tokenWithUsername.split(':');
    return username;
}

function REST_ROUTER(router,connection) {
    var self = this;
    self.handleRoutes(router,connection);
}

// Here is where we define the routes. Essentially a route is a path taken through the code dependent upon the 
// contents of the URL
REST_ROUTER.prototype.handleRoutes= function(router,connection) {

    // GET with no specifier - returns system version information
    // req paramdter is the request object
    // res parameter is the response object
    router.get("/",function(req,res){
        res.json({"Message":"Orders Webservices Server Version 2.0"});
    });
    
    router.get("/test", function(req, res) {
        console.log("Test ...") 
        res.json({"Error" : false, "Message" : "test passed !"});
    });

    // GET for /orders specifier - returns all orders currently stored in the database
    // req paramdter is the request object
    // res parameter is the response object
    router.get("/orders", isAuthenticated, function(req,res){
        logMessage('info', "Getting all database entries..." );
        var query = "SELECT * FROM ??";
        var table = ["orders"];
        query = mysql.format(query,table);
        connection.query(query,function(err,rows){
            if(err) {
                logMessage('error', "Error executing MySQL query");
                res.json({"Error" : true, "Message" : "Error executing MySQL query"});
            } else {
                logMessage('info', "Orders retrieved successfully...");
                res.json({"Error" : false, "Message" : "Success", "Orders" : rows});
            }
        });
    });

    // GET for /orders/order id specifier - returns the order for the provided order ID
    // req paramdter is the request object
    // res parameter is the response object
    router.get("/orders/:order_id", isAuthenticated, function(req,res){
        logMessage('info', "Getting order ID: ", req.params.order_id );
        var query = "SELECT * FROM ?? WHERE ??=?";
        var table = ["orders","order_id",req.params.order_id];
        query = mysql.format(query,table);
        connection.query(query,function(err,rows){
            if(err) {
                logMessage('error', "Error executing MySQL query");
                res.json({"Error" : true, "Message" : "Error executing MySQL query"});
            } else {
                logMessage('info', "Order retrieved successfully...");
                res.json({"Error" : false, "Message" : "Success", "Users" : rows});
            }
        });
    });

    // POST for /orders?order_date&first_name&last_name&address&phone - adds order
    // req paramdter is the request object - note to get parameters (eg. stuff afer the '?') you must use req.body.param
    // res parameter is the response object 
    router.post("/orders", isAuthenticated, function(req,res){
        //console.log("url:", req.url);
        //console.log("body:", req.body);
        logMessage('info',"Adding to orders table " + req.body.order_date + "," + req.body.first_name + "," + req.body.last_name + "," + req.body.address + "," + req.body.phone);
        var query = "INSERT INTO ??(??,??,??,??,??) VALUES (?,?,?,?,?)";
        var table = ["orders","order_date","first_name","last_name","address","phone",req.body.order_date,req.body.first_name,req.body.last_name,req.body.address,req.body.phone];
        query = mysql.format(query,table);
        connection.query(query,function(err,rows){
            if(err) {
                logMessage('error', "Error executing MySQL query");
                res.json({"Error" : true, "Message" : "Error executing MySQL query"});
            } else {
                logMessage('info', "Order Added successfully...");
                res.json({"Error" : false, "Message" : "User Added !"});
            }
        });
    });

    // DELETE for /orders/:order_id
    // deletes the order with the provided order ID
    router.delete("/orders/:order_id", isAuthenticated, function(req, res) {
        logMessage('info', "Deleting order ID: " + req.params.order_id);
        var query = "DELETE FROM ?? WHERE ??=?";
        var table = ["orders", "order_id", req.params.order_id];
        query = mysql.format(query, table);
        connection.query(query, function(err, result) {
            if(err) {
                logMessage('info', "Success executing MySQL query " + err);
                res.json({"Error": true, "Message": "Error executing MySQL query"});
            } else {
                if(result.affectedRows == 0) {
                    // No rows affected means no order found with that ID
                    logMessage('error', "No order found with the given ID");
                    res.json({"Error": false, "Message": "No order found with the given ID"});
                } else {
                    logMessage('info', "Order deleted successfully");
                    res.json({"Error": false, "Message": "Order deleted successfully"});
                }
            }
        });
    });

    // POST for /api/users/register
    // Registers a new user with a username and password
    router.post("/users/register", function(req, res) {
        logMessage('info', "Registering user...");
        var username = req.body.username;
        var password = req.body.password;

        // Basic validation
        if (!username || !password) {
            logMessage('error', `Missing Username or Password`);
            return res.status(400).json({"Error": true, "Message": "Username and password are required"});
        }

        logMessage('info', `Registering User ${username}`);

        // Hash the password before storing it
        // console.log("before bcrypt ", password);
        var hashedPassword = bcrypt.hashSync(password, 10);
        // console.log("after bcrypt ", hashedPassword);

        var query = "INSERT INTO ?? (??, ??) VALUES (?, ?)";
        var table = ["users", "username", "password", username, hashedPassword];
        query = mysql.format(query, table);

        connection.query(query, function(err, result) {
            if (err) {
                res.json({"Error": true, "Message": "Error executing MySQL query"});
            } else {
                logMessage('info', `User ${username} registered successfully`);
                res.json({"Error": false, "Message": "User registered successfully"});
            }
        });
    });
    

    // POST for /api/users/login
    // Logs in a user with a username and password
    router.post("/users/login", function(req, res) {
        console.log("Body ...", req.body);
        var username = req.body.username;
        var password = req.body.password;

        logMessage('info', `Logging in user ${username}`);
        // Basic validation
        if (!username || !password) {
            logMessage('error', `Missing Username or Password`);
            return res.status(400).json({"Error": true, "Message": "Username and password are required"});
        }

        var query = "SELECT * FROM ?? WHERE ??=?";
        var table = ["users", "username", username];
        query = mysql.format(query, table);

        console.log("Provided Usernmae: ", username, " Password: ", password);
        connection.query(query, function(err, rows) {
            if (err) {
                res.json({"Error": true, "Message": "Error executing MySQL query"});
            } else {
                console.log("Rows length: ", rows.length);
                // console.log("Queried Password: ", rows[0].password);
                // console.log("Hashed Password: ", bcrypt.hashSync(password, 10));

                if (rows.length == 1 && bcrypt.compareSync(password, rows[0].password)) {
                    // Passwords match
                    logMessage('info', `User ${username} logged in successfully`);
                    console.log("Login successful for user ", username);
                    // console.log("Secret Key:", secretKey); // Debugging line

                    const token = generateToken(rows[0].username, secretKey);
                    // console.log("Generated Token:", token);

                    activeTokens[token] = true; // Mark the token as active
                    res.json({"Error": false, "Message": "Login successful", "Token": token});
                } else {
                    // Authentication failed
                    console.log("Login failed");
                    res.json({"Error": true, "Message": "Invalid username or password"});
                }
            }
        });
    });

    // POST for /api/users/logout
    // LogsOut a currently loggedIn user
    router.post("/users/logout", isAuthenticated, function(req, res) {
        const token = req.headers.authorization.split(" ")[1]; // Assuming the token is sent in the Authorization header
    
        if (!token || !activeTokens[token]) {
            return res.json({"Error": true, "Message": "Invalid or missing token"});
        }

        logMessage('info', `Logout successful`);
    
        // Mark the token as inactive
        activeTokens[token] = false;
        res.json({"Error": false, "Message": "Logout successful, token invalidated"});
    });
}

// The next line just makes this module available... think of it as a kind package statement in Java
module.exports = REST_ROUTER;