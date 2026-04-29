//This is the package in which the class is stored in.
package csc2b.server;

//Here below are the packages I will need to use in the class.
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Motsoetsoana M.R.
 * @version - Practical 06
 * 
 * This is the that is starting on the port 2024.
 * And creating the Music Handler for each client.
 */
public class Server 
{
	//This is the port the server will operate on.
	public static final int PORT = 2024;
	
	/**
	 * This is the entry-point into the client.
	 * @param args - array of Strings.
	 */
	public static void main(String[] args)
	{
			System.out.println("MUSICNET Server starting on port: " + PORT);
			
			//Creating the serverSocket
			try(ServerSocket serverSocket = new ServerSocket(PORT))
			{
				while(true)
				{
					//Accepting the client
					Socket client = serverSocket.accept();
					System.out.println("Client connected: " + client.getInetAddress());
					
					//Creating the new thread to handle this client
					new Thread(new MusicHandler(client)).start();
				}
			}
			catch (IOException e) 
			{
				//If port is already in use or IO error occurs.
				e.printStackTrace();
			}
	}
}
