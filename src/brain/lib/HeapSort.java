package brain.lib;


import pong.Paddle;

public class HeapSort
{
    public static Paddle[] sort(Paddle[] i)
    {
        int N = i.length;

        for (int k = N/2; k > 0; k--)
            downheap(i, k, N);

        do
        {
            Paddle T = i[0];
            i[0] = i[N - 1];
            i[N - 1] = T;

            N = N - 1;
            downheap(i, 1, N);
        }
        while (N > 1);

        return i;
    }

    private static void downheap(Paddle i[], int k, int N)
    {
        Paddle T = i[k - 1];

        while (k <= N/2)
        {
            int j = k + k;
            if ((j < N) && (i[j - 1].getFitness() > i[j].getFitness()))
                j++;

            if (T.getFitness() <= i[j - 1].getFitness())
                break;

            else
            {
                i[k - 1] = i[j - 1];
                k = j;
            }
        }
        i[k - 1] = T;
    }
}
