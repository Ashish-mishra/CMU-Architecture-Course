import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import java.rmi.registry.Registry;

public class LogServices extends UnicastRemoteObject implements LogServicesAI 
{

    private static final String LOG_FILE = "ms.log";
    private static Logger FILE_LOGGER = Logger.getLogger("Orders DB"); 

    public LogServices() throws RemoteException {}

    public static void main(String args[]) 
    { 	
    	// What we do is bind to rmiregistry, in this case localhost, port 1097. This is the default
    	// RMI port. Note that I use rebind rather than bind. This is better as it lets you start
    	// and restart without having to shut down the rmiregistry. 

        try 
        { 
            LogServices obj = new LogServices();

            Registry registry = Configuration.createRegistry();
            registry.bind("LogServices", obj);

            String[] boundNames = registry.list();
            System.out.println("Registered services:");
            for (String name : boundNames) {
                System.out.println("\t" + name);
            }

            // Logger setup
            System.setProperty("java.util.logging.SimpleFormatter.format", 
            "%1$tF %1$tT %4$s %5$s%6$s%n");
            FileHandler fileHandler = new FileHandler(LOG_FILE, true);
            fileHandler.setFormatter(new SimpleFormatter());
            FILE_LOGGER.addHandler(fileHandler);

        } catch (Exception e) {

            System.out.println("LogServices init err: " + e.getMessage()); 
            e.printStackTrace();
        } 

    }

    @Override
    public void log(String level, String message) throws RemoteException
    {
        Level levelValue = Level.parse(level);
        FILE_LOGGER.log(levelValue, message);
    }
    
}
