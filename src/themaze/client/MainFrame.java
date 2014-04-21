package themaze.client;

import themaze.Communication;
import themaze.Communication.Command;
import themaze.client.panels.ConnectPanel;
import themaze.client.panels.GamesPanel;
import themaze.client.panels.MazePanel;

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
    private Communication comm;

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
            input.setText(null);
            for (Command cmd : Command.values())
                if (cmd.name().equalsIgnoreCase(e.getActionCommand().trim()))
                {
                    if (maze.getParent() != null && (cmd == Command.Close || maze.isReady()))
                        comm.sendCmd(cmd);
                    return;
                }
            System.out.println("Invalid command!");
        }
        catch (IOException ex) { ex.printStackTrace(); }
    }

    public void setCommunication(Communication comm)
    {
        this.comm = comm;
        remove(connect);
        add(games);
        repaint();
        pack();
        System.out.println("Connected.");
    }

    public void setMobiles(byte[] mobiles)
    {
        if (!maze.isReady())
            System.out.println("Game started.");
        maze.setMobiles(mobiles);
    }

    public void setMaze(byte rows, byte columns, byte[] data)
    {
        maze.setMaze(rows, columns, data);
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
            repaint();
            pack();
        }
    }

    public void onFinish(boolean winner) { maze.finish(winner); }
    public void onOpen(byte row, byte column) { maze.open(row, column); }
    public void onTake(byte row, byte column) { maze.take(row, column); }
}
