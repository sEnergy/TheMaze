package themaze.client2;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GamePanel extends JPanel
{
    private final Map<Integer, Image> images = new HashMap<>();
    private int rows, columns;
    private byte[] data;

    public GamePanel()
    {
        super();

        images.put(0, new ImageIcon("lib/gui/floor.png").getImage());
        images.put(1, new ImageIcon("lib/gui/wall.png").getImage());
        images.put(2, new ImageIcon("lib/gui/gate_closed.png").getImage());
        images.put(3, new ImageIcon("lib/gui/gate_open.png").getImage());
        images.put(4, new ImageIcon("lib/gui/key.png").getImage());
        images.put(5, new ImageIcon("lib/gui/finish.png").getImage());

        images.put(10, new ImageIcon("lib/gui/pl1.png").getImage());
        images.put(14, new ImageIcon("lib/gui/gate_pl1.png").getImage());

        images.put(100, new ImageIcon("lib/gui/dir_up.png").getImage());
        images.put(101, new ImageIcon("lib/gui/dir_right.png").getImage());
        images.put(102, new ImageIcon("lib/gui/dir_down.png").getImage());
        images.put(103, new ImageIcon("lib/gui/dir_left.png").getImage());
    }

    public void setGameSize(int rows, int columns)
    {
        this.rows = rows;
        this.columns = columns;
    }

    public void setGameData(byte[] data)
    {
        this.data = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (data == null || data.length != rows * columns)
            return;

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++)
            {
                int i = data[c + r * columns];
                if (i >9)
                {
                    int dir = (i % 10) % 4;
                    g.drawImage(images.get(i - dir), c * 20, r * 20, null);
                    g.drawImage(images.get(100 + dir), c * 20, r * 20, null);
                }
                else
                    g.drawImage(images.get(i), c * 20, r * 20, null);
            }

        setPreferredSize(new Dimension(columns * 20, rows * 20));
        ((JFrame)SwingUtilities.getRoot(this)).pack();
    }
}
