package themaze.client;

import java.io.*;
import java.net.Socket;

public class Client
{
    public static void main(String[] args)
    {
        try
        {
            Socket socket = new Socket("localhost", 50000);
            PrintWriter send = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            final BufferedReader recv = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread()
            {
                public void run()
                {
                    try
                    {
                        String line;
                        while ((line = recv.readLine()) != null)
                            System.out.println(line);
                    }
                    catch (IOException e){}
                }
            }.start();

            while (!socket.isClosed())
                send.println(console.readLine());
        } catch (Exception e) { e.printStackTrace(); }
    }
}
