import java.util.Properties;
import java.io.FileReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LoggerClient
{
	String response = null;
	Properties registry = null;

	public LoggerClient() {
		  // Loads the registry from 'registry.properties'
		  // This files contains entries like:
		  //    <Service> = <host>:<port>
		  // indicating that a service is registered in 
		  // an RMI registry at host on port
		  registry = new Properties();
		  try {
			registry.load(new FileReader("registry.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void log(String level, String message)
	{
		   // Get the registry entry for LogServices service
		   String entry = registry.getProperty("LogServices");
		   String host = entry.split(":")[0];
		   String port = entry.split(":")[1];
		   // Get the RMI registry
		   try {
			Registry reg = LocateRegistry.getRegistry(host, Integer.parseInt(port));
			LogServicesAI obj = (LogServicesAI) reg.lookup("LogServices"); 
			obj.log(level, message);
		   } catch (Exception e) {
			e.printStackTrace();
		   }	
    }

}