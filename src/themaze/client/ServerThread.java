package themaze.client;

import themaze.Communication;

import java.io.IOException;

public class ServerThread extends Thread
{
    private final Communication comm;
    private final MainFrame frame;

    public ServerThread(Communication comm, MainFrame frame) throws IOException
    {
        this.comm = comm;
        this.frame = frame;
        frame.setCommunication(comm);
    }

    public void run()
    {
        try
        {
            while (true)
            {
                switch (comm.readCommand())
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

                    case Show:
                        final byte rows = comm.readByte();
                        final byte columns = comm.readByte();
                        final byte[] data = comm.readBytes(rows * columns);
                        javax.swing.SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                if (rows == 1)
                                    frame.setMobiles(data);
                                else
                                    frame.setMaze(rows, columns, data);
                            }
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
                        if (take == 2)
                            System.out.println("There is no key in front of you.");
                        else
                        {
                            if (take == 1)
                                System.out.println("You picked up a key.");
                            final byte row = comm.readByte();
                            final byte column = comm.readByte();
                            javax.swing.SwingUtilities.invokeLater(new Runnable()
                            {
                                public void run() { frame.onTake(row, column); }
                            });
                        }
                        break;

                    case Open:
                        final byte open = comm.readByte();
                        if (open == 2)
                            System.out.println("You don't have a key!");
                        else if (open == 3)
                            System.out.println("There is no gate in front of you.");
                        else
                        {
                            if (open == 1)
                                System.out.println("You opened the gate.");
                            final byte row = comm.readByte();
                            final byte column = comm.readByte();
                            javax.swing.SwingUtilities.invokeLater(new Runnable()
                            {
                                public void run() { frame.onOpen(row, column); }
                            });
                        }
                        break;

                    case Step:
                        System.out.println("You can't make a step that way.");
                        break;
                }
            }
        }
        catch (IOException e) { e.printStackTrace(); }
    }
}
