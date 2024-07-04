import java.rmi.*;

public interface LogServicesAI extends java.rmi.Remote 
{
    void log(String level, String message) throws RemoteException;
}
