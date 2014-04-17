package themaze.client2;

import java.io.IOException;

public class ServerThread extends Thread
{
    private final Communication comm;
    private final GamePanel game;
    private int rows, columns;

    public ServerThread(Communication comm, GamePanel game) throws IOException
    {
        this.comm = comm;
        this.game = game;

        comm.sendStrings("game", "test");
    }

    public void run()
    {
        try
        {
            while (true)
            {
                switch (comm.readByte())
                {
                    case 1:
                        rows = comm.readByte();
                        columns = comm.readByte();
                        javax.swing.SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run() { game.setGameSize(rows, columns); }
                        });
                        break;
                    case 2:
                        final byte[] data = comm.readBytes(rows * columns);
                        javax.swing.SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run() { game.setGameData(data); }
                        });
                        break;
                    case 3:
                        comm.readString();
                }
            }
        }
        catch (IOException e) { System.out.print(e.getMessage()); }
    }
}
