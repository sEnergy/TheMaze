package themaze.client.panels;

import themaze.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class MazePanel extends JPanel
{
    private final Map<Integer, Image> images = new HashMap<>();
    private int rows, columns;
    private byte[] data;
    private Map<Position, Byte> mobiles = new HashMap<>();
    private boolean finished, winner, started;

    public MazePanel()
    {
        super();

        images.put(0, new ImageIcon("lib/gui/floor.png").getImage());
        images.put(1, new ImageIcon("lib/gui/wall.png").getImage());
        images.put(2, new ImageIcon("lib/gui/gate_closed.png").getImage());
        images.put(3, new ImageIcon("lib/gui/gate_open.png").getImage());
        images.put(4, new ImageIcon("lib/gui/key.png").getImage());
        images.put(5, new ImageIcon("lib/gui/finish.png").getImage());

        images.put(10, new ImageIcon("lib/gui/pl1.png").getImage());
        images.put(20, new ImageIcon("lib/gui/pl1.png").getImage());
        images.put(30, new ImageIcon("lib/gui/pl1.png").getImage());
        images.put(40, new ImageIcon("lib/gui/pl1.png").getImage());

        images.put(100, new ImageIcon("lib/gui/dir_up.png").getImage());
        images.put(101, new ImageIcon("lib/gui/dir_right.png").getImage());
        images.put(102, new ImageIcon("lib/gui/dir_down.png").getImage());
        images.put(103, new ImageIcon("lib/gui/dir_left.png").getImage());
    }

    public boolean isReady() { return !finished && started; }

    public void setMaze(int rows, int columns, byte[] data)
    {
        this.rows = rows;
        this.columns = columns;
        this.data = data;
        this.mobiles.clear();
        finished = winner = started = false;
        setPreferredSize(new Dimension(columns * 20, rows * 20));
        repaint();
    }

    public void start() { started = true; }
    public void finish(boolean winner)
    {
        finished = true;
        this.winner = winner;
        repaint();
    }

    public void change(int row, int column, byte newData)
    {
        if (newData < 0)
            mobiles.remove(new Position(row, column));
        else if (newData < 10)
            data[column + row * columns] = newData;
        else
        {
            for (Map.Entry<Position, Byte> mobile : mobiles.entrySet())
                if (newData / 10 == mobile.getValue() / 10)
                {
                    mobiles.remove(mobile.getKey());
                    break;
                }
            mobiles.put(new Position(row, column), newData);
        }
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
                g.drawImage(images.get(i), c * 20, r * 20, null);
            }

        for (Map.Entry<Position, Byte> mobile : mobiles.entrySet())
        {
            int r = mobile.getKey().row;
            int c = mobile.getKey().column;
            int m = mobile.getValue();
            int dir = m % 10;
            if (m == 50)
            {
                g.setColor(Color.RED);
                g.fillOval(c * 20, r * 20, 20, 20);
            }
            else
            {
                g.drawImage(images.get(m - dir), c * 20, r * 20, null);
                g.drawImage(images.get(100 + dir % 4), c * 20, r * 20, null);
            }
            if (dir > 3)
            {
                g.setColor(Color.RED);
                g.fillOval(c * 20 + 9, r * 20 + 9, 2, 2);
            }
        }

        if (finished)
        {
            String str = winner ? "You have won!" : "You have lost.";
            g.setColor(winner ? Color.GREEN : Color.RED);
            g.setFont(g.getFont().deriveFont(Font.BOLD, 20));
            FontMetrics fm = g.getFontMetrics();
            Rectangle2D r = fm.getStringBounds(str, g);
            int x = (columns * 20 - (int) r.getWidth()) / 2;
            int y = (rows * 20 - (int) r.getHeight()) / 2 + fm.getAscent();
            g.drawString(str, x, y);
        }
    }
}
