package themaze.client2;

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

    public byte readByte() throws IOException
    {
        return input.readByte();
    }

    public String readString() throws IOException
    {
        return input.readUTF();
    }

    public byte[] readBytes(int size) throws IOException
    {
        byte[] data = new byte[size];
        input.read(data, 0, size);
        return data;
    }

    public void sendStrings(String ... strs) throws IOException
    {
        synchronized (output)
        {
            for (String str : strs)
                output.writeUTF(str);
            output.flush();
        }
    }

    public void close() throws IOException
    {
        socket.close();
    }
}