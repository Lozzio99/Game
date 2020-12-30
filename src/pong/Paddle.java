package pong;


import brain.lib.NeuralNetwork;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

public class Paddle extends Rectangle
{
    public int id;
    public double fitness = 0;
    public boolean isSmart = true;
    public static double maxFitness = 0;
    public int yVelocity;
    public int speed = 25;
    public NeuralNetwork brain;


    public Paddle(int x , int y, int Paddle_W, int Paddle_H, int id, double maxFitness)
    {
        super(x,y,Paddle_W,Paddle_H);
        this.id = id;
        this.fitness =0;
    }
    public void keyPressed (KeyEvent e)
    {
        switch (id)
        {
            case 1 :
                if(e.getKeyCode()==KeyEvent.VK_A)
                {
                    setYDirection(-speed);
                    move();
                }
                if(e.getKeyCode()==KeyEvent.VK_Z)
                {
                    setYDirection(speed);
                    move();
                }
                break;
        }
    }

    // ? -> ? <- ? //
    public void goUp( )
    {
        setYDirection(speed);
        move();
    }
    public void goDown()
    {
        setYDirection(-speed);
        move();
    }
    public void stay()
    {
        setYDirection(0);
        move();
    }
    public void haveBrain (NeuralNetwork brain)
    {
        this.brain = brain;
    }
    public NeuralNetwork getNeurons ()
    {
        return this.brain;
    }
    public void keyReleased (KeyEvent e)
    {
        switch (id)
        {
            case 1 :
                if(e.getKeyCode()==KeyEvent.VK_A)
                {
                    setYDirection(0);
                    move();
                }
                if(e.getKeyCode()==KeyEvent.VK_Z)
                {
                    setYDirection(0);
                    move();
                }
                break;
        }
    }
    public void setYDirection(int yDirection)
    {
        yVelocity = yDirection;
    }
    public void move ()
    {
        y = y+ yVelocity;
    }
    public void draw (Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLUE);
        Rectangle2D.Double paddle = new Rectangle2D.Double(x,y,width,height);
        g2.fill(paddle);
        g2.setColor(Color.BLACK);
        g2.draw(paddle);
    }
    public void addFitness(double fitness)
    {
        this.fitness += fitness;
    }
    public void setFitness(double fitness)
    {
        this.fitness = fitness;
    }

    public void setMaxFitness(double fitness)
    {
        maxFitness = fitness;
    }
    public double getFitness()
    {
        return this.fitness;
    }


}
