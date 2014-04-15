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
            if (cmd.type.ordinal() < Command.CommandType.Show.ordinal())
                cmd.setData(input.readUTF());
            return cmd;
        }
    }

    public void sendBytes(int cmd, int ... bytes) throws IOException
    {
        synchronized (output)
        {
            output.writeByte(cmd);
            for (int i : bytes)
                output.writeByte(i);
            output.flush();
        }
    }

    public void sendString(int cmd, String str) throws IOException
    {
        synchronized (output)
        {
            output.writeByte(cmd);
            output.writeUTF(str);
            output.flush();
        }
    }

    public void sendData(int cmd, byte[] data) throws IOException
    {
        synchronized (output)
        {
            output.writeByte(cmd);
            output.write(data);
            output.flush();
        }
    }

    public void close() throws IOException
    {
        socket.close();
    }
}
