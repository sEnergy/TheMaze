package themaze.client.panels;

import themaze.client.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ConnectPanel extends JPanel implements ActionListener
{
    private final JTextField server = new JTextField("localhost");
    private final JTextField port = new JTextField("50000");

    public ConnectPanel()
    {
        super();
        setPreferredSize(new Dimension(400, 400));
        setLayout(new GridBagLayout());

        JButton connect = new JButton("Connect");
        server.setPreferredSize(new Dimension(150, 20));
        server.setHorizontalAlignment(SwingConstants.CENTER);
        port.setPreferredSize(new Dimension(100, 20));
        port.setHorizontalAlignment(SwingConstants.CENTER);
        connect.addActionListener(this);

        addToGrid(new JLabel("Server"), 0, 0, 1);
        addToGrid(server, 0, 1, 1);
        addToGrid(new JLabel("Port"), 0, 2, 1);
        addToGrid(port, 0, 3, 1);
        addToGrid(connect, 1, 0, 4);
    }

    private void addToGrid(Component component, int x, int y, int height)
    {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.gridx = x;
        c.gridy = y;
        c.gridheight = height;
        add(component, c);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            int p = Integer.parseInt(port.getText());
            ((MainFrame) SwingUtilities.getRoot(this)).connect(server.getText(), p);
        }
        catch (NumberFormatException ex) { System.out.println("Invalid port!"); }
        catch (IOException ex) { ex.printStackTrace(); }
    }
}
