package themaze.client;

import themaze.Communication;
import themaze.Communication.Command;

import java.io.IOException;
import java.net.Socket;

public class Client
{
    private static MainFrame frame;
    private static Communication comm;

    public static void main(String[] args)
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                frame = new MainFrame();
                frame.setVisible(true);
            }
        });
    }

    public static void connect(String host, int port) throws IOException
    {
        comm = new Communication(new Socket(host, port));
        new ServerThread(comm, frame).start();
    }

    public static void newGame(int id, int players) throws IOException
    { comm.sendCmd(Command.Game, id, players); }

    public static void joinGame(int id) throws IOException
    { comm.sendCmd(Command.Join, id); }
}
