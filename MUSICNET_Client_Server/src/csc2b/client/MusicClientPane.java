package csc2b.client;

//Importing the packages of the classes I will need.
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;

/**
 * @author - Motsoetsoana M.R.
 * @version - Practical 6 
 * 
 * GUI Pane for the MUSICNET client.
 * Handles socket communication directly.
 */
public class MusicClientPane extends GridPane
{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private TextArea output;

    public MusicClientPane() 
    {
        this.setHgap(10);
        this.setVgap(10);

        try 
        {
            socket = new Socket("localhost", 2024);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } 
        catch (IOException e) 
        {
            throw new RuntimeException("Could not connect to server", e);
        }

        // Input fields
        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        TextField fileIdField = new TextField();

        // Buttons
        Button loginBtn = new Button("MUSICIN");
        Button listBtn = new Button("MUSICLIST");
        Button downloadBtn = new Button("MUSICDOWN");
        Button logoutBtn = new Button("MUSICOUT");

        // Output
        output = new TextArea();
        output.setEditable(false);
        output.setPrefHeight(250);

        // Layout
        this.add(new Label("Username:"), 0, 0);
        this.add(userField, 1, 0);
        this.add(new Label("Password:"), 0, 1);
        this.add(passField, 1, 1);
        this.add(loginBtn, 2, 0, 1, 2);

        this.add(new Label("File ID:"), 0, 2);
        this.add(fileIdField, 1, 2);
        this.add(downloadBtn, 2, 2);

        this.add(listBtn, 0, 3);
        this.add(logoutBtn, 1, 3);

        this.add(output, 0, 4, 3, 1);

        // Event handlers
        loginBtn.setOnAction(e -> sendCommand("MUSICIN " + userField.getText() + " " + passField.getText()));
        listBtn.setOnAction(e -> sendCommand("MUSICLIST"));
        downloadBtn.setOnAction(e -> downloadFile(fileIdField.getText()));
        logoutBtn.setOnAction(e -> sendCommand("MUSICOUT"));
    }

    private void sendCommand(String cmd)
    {
        try 
        {
            out.println(cmd);
            String resp = in.readLine();
            output.appendText(resp + "\n");
        } catch (IOException e) 
        {
            output.appendText("NO Communication error\n");
        }
    }

    private void downloadFile(String id) 
    {
        try 
        {
            out.println("MUSICDOWN " + id);
            String resp = in.readLine();
            output.appendText(resp + "\n");

            if (!resp.startsWith("YES")) return;

            String[] parts = resp.split(" ");
            long fileSize = Long.parseLong(parts[1]);

            File savePath = Paths.get("data/client/" + id + ".pdf").toFile();
            try (FileOutputStream fos = new FileOutputStream(savePath))
            {
                InputStream is = socket.getInputStream();
                byte[] buffer = new byte[4096];
                long remaining = fileSize;
                int read;
                while (remaining > 0 && (read = is.read(buffer, 0, (int)Math.min(buffer.length, remaining))) != -1) {
                    fos.write(buffer, 0, read);
                    remaining -= read;
                }
            }
            output.appendText("YES File downloaded to " + savePath.getPath() + "\n");
        } catch (Exception e) 
        {
            output.appendText("NO Download failed: " + e.getMessage() + "\n");
        }
    }
}
