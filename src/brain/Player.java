package brain;
import brain.lib.Matrix;
import pong.Paddle;
import pong.PongPanel;

import java.util.ArrayList;
import java.util.Random;

public class Player
{
    public double fitness = 0;
    public static int id = 0;
    public Paddle guy;
    public Matrix weights_ih;
    public Matrix weights_ho;
    public Matrix bias_hid;
    public Matrix bias_out;
    public int hid_nodes,in_nodes,out_nodes;

    public Player()
    {
        this.id ++;
        this.fitness = 0;
    }
    public Paddle getGuy()
    {
        int y = new Random().nextInt(PongPanel.GAME_HEIGHT-PongPanel.PADDLE_HEIGHT);
        guy = new Paddle(0,y,PongPanel.PADDLE_WIDTH, PongPanel.PADDLE_HEIGHT, id,PongPanel.maxFitness);
        return this.guy;
    }
    public void initialise()
    {

    }
    public void setFitness (double fitness)
    {
        this.fitness = fitness;
    }
    public double getFitness()
    {
        return this.fitness;
    }
    public Matrix getWeights_ih()
    {
        return this.weights_ih;
    }
    public Matrix getBias_hid ()
    {
        return this.bias_hid;
    }
    public Matrix getBias_out ()
    {
        return this.bias_out;
    }
    public Matrix getWeights_ho()
    {
        return this.weights_ho;
    }

}
