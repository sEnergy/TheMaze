package themaze.client.panels;

import themaze.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class GamesPanel extends JPanel implements ActionListener
{
    private final JList<String> mazes = new JList<>();
    private final JList<String> games = new JList<>();
    private final JSlider slider = new JSlider(1, 4, 1);

    public GamesPanel()
    {
        super();
        setPreferredSize(new Dimension(400, 400));
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        JButton newGame = new JButton("New Game");
        JButton joinGame = new JButton("Join Game");
        newGame.addActionListener(this);
        joinGame.addActionListener(this);
        mazes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        games.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        slider.setMajorTickSpacing(1);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setPreferredSize(new Dimension(100, 50));

        addToGrid(new JLabel("Mazes"), 0, 0, false);
        addToGrid(mazes, 0, 1, true);
        addToGrid(newGame, 0, 2, false);
        addToGrid(new JLabel("Players"), 0, 3, false);
        addToGrid(slider, 0, 4, false);
        addToGrid(new JLabel("Games"), 1, 0, false);
        addToGrid(games, 1, 1, true);
        addToGrid(joinGame, 1, 2, false);
    }

    private void addToGrid(Component component, int x, int y, boolean fill)
    {
        GridBagConstraints c = new GridBagConstraints();
        if (fill)
        {
            c.fill = GridBagConstraints.BOTH;
            c.weighty = 1;
        }
        c.weightx = 0.5;
        c.gridx = x;
        c.gridy = y;
        c.insets = new Insets(5, 5, 5, 5);
        add(component, c);
    }

    public void setMazes(String[] mazes) { this.mazes.setListData(mazes); }
    public void setGames(String[] games) { this.games.setListData(games); }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("New Game"))
        {
            int i = mazes.getSelectedIndex();
            if (i == -1)
                System.out.println("You have to select some maze!");
            else
                try { Client.newGame(i, slider.getValue()); }
                catch (IOException ex) { ex.printStackTrace(); }
        }
        else if (e.getActionCommand().equals("Join Game"))
        {
            Client.joinGame(5);
        }
    }
}
