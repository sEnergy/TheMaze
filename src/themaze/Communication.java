package themaze;

import java.io.*;
import java.net.Socket;

/**
 * Třída řešící komunikaci mezi serverem a klientem.
 * Obsahuje metody pro přijímání a odesílání dat.
 *
 * @author Jaroslav Kubík
 */
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

    public Command readCommand() throws IOException { return Command.values()[input.readByte()]; }
    public String readString() throws IOException { return input.readUTF(); }
    public byte readByte() throws IOException { return input.readByte(); }
    public int readInt() throws IOException { return input.readInt(); }
    public byte[] readBytes(int size) throws IOException
    {
        byte[] data = new byte[size];
        input.readFully(data, 0, size);
        return data;
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

    public void sendInts(Command cmd, int... data) throws IOException
    {
        synchronized (output)
        {
            output.writeByte(cmd.ordinal());
            for (int i : data)
                output.writeInt(i);
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
        Maze,   //      byte playerColor, rows, byte columns, byte[] maze   ----------
        Change, //      byte row, column, data (invalid position = remove)  ----------
        Info,   //      byte data (negative = dead), int steps              ----------
        Close,  //      byte winner (0 = start)
        Keys,   //      byte keys
        Take,   //      byte 0/1 (success/fail)
        Open,   //      byte 0/1/2 (success/no key/no gate)
        Step,   //      sent only if fails
        Go,     //
        Stop,   //
        Left,   //
        Right   //
        /*
            data            object

            0               empty
            1               wall
            2               gate - closed
            3               gate - opened
            4               key
            5               finish

            1x/2x/3x/4x     players (1-4)
            x = direction (0 up, 1 right, 2 down, 3 left)

            50 to 127       guards
         */
    }
}
