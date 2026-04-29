//This class is stored in the package below.
package csc2b.server;

//These are the packages I will need.
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author - Motsoetsoana M.R.
 * @version - Practical 06.
 * 
 * This is the class that is managing the commands.
 * MUSICIN, MUSICLIST, MUSICOWN, MUSICOUT. 
 */
public class MusicHandler implements Runnable
{
	private Socket connection;
	private BufferedReader in;
	private PrintWriter out;
	private boolean loggedIn = false;
	private String username;
	
	/**
	 * This is the constructor for the class.
	 * @param connection - connection to the client.
	 */
	public MusicHandler(Socket connection)
	{
		this.connection = connection;
	}

	@Override
	public void run() 
	{
		try
		{
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			out = new PrintWriter(connection.getOutputStream(),true);
			
			String line;
			while((line = in.readLine()) != null)
			{
				processCommand(line.trim());
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				connection.close();
			} catch (IOException e2) 
			{
				
			}
		}
	}

	
	private void processCommand(String cmdLine) 
	{
		try 
		{
			String[] parts = cmdLine.split(" ");
			String cmd = parts[0].toUpperCase();
			
			switch (cmd) 
			{
				case "MUSICIN":
					if(parts.length < 3)
					{
						out.println("NO Missing username or password");
						return;
					}
					if(matchLogin(parts[1], parts[2]))
					{
						loggedIn = true;
						username = parts[1];
						out.println("Yes Login successful");
					}
					else
					{
						out.println("NO Invalid credentials");
					}
					break;
					
				case "MUSICLIST":
					if(!loggedIn)
					{
						out.println("Please login first");
						return;
					}
					ArrayList<String> files = getFileList();
					out.println("YES " + String.join(",", files));
					return;
					
				case "MUSICDOWN":
					if(!loggedIn)
					{
						out.println("Please login first");
						return;
					}
					if(parts.length < 2)
					{
						out.println("NO Missing file ID");
						return;
					}
					handleDownload(parts[1]);
					break;
					
				case "MUSICOUT":
					loggedIn = false;
					out.println("YES Logged out");
					break;
					
					default:
						out.println("NO Unkown command");
			}
		} 
		catch (Exception e)
		{
			out.println("NO Error: " + e.getMessage());
		}
	}

	/**
	 * Matches the username and the password against the user.
	 * @param userN - the username.
	 * @param passW - the password.
	 * @return - return true if user exists, false otherwise.
	 */
	private boolean matchLogin(String userN, String passW)
	{
		boolean found = false;
		
		//Code to search users.txt file for match with userN and passW.
		File userFile = new File("data/server/User.txt");
		try
		{
		    Scanner scan = new Scanner(userFile);
		    while(scan.hasNextLine()&&!found)
		    {
				String line = scan.nextLine();
				String lineSec[] = line.split("\\s");
	    		
				//***OMITTED - Enter code here to compare user***
				if(lineSec.length == 2 && lineSec[0].equals(userN) && lineSec[1].equals(passW))
				{
					found = true;
				}
				
		    }
			scan.close();
		}
		catch(IOException ex)
		{
		    ex.printStackTrace();
		}
		
		return found;
	}
	
	private ArrayList<String> getFileList()
	{
		ArrayList<String> result = new ArrayList<String>();
		
		//Code to add list text file contents to the arraylist.
		File lstFile = new File("data/server/Docs.txt");
		try
		{
			Scanner scan = new Scanner(lstFile);

			//***OMITTED - Read each line of the file and add to the arraylist***
			while(scan.hasNextLine())
			{
				result.add(scan.nextLine());
			}
			
			scan.close();
		}	    
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		
		return result;
	}
	
	private String idToFileName(String strID)
	{
		String result ="";
		
		//Code to find the file name that matches strID
		File lstFile = new File("data/server/Docs.txt");
    	try
    	{
    		Scanner scan = new Scanner(lstFile);

    		String results = "";
    		//***OMITTED - Read filename from file and search for filename based on ID***
    		while (scan.hasNextLine())
    		{
                String line = scan.nextLine();
                String[] parts = line.split("\\s+");
                if (parts.length == 2 && parts[0].equals(strID))
                {
                    result = parts[1];
                    break;
                }
    		
    		scan.close();
    		}}
    	catch(IOException ex)
    	{
    		ex.printStackTrace();
    	}
		return result;
	}
	
	public void handleDownload(String id)
	{
		try {
            String filename = idToFileName(id);
            if (filename.isEmpty()) {
                out.println("NO Invalid file ID");
                return;
            }

            File pdfFile = new File("data/server/" + id + ".pdf");
            if (!pdfFile.exists()) {
                out.println("NO File not found on server");
                return;
            }

            long size = pdfFile.length();
            out.println("YES " + size);

            OutputStream os = connection.getOutputStream();
            Files.copy(pdfFile.toPath(), os);
            os.flush();
        } catch (IOException e) {
            out.println("NO Error sending file");
        }
	}
}
