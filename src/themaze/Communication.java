package themaze;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Communication implements Closeable
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

    public Command readCommand() throws IOException { return Command.values()[input.read()]; }

    public String readString() throws IOException { return input.readUTF(); }
    public byte readByte() throws IOException { return input.readByte(); }
    public byte[] readBytes(int size) throws IOException
    {
        byte[] data = new byte[size];
        input.readFully(data, 0, size);
        return data;
    }

    public void sendCmd(Command cmd, int... data) throws IOException
    {
        synchronized (output)
        {
            output.writeByte(cmd.ordinal());
            for (int b : data)
                output.writeByte(b);
            output.flush();
        }
    }

    public void sendData(Command cmd, byte[] data, int... attributes) throws IOException
    {
        synchronized (output)
        {
            output.writeByte(cmd.ordinal());
            for (int b : attributes)
                output.writeByte(b);
            output.write(data);
            output.flush();
        }
    }

    public void sendStrings(Command cmd, String... strs) throws IOException
    {
        synchronized (output)
        {
            output.writeByte(cmd.ordinal());
            output.writeByte(strs.length);
            for (String str : strs)
                output.writeUTF(str);
            output.flush();
        }
    }

    @Override
    public void close() throws IOException { socket.close(); }

    public enum Command
    {
        //              S->C                                                C->S
        Game,   //      byte size, str[] names                              byte id, byte players, byte speed
        Join,   //      byte size, str[] names                              byte id
        Maze,   //      byte rows, byte columns, byte[] maze                ----------
        Change, //      byte row, column, data                              ----------
        Close,  //      byte 0/1/2 (start/won/lost)
        Keys,   //      byte keys
        Take,   //      byte 0/1 (success/fail)
        Open,   //      byte 0/1/2 (success/no key/no gate)
        Step,   //      sent only if fails
        Go,     //
        Stop,   //
        Left,   //
        Right   //
    }
}
