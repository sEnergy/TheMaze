package themaze.server;

import java.net.*;

public class Server
{
    public static void main(String[] args)
    {
        try
        {
            ServerSocket server = new ServerSocket(50000);
            while (true)
            {
                Socket client = server.accept();
                new ClientThread(client).start();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
