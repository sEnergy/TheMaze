package themaze.client.panels;

import themaze.client.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

/**
 * Panel obsahující grafické rozhraní pro vytvoření hry nebo připojení k rozehrané hře.
 *
 * @author Jaroslav Kubík
 * @author Marcel Fiala
 */
public class GamesPanel extends JPanel implements ActionListener
{
    private final JList<String> mazes = new JList<>();
    private final JList<String> games = new JList<>();
    private final JSlider players = new JSlider(1, 4, 1);
    private final JSlider speed = new JSlider(1, 10, 1);

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
        players.setMajorTickSpacing(1);
        players.setPaintTrack(true);
        players.setPaintLabels(true);
        speed.setMajorTickSpacing(2);
        speed.setMinorTickSpacing(1);
        speed.setPaintTrack(true);
        speed.setPaintLabels(true);

        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        for (int i = 1; i <= 5; i++)
            labels.put(i * 2, new JLabel(String.valueOf(i)));
        speed.setLabelTable(labels);

        addToGrid(new JLabel("Mazes"), 0, 0, false, 0);
        addToGrid(mazes, 0, 1, true, 1);
        addToGrid(newGame, 0, 2, false, 0);
        addToGrid(new JLabel("Players"), 0, 3, false, 0);
        addToGrid(players, 0, 4, true, 0);
        addToGrid(new JLabel("Games"), 1, 0, false, 0);
        addToGrid(games, 1, 1, true, 1);
        addToGrid(joinGame, 1, 2, false, 0);
        addToGrid(new JLabel("Speed"), 1, 3, false, 0);
        addToGrid(speed, 1, 4, true, 0);
    }

    private void addToGrid(Component component, int x, int y, boolean fill, int weight)
    {
        GridBagConstraints c = new GridBagConstraints();
        if (fill)
            c.fill = GridBagConstraints.BOTH;
        c.weighty = weight;
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
                MainFrame.getInstance().newGame(i, players.getValue(), speed.getValue());
        }
        else if (e.getActionCommand().equals("Join Game"))
        {
            int i = games.getSelectedIndex();
            if (i == -1)
                System.out.println("You have to select some game!");
            else
                MainFrame.getInstance().joinGame(i);
        }
    }
}
