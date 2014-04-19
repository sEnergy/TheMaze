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
    public int readInt() throws IOException { return input.readInt(); }
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
            for (int i : data)
                output.writeInt(i);
            output.flush();
        }
    }

    public void sendBytes(Command cmd, int... data) throws IOException
    {
        synchronized (output)
        {
            output.writeByte(cmd.ordinal());
            for (int b : data)
                output.writeByte(b);
            output.flush();
        }
    }

    public void sendMaze(byte rows, byte columns, byte[] data) throws IOException
    {
        synchronized (output)
        {
            output.writeByte(Command.Show.ordinal());
            output.writeByte(rows);
            output.writeByte(columns);
            output.write(data);
            output.flush();
        }
    }

    public void sendStrings(Command cmd, String... strs) throws IOException
    {
        synchronized (output)
        {
            output.writeByte(cmd.ordinal());
            output.writeInt(strs.length);
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
        Game,   //      int size, str[] names                               int id, int players
        Join,   //      int size, str[] names                               int id
        Show,   //      byte rows, byte columns, byte[] maze                ----------
        //              byte 1, byte size, (byte r, c, m)[] mobiles         ----------
        Close,  //      byte winner
        Keys,   //      byte keys
        Take,   //      byte 0/1 (change/success), r, c
        //              byte 2 (fail)
        Open,   //      byte 0/1 (change/success), r, c
        //              byte 2/3 (no key/no gate)
        Step,   //      only if fails
        Go,     //
        Stop,   //
        Left,   //
        Right   //
    }
}