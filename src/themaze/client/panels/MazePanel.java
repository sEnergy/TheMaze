package themaze.client.panels;

import themaze.Position;
import themaze.Position.Direction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class MazePanel extends JPanel
{
    private final Map<Integer, Image> images = new HashMap<>();
    private int color, rows, columns;
    private byte[] data;
    private Map<Integer, Mobile> mobiles = new HashMap<>();
    private GameState state;

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

        setToolTipText("");
    }

    public boolean isReady() { return state == GameState.Started; }

    public void setMaze(byte color, byte rows, byte columns, byte[] data)
    {
        this.color = color * 10;
        this.rows = rows;
        this.columns = columns;
        this.data = data;
        this.mobiles.clear();
        state = GameState.Init;
        setPreferredSize(new Dimension(columns * 20, rows * 20));
        repaint();
    }

    public void start()
    {
        state = GameState.Started;
        System.out.println("Game started.");
    }

    public void finish(boolean winner)
    {
        state = winner ? GameState.Won : GameState.Lost;
        repaint();
    }

    public void change(int row, int column, byte newData)
    {
        if (newData < 10)
            data[column + row * columns] = newData;
        else
        {
            int dir = newData < 50 ? newData % 10 : 0;
            int m = newData - dir;

            if (row == -1)
                mobiles.remove(m);
            else
                mobiles.put(m, new Mobile(row, column, dir));

            if (column == -1)
                if (m == color)
                    state = GameState.Killed;
                else
                    System.out.printf("Player %d has been killed.\n", m / 10);
        }
        repaint();
    }

    public void onInfo(byte mobile, int steps)
    {
        int dir = mobile < 50 ? mobile % 10 : 0;
        int m = mobile - dir;
        mobiles.get(m).steps = steps;
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

        for (Map.Entry<Integer, Mobile> entry : mobiles.entrySet())
        {
            Mobile mobile = entry.getValue();
            int r = mobile.position.row;
            int c = mobile.position.column;
            int m = entry.getKey();
            if (m >= 50)
            {
                g.setColor(Color.RED);
                g.fillOval(c * 20, r * 20, 20, 20);
            }
            else
            {
                g.drawImage(images.get(m), c * 20, r * 20, null);
                g.drawImage(images.get(100 + mobile.dir.ordinal()), c * 20, r * 20, null);
            }
            if (m == color)
            {
                g.setColor(Color.RED);
                g.fillOval(c * 20 + 9, r * 20 + 9, 2, 2);
            }
        }

        if (state == GameState.Killed)
            drawStatus(g, "You have been killed!", Color.RED);
        else if (state == GameState.Won)
            drawStatus(g, "You have won!", Color.GREEN);
        else if (state == GameState.Lost)
            drawStatus(g, "You have lost.", Color.YELLOW);
    }

    private void drawStatus(Graphics g, String str, Color color)
    {
        g.setColor(color);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(str, g);
        int x = (columns * 20 - (int) r.getWidth()) / 2;
        int y = (rows * 20 - (int) r.getHeight()) / 2 + fm.getAscent();
        g.drawString(str, x, y);
    }

    @Override
    public String getToolTipText(MouseEvent event)
    {
        for (Map.Entry<Integer, Mobile> entry : mobiles.entrySet())
        {
            Mobile mobile = entry.getValue();
            Position position = new Position(event.getY() / 20, event.getX() / 20);
            if (position.equals(mobile.position) && mobile.steps != 0)
                return String.format("Steps: %d", mobile.steps);
        }
        return null;
    }

    private enum GameState
    {
        Init, Started, Killed, Won, Lost
    }

    private class Mobile
    {
        public final Position position;
        public final Direction dir;
        public int steps;

        private Mobile(int row, int column, int direction)
        {
            position = new Position(row, column);
            dir = Direction.values()[direction];
        }
    }
}
