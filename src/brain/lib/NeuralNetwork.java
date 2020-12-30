package brain.lib;

import java.util.Random;

public class NeuralNetwork
{
    private Matrix weights_ho;
    private Matrix weights_ih;
    private Matrix bias_hid;
    private Matrix bias_out;
    private static double lk = 0.1;

    public void setLearningRate(double lk)
    {
        this.lk = lk;
    }
    public NeuralNetwork(int in_nodes, int hid_nodes, int out_nodes, boolean random)
    {
        if (random)
        {
            this.weights_ih = new Matrix(hid_nodes, in_nodes);
            this.weights_ih= Matrix.randomize(this.weights_ih);
            this.weights_ho = new Matrix(out_nodes,hid_nodes);
            this.weights_ho= Matrix.randomize(this.weights_ho);

            this.bias_hid = new Matrix(hid_nodes,1);
            this.bias_hid = Matrix.randomize(this.bias_hid);
            this.bias_out = new Matrix(out_nodes,1);
            this.bias_out = Matrix.randomize(this.bias_out);
        }
        else
        {
            this.weights_ih = new Matrix(hid_nodes, in_nodes);
            this.weights_ho = new Matrix(out_nodes,hid_nodes);
            this.bias_hid = new Matrix(hid_nodes,1);
            this.bias_out = new Matrix(out_nodes,1);
        }

    }
    public Matrix feedforward(double [] input)
    {
        //generating hidden output
        Matrix inputs = new Matrix(input);
        Matrix hidden = Matrix.multiply(this.weights_ih,inputs);
        hidden = Matrix.add(hidden,this.bias_hid);
        hidden = map(hidden);

        //generating final output   NEW MATRIX RESULTING FROM THE EVOLVING OUTPUT
        Matrix outputs = Matrix.multiply(this.weights_ho,hidden);
        outputs = Matrix.add(outputs,this.bias_out);
        outputs= map(outputs);
        return outputs;
    }

    public static Matrix map(Matrix m)
    {
        double [][] result = new double[m.n_rows][m.n_cols];
        for (int i = 0; i<m.n_rows; i++)
        {
            for (int k = 0; k< m.n_cols; k++)
            {
                result[i][k] = Math.tanh(m.getMatrix()[i][k]);
            }
        }
        return new Matrix(result);
    }

    public void setAllWeights (Matrix w_ho,Matrix b_ho, Matrix w_ih, Matrix b_ih)
    {
        this.weights_ho = w_ho;
        this.bias_out = b_ho;
        this.weights_ih = w_ih;
        this.bias_hid = b_ih;
    }
    public Matrix getWeights_ih()
    {
        return this.weights_ih;
    }
    public Matrix getWeights_ho()
    {
        return this.weights_ho;
    }
    public Matrix getBias_hid()
    {
        return this.bias_hid;
    }
    public Matrix getBias_out()
    {
        return this.bias_out;
    }

    public void mutate()
    {
        int x = new Random().nextInt(4);
        switch (x)
        {
            case 0:
                this.weights_ho = Matrix.mutate(this.weights_ho);
                break;
            case 1:
                this.weights_ih = Matrix.mutate(this.weights_ih);
                break;
            case 2:
                this.bias_hid = Matrix.mutate(this.bias_hid);
                break;
            case 3:
                this.bias_out = Matrix.mutate(this.bias_out);
                break;
        }

    }


    public void train(Matrix given, Matrix targets)
    {
        //generating hidden output
        Matrix inputs = given;
        Matrix hidden = Matrix.multiply(this.weights_ih,inputs);
        hidden = Matrix.add(hidden,this.bias_hid);
        //1
        hidden= Matrix.map(hidden);
        //generating final output   NEW MATRIX RESULTING FROM THE EVOLVING OUTPUT
        Matrix outputs =Matrix.multiply(this.weights_ho,hidden);
        outputs = Matrix.add(outputs,this.bias_out);
        outputs= Matrix.map(outputs);

        //   map((E-O)(dfunc(O))H^)

        //calculate out error
        //E-O
        Matrix tgt = new Matrix(targets.getMatrix());
        Matrix out_error = Matrix.subtract(tgt, outputs);
        // calculate out gradient
        Matrix o_gradient = Matrix.dfunc(outputs);
        o_gradient = o_gradient.multiply(out_error);
        o_gradient = o_gradient.multiply(this.lk);

        //hidden layer transposed
        Matrix hid_transp = Matrix.transpose(hidden);

        //calculate and adjust out weights
        //OH^
        Matrix dw_ho = Matrix.multiply(o_gradient,hid_transp);
        this.weights_ho = Matrix.add(this.weights_ho,dw_ho);
        this.bias_out = Matrix.add(this.bias_out,o_gradient);

        //calculate hidden error
        Matrix w_ho_transp = Matrix.transpose(this.weights_ho);

        Matrix hidden_er = Matrix.multiply(w_ho_transp,out_error);
        //calculate hidden gradient
        Matrix h_gradient = Matrix.dfunc(hidden);
        h_gradient = h_gradient.multiply(hidden_er);
        h_gradient = h_gradient.multiply(this.lk);

        //calculate and adjust hidden weights
        Matrix inputs_T = Matrix.transpose(inputs);
        Matrix d_ih = Matrix.multiply(h_gradient,inputs_T);

        this.weights_ih = Matrix.add(this.weights_ih,d_ih);

        this.bias_hid = Matrix.add(this.bias_hid,h_gradient);

    }
}
