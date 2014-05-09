package themaze.client;

import themaze.Communication.Command;
import themaze.client.panels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainFrame extends JFrame implements ActionListener
{
    private final ConnectPanel connect = new ConnectPanel();
    private final GamesPanel games = new GamesPanel();
    private final MazePanel maze = new MazePanel();
    private final JTextField input = new JTextField();
    private ServerThread server;

    public MainFrame()
    {
        super("The Maze");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setSize(600, 400);
        setResizable(false);
        input.addActionListener(this);

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 400));
        panel.setLayout(new GridBagLayout());
        addButton(panel, "close", 0, 0, 2, 50);
        addButton(panel, "left", 0, 1, 1, 10);
        addButton(panel, "right", 1, 1, 1, 10);
        addButton(panel, "go", 0, 2, 1, 10);
        addButton(panel, "stop", 1, 2, 1, 10);
        addButton(panel, "step", 0, 3, 1, 10);
        addButton(panel, "keys", 1, 3, 1, 10);
        addButton(panel, "take", 0, 4, 1, 10);
        addButton(panel, "open", 1, 4, 1, 10);
        addToGrid(panel, new JConsoleOutput(), 0, 5, 1, 2, 0);
        addToGrid(panel, input, 0, 6, 0, 2, 0);

        add(panel);
        add(connect);
        pack();
        setVisible(true);
    }

    public static void main(String[] args)
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run() { new MainFrame(); }
        });
    }

    private void addButton(JPanel panel, String text, int x, int y, int width, int inset)
    {
        JButton button = new JButton(text);
        button.addActionListener(this);
        addToGrid(panel, button, x, y, 0, width, inset);
    }

    private void addToGrid(JPanel panel, Component component, int x, int y, double weighty, int width, int inset)
    {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = x;
        c.gridy = y;
        c.weighty = weighty;
        c.gridwidth = width;
        c.insets = new Insets(2, inset, 2, inset);
        panel.add(component, c);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            if (e.getSource() == input)
                input.setText(null);

            Command cmd = getCmd(e.getActionCommand());
            if (cmd == null)
                System.out.println("Invalid command!");
            else if (maze.getParent() != null && (cmd == Command.Close || maze.isReady()))
                server.sendCmd(cmd);
            else if (games.getParent() != null && cmd == Command.Close)
                disconnect();
        }
        catch (IOException ex) { disconnect(); }
    }

    private Command getCmd(String str)
    {
        Command[] cmd = Command.values();
        for (int i = Command.Close.ordinal(); i < cmd.length; i++)
            if (cmd[i].name().equalsIgnoreCase(str.trim()))
                return cmd[i];
        return null;
    }

    public void joinGame(int id)
    {
        try { server.joinGame(id); }
        catch (IOException ex) { disconnect(); }
    }

    public void newGame(int id, int players, int speed)
    {
        try { server.newGame(id, players, speed); }
        catch (IOException ex) { disconnect(); }
    }

    public void connect(String host, int port)
    {
        try
        {
            server = new ServerThread(this, host, port);
            server.start();
            remove(connect);
            add(games);
            repaint();
            pack();
            System.out.println("Connected.");
        }
        catch (IOException ex) { System.out.println("Connection failed!"); }
    }

    public void disconnect()
    {
        if (server != null)
        {
            server.close();
            server = null;
            System.out.println("Disconnected.");
        }
        remove(games);
        remove(maze);
        add(connect);
        repaint();
        pack();
    }

    public void setMaze(byte color, byte rows, byte columns, byte[] data)
    {
        maze.setMaze(color, rows, columns, data);
        remove(games);
        add(maze);
        repaint();
        pack();
    }

    public void setMazes(String[] mazes) { games.setMazes(mazes); }
    public void setGames(String[] mazes)
    {
        games.setGames(mazes);
        if (maze.getParent() != null)
        {
            remove(maze);
            add(games);
            System.out.println("Game closed.");
            repaint();
            pack();
        }
    }

    public void onFinish(boolean winner) { maze.finish(winner); }
    public void onChange(byte row, byte column, byte data) { maze.change(row, column, data); }
    public void onStart() { maze.start(); }
    public void onInfo(int mobile, int steps) { maze.onInfo(mobile, steps); }
}
