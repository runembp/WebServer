import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.http.HttpResponse;
import java.nio.Buffer;
import java.time.LocalDate;
import java.util.StringTokenizer;

class WebServer
{
    public static void main(String[] args) throws IOException
    {
        System.out.println("Starting server");

        String sentence;

        ServerSocket socket = new ServerSocket(8080);
        System.out.println("Socket assigned to " + socket.getLocalPort());

        System.out.println("Trying to connect...");
        Socket connection = socket.accept();
        System.out.println("Connection established at: " + socket.getLocalSocketAddress());

        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        DataOutputStream outToClient = new DataOutputStream(connection.getOutputStream());
        sentence = inFromClient.readLine();
        System.out.println("FROM CLIENT: " + sentence);

        String path = "/Users/rune/Google Drev/Programmering/Skole/Webserver/src/";

        StringTokenizer tokenizer = new StringTokenizer(sentence);

        tokenizer.nextToken(); // HTTP Request
        String fileName = tokenizer.nextToken().toLowerCase(); // Filename - ensured to be lowercase

        File file = new File(path + fileName);

        if(fileName.equals("/") || fileName.equals("/index"))
        {
            file = new File(path + "index.html");
            outToClient.writeBytes("HTTP/1.1 200 Success\n");
        }
        if(fileName.equals("/coding"))
        {
            file = new File(path + "unavailable.html");
            outToClient.writeBytes("HTTP/1.1 500 Temporarily unavailable");
        }
        if(fileName.equals("/missingimplementation"))
        {
            file = new File(path + "missingimplementation.html");
            outToClient.writeBytes("HTTP/1.1 400 Functionality not yet implemented");
        }
        if(!file.exists())
        {
            file = new File(path + "error.html");
            outToClient.writeBytes("HTTP/1.1 404 File was not found\n");
        }

        FileInputStream fraFil = new FileInputStream(file);

        outToClient.writeBytes("Date: " + LocalDate.now() + "\n");
        outToClient.writeBytes("Content-length: " + file.length() + "\n");
        outToClient.writeBytes("Content-type: text/html\n");

        byte[] buffer = new byte[4096];
        while(fraFil.read(buffer) != -1)
        {
            outToClient.write(buffer);
        }

        connection.close();
        socket.close();
    }
}