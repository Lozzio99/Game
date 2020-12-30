package pong;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.concurrent.Callable;

public class Score extends Rectangle
{
    public static int GAME_WIDTH;
    public static int GAME_HEIGHT;
    public static int bounces = 0;
    public static int lost;
    public static JFrame frame;
    public static Graphics graphics;
    public int CHAMPIONS_L = 0;



    public Score (int GAME_WIDTH,int GAME_HEIGHT)
    {
        this.GAME_WIDTH = GAME_WIDTH;
        this.GAME_HEIGHT = GAME_HEIGHT;

    }


    public void draw (Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.white);
        g2.setFont(new Font("Consolas",Font.PLAIN,15));
        Line2D.Double line = new Line2D.Double(GAME_WIDTH/2,0,GAME_WIDTH/2,GAME_HEIGHT);
        g2.draw(line);
        g2.drawString("players active: "+ CHAMPIONS_L,100,30);

        g2.drawString("fail : "+String.valueOf(lost),130,60);
        g2.drawString(" press SPACE for a new ball ", (GAME_WIDTH-300),20);

        g2.drawString("max fitness : "+String.valueOf(PongPanel.maxFitness),130,90);
        g2.setFont(new Font("Consolas",Font.PLAIN,20));
        g2.drawString("bounces : "+String.valueOf(bounces /1000)+String.valueOf(bounces %1000),(GAME_WIDTH)-200,50);
    }
    public void set(int size)
    {
        CHAMPIONS_L= size;
    }


}
