package themaze.server.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Communication
{
    private final Socket socket;
    private final DataOutputStream output;
    private final DataInputStream input;

    public Communication(Socket socket) throws IOException
    {
        this.socket = socket;
        output = new DataOutputStream(socket.getOutputStream());
        input = new DataInputStream(socket.getInputStream());
    }

    public Command receive() throws IOException
    {
        synchronized (input)
        {
            Command cmd = new Command(input.readUTF());
            return cmd;
        }
    }

    public void send(String cmd)
    {

    }
}
