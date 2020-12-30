package pong;

import brain.Player;
import brain.lib.HeapSort;
import brain.lib.NeuralNetwork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class PongPanel extends JPanel implements Runnable
{
    public static final int GAME_WIDTH = 1000;
    public static final int GAME_HEIGHT = 600;
    public static final Dimension SCREEN = new Dimension(GAME_WIDTH,GAME_HEIGHT);
    public static final int BALL_D = 20;
    public static double ball_speed = 0;
    public static final int PADDLE_WIDTH = 25;
    public static final int PADDLE_HEIGHT = 100;
    public static final int MAX_POPULATION = 100;
    public Thread gameThread;
    public Image image;
    public Graphics graphics;
    public Random random;
    public ArrayList<Paddle> population ;
    public ArrayList<Paddle> newGeneration;
    public static ArrayList<Paddle> safe;
    public static double maxFitness = 0;
    public Ball ball;
    public Score score;
    public static NeuralNetwork smartest;
    public static Paddle bestGuy;
    public ArrayList<double [][]> movements;
    public KeyListener listener;

    public PongPanel()
    {
        this.newGeneration = new ArrayList<>();
        newPaddles();
        newBall();
        score = new Score(GAME_WIDTH,GAME_HEIGHT);
        this.setFocusable(true);
        this.setPreferredSize(SCREEN);
        listener = new AL();
        this.addKeyListener(listener);
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void newBall()
    {
        random = new Random();
        ball = new Ball((GAME_WIDTH/2)-(BALL_D/2),random.nextInt(GAME_HEIGHT-BALL_D),BALL_D,BALL_D);
    }
    public void newPaddles()
    {
        this.population = new ArrayList<>();
        //paddle1 = new Paddle(0,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH, PADDLE_HEIGHT, 1);
        for (int i = 0; i<MAX_POPULATION; i++)
        {
            NeuralNetwork brain = new NeuralNetwork(4,12,2,true);
            Paddle player = new Player().getGuy();
            player.haveBrain(brain);
            player.isSmart = true;
            player.setFitness(0);
            this.population.add(player);
        }
        smartest = this.population.get(new Random().nextInt(MAX_POPULATION)).getNeurons();
        bestGuy = new Player().getGuy();
        bestGuy.haveBrain(smartest);
        safe= new ArrayList<>();
        safe.add(bestGuy);
    }
    public void paint(Graphics g)
    {
        image = createImage(getWidth(),getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image,0,0,this);
    }
    public void draw(Graphics g)
    {
        for (Paddle guy : this.population)
        {
            if (guy.isSmart)
                guy.draw(g);
        }
        ball.draw(g);
        score.draw(g);
        score.set(this.population.size());
    }
    public void move()
    {
        ball.move();
    }


    public void checkCollision()
    {



        //bounce ball off top & bottom window edges
        if (ball.y <= 0)
            ball.setYDirection(-ball.yVelocity);
        if (ball.y>= GAME_HEIGHT-BALL_D)
            ball.setYDirection(-ball.yVelocity);


        for (Paddle guy : this.population)
        {
            if (guy.y<= 0)
                guy.y = 0;
            if (guy.y >= (GAME_HEIGHT-PADDLE_HEIGHT))
                guy.y = (GAME_HEIGHT-PADDLE_HEIGHT);
        }

        int intersect = 0;
        if (ball.x <=  PADDLE_WIDTH )
        {
            for (Paddle guy : this.population)
            {
                //bounce ball with paddles
                if (ball.intersects(guy))
                {
                    ball.xVelocity = Math.abs(ball.xVelocity);
                    ball.setXDirection(ball.xVelocity);

                    guy.addFitness(1);
                    intersect = 1;
                    guy.isSmart = true;
                }
                if (!ball.intersects(guy))
                {
                    guy.isSmart = false;
                }
            }
            Score.bounces += intersect;
        }
        if (ball.x<=2)
        {
            Score.lost++;
            Score.bounces = 0;
            ball_speed = 0;
            for (Paddle all : this.population )
            {
                all.isSmart = false;
                all.setFitness(0);
            }
            startAgain();
        }
        //if (ball.intersects(paddle2))
        if (ball.x >= GAME_WIDTH-BALL_D)
        {
            ball.xVelocity = Math.abs(ball.xVelocity)+ ball_speed;
            ball.setXDirection(-(ball.xVelocity));
        }
    }
    public NeuralNetwork givenBrainBy (Paddle bestGuy)
    {
        NeuralNetwork given = new NeuralNetwork(4,12,2,false);
        given.setAllWeights(bestGuy.getNeurons().getWeights_ho(),bestGuy.getNeurons().getBias_out(),bestGuy.getNeurons().getWeights_ih(),bestGuy.getNeurons().getBias_hid());
        return given;
    }

    public void getBest()
    {
        if (ball.x <=  PADDLE_WIDTH  )
        {
            double max = 1;
            for (Paddle paddle : this.population)
            {
                if (paddle.getFitness() >= max)
                {
                    max = paddle.getFitness();
                    bestGuy.setFitness(paddle.getFitness());
                    safe.add(paddle);
                    if (paddle.getFitness() > maxFitness)
                    {
                        smartest = this.givenBrainBy(paddle);
                        maxFitness = bestGuy.getFitness();
                    }
                }
            }
        }
    }
    public void startAgain()
    {
        // here ::   this population = new generation -> RESTART -> all fitness 0 -> new gen clear?

        if (safe.size() == 0)
        {
            newPaddles();
        }
        else
        {
            evolve();
        }
        //----------------------------//


        this.population.clear();
        this.population.addAll(this.newGeneration);
        this.newGeneration.clear();
        newBall();
    }
    public void evolve()
    {
        Paddle [] best = new Paddle[safe.size()];
        for (int i = 0; i< safe.size(); i++)
        {
            best[i] = safe.get(i);
        }
        HeapSort.sort(best);
        safe.clear();
        for (int i = 0; i< MAX_POPULATION; i++)
        {
            safe.add(best[i]);
            if (i+1 == best.length)
                break;
        }
        int count = 0;
        this.newGeneration.clear();
        for (Paddle paddle : safe)
        {
            double m = paddle.getFitness();
            m = m * 10;
            for (int k = 0; k <= m; k++)
            {
                if (count < MAX_POPULATION)
                {
                    Paddle guy = new Player().getGuy();
                    guy.haveBrain(givenBrainBy(paddle));
                    if (new Random().nextDouble() < 0.2)
                        guy.getNeurons().mutate();
                    guy.setFitness(0);
                    guy.isSmart = true;
                    this.newGeneration.add(guy);
                    count++;
                }
                else
                {
                    break;
                }
            }
        }
        double diff = MAX_POPULATION - this.newGeneration.size();
        if (diff >= 0)
        {
            for (int i = 0; i< diff ; i++)
            {
                this.newGeneration.add(safe.get(new Random().nextInt(safe.size())));
            }
        }
    }
    public void printStats()
    {

        System.out.println("----------------------------------------");
        /*
         System.out.println("\nGeneration : "+ score.lost + "    ||   Population size : "+this.population.size()+ "\nn_bounces :"+ this.score.bounces );
         System.out.println(" new generation size : "+ newGeneration.size());
         System.out.println(" \n<<<<>>>>>\n" + "best brain so far : "+ smartest );
         System.out.println("\n safe are : "+ safe.size());
         System.out.println("\n best guy fit  "+ bestGuy.getFitness() + " max fitness > "+ maxFitness);
         System.out.println("\n W_HO ");
         smartest.getWeights_ho().printMatrix();
         System.out.println("\n W_IH ");
         smartest.getWeights_ih().printMatrix();
         System.out.println("\n B_IH ");
         smartest.getBias_hid().printMatrix();
         System.out.println("\n B_HO ");
         smartest.getBias_out().printMatrix();
        */
        System.out.println("\n----------------------------------------");


    }
    public void checkdumb()
    {
        if (ball.x <= PADDLE_WIDTH)
        {
            var survival = new ArrayList<Paddle>();
            for (Paddle paddle : this.population)
            {
                if (paddle.isSmart)
                    survival.add(paddle);
            }
            if (survival.size()!= 0)
            {
                this.population.clear();
                this.population.addAll(survival);
            }
        }

    }
    public void run()
    {
        //game loop
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000/amountOfTicks;
        double delta = 0;
        while (true)
        {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1)
            {
                think();
                move();
                moveguys();
                checkCollision();
                checkdumb();
                getBest();
                repaint();
                delta--;
            }

        }
    }
    public void think()
    {
        movements = new ArrayList<>();
        for (Paddle guy : this.population)
        {
            double [] input = new double[]{guy.x,guy.y,ball.x, ball.y};
            double [][] outputs = guy.getNeurons().feedforward(input).getMatrix();
            movements.add(outputs);
        }
    }
    public void moveguys ()
    {
        for (int i = 0; i< this.population.size(); i++)
        {
            Paddle guy = this.population.get(i);
            double [][] outputs = this.movements.get(i);
            if (outputs[0][0]>= 0.3333)
            {
                guy.goUp();
            }
            else if (outputs[1][0]<= 0.6666)
            {
                guy.goDown();
            }
            else if (outputs[0][0]<= 0.3333 || outputs[1][0]>= 0.6666 )
            {
                guy.stay();
            }
        }
    }
    public class AL extends KeyAdapter
    {
        @Override
        public void keyPressed(KeyEvent e)
        {
            if (e.getKeyCode() == KeyEvent.VK_SPACE)
            {
                //Score.bounces = 0;
                newBall();
            }
        }
        @Override
        public void keyReleased(KeyEvent e)
        {
            System.out.println(" new game ");
        }
    }

}
