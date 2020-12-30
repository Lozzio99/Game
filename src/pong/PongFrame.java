package pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PongFrame extends JFrame
{
    private PongPanel panel ;
    private WindowEvent listen;

    public PongFrame()
    {
        panel = new PongPanel();
        this.add(panel);
        this.setResizable(false);
        this.setBackground(Color.BLACK);
        WindowAdapter closed = new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                listen = new WindowEvent(PongFrame.getWindows()[0], 201);
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(listen);
                System.out.println("System closed by user");
                System.exit(0);
            }
        };
        this.addWindowListener(closed);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
