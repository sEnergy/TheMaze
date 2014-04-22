package themaze.client;

import themaze.Communication;
import themaze.Communication.Command;

import java.io.IOException;
import java.net.Socket;

public class ServerThread extends Thread
{
    private final MainFrame frame;
    private final Communication comm;

    public ServerThread(MainFrame frame, String host, int port) throws IOException
    {
        this.frame = frame;
        comm = new Communication(new Socket(host, port));
    }

    public void sendCmd(Command cmd) throws IOException { comm.sendCmd(cmd);}
    public void joinGame(int id) throws IOException { comm.sendCmd(Command.Join, id); }
    public void newGame(int id, int players, int speed) throws IOException
    { comm.sendCmd(Command.Game, id, players, speed); }

    public void run()
    {
        try
        {
            while (true)
                handleCmd(comm.readCommand());
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    private void handleCmd(Command cmd) throws IOException
    {
        switch (cmd)
        {
            case Game:
                final String[] mazes = new String[comm.readInt()];
                for (int i = 0; i < mazes.length; i++)
                    mazes[i] = comm.readString();
                javax.swing.SwingUtilities.invokeLater(new Runnable()
                {
                    public void run() { frame.setMazes(mazes); }
                });
                break;

            case Join:
                final String[] games = new String[comm.readInt()];
                for (int i = 0; i < games.length; i++)
                    games[i] = comm.readString();
                javax.swing.SwingUtilities.invokeLater(new Runnable()
                {
                    public void run() { frame.setGames(games); }
                });
                break;

            case Maze:
                final byte rows = comm.readByte();
                final byte columns = comm.readByte();
                final byte[] maze = comm.readBytes(rows * columns);
                javax.swing.SwingUtilities.invokeLater(new Runnable()
                {
                    public void run() { frame.setMaze(rows, columns, maze); }
                });
                break;

            case Mobiles:
                final byte[] mobiles = comm.readBytes(comm.readByte());
                javax.swing.SwingUtilities.invokeLater(new Runnable()
                {
                    public void run() { frame.setMobiles(mobiles); }
                });
                break;

            case Change:
                final byte row = comm.readByte();
                final byte column = comm.readByte();
                final byte data = comm.readByte();
                javax.swing.SwingUtilities.invokeLater(new Runnable()
                {
                    public void run() { frame.onChange(row, column, data); }
                });
                break;

            case Close:
                final boolean winner = comm.readByte() == 1;
                javax.swing.SwingUtilities.invokeLater(new Runnable()
                {
                    public void run() { frame.onFinish(winner); }
                });
                break;

            case Keys:
                System.out.printf("You have %d key(s).\n", comm.readByte());
                break;

            case Take:
                final byte take = comm.readByte();
                if (take == 0)
                    System.out.println("You picked up a key.");
                else if (take == 1)
                    System.out.println("There is no key in front of you.");
                break;

            case Open:
                final byte open = comm.readByte();
                if (open == 0)
                    System.out.println("You opened the gate.");
                else if (open == 1)
                    System.out.println("You don't have a key!");
                else if (open == 2)
                    System.out.println("There is no gate in front of you.");
                break;

            case Step:
                System.out.println("You can't go that way.");
                break;
        }
    }
}
