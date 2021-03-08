
package solorio.dataGenerators.distrib;

/**
 *
 * @author SSF
 */
import java.io.Serializable;
import java.io.IOException;
import java.util.Random;

/**
 * A distribution over a finite set of elements 0, 1, ..., k, specified with an
 * array of k probabilities pi_0,...,pi_k summing to 1.
 */
public class Multinomial implements Serializable, IntegerDist {

    double[] pi;
    Random rand;

    transient int totalCount;
    transient int[] counts;

    /**
     * Creates a Multinomial object representing the uniform distribution over k
     * elements.
     *
     * @param k
     * @param random
     */
    public Multinomial(int k, Random random) {
        rand = random;

        pi = new double[k];
        for (int i = 0; i < k; i++) {
            pi[i] = 1.0 / k;
        }

        counts = new int[k];
    }
//    
//        public Multinomial(int k, long seed) {
//        if (seed == -1) {
//            rand = new Random();
//        } else {
//            rand = new Random(seed);
//        }
//        pi = new double[k];
//        for (int i = 0; i < k; i++) {
//            pi[i] = 1.0 / k;
//        }
//
//        counts = new int[k];
//    }

    /**
     * Creates a Multinomial object with probabilities specified by the given
     * array.
     *
     * @param pi
     * @param random
     * @throws IllegalArgumentException if pi does not define a probability
     * distribution
     */
    public Multinomial(double[] pi, Random random) {
        rand = random;
        this.pi = (double[]) pi.clone();
        double sum = 0;
        for (int i = 0; i < pi.length; i++) {
            if ((pi[i] < 0) || (pi[i] > 1)) {
                throw new IllegalArgumentException("Probability " + pi[i]
                        + " for element " + i
                        + " is not valid.");
            }
            sum += pi[i];
        }
        if (Math.abs(sum - 1) > 1e-9) {
            throw new IllegalArgumentException("Probabilities sum to "
                    + sum + " rather than 1.0.");
        }

        counts = new int[pi.length];
    }

    /**
     * Returns the size of the set that this distribution is defined over.
     *
     * @return
     */
    public int size() {
        return pi.length;
    }

    /**
     * Returns the probability of element i.
     *
     * @param i
     * @return
     */
    @Override
    public double getProb(int i) {
        return pi[i];
    }

    /**
     * Returns the log of the probability of element i.
     *
     * @param i
     * @return
     */
    @Override
    public double getLogProb(int i) {
        return Math.log(pi[i]);
    }

    /**
     * Records an occurrence of element i, for use in updating parameters.
     *
     * @param i
     */
    public void collectStats(int i) {
        totalCount++;
        counts[i]++;
    }

    /**
     * Records n occurrences of an element i, for use in updating parameters.
     *
     * @param i
     * @param n
     */
    public void collectAggrStats(int i, int n) {
        totalCount += n;
        counts[i] += n;
    }

    /**
     * Sets the parameter array pi to the values that maximize the likelihood of
     * the elements passed to collectStats since the last call to updateParams.
     * Then clears the collected statistics, and returns the difference between
     * the log likelihood of the data under the new parameters and the log
     * likelihood under the old parameters.
     *
     * @return
     */
    public double updateParams() {
        double oldLogProb = 0;
        double newLogProb = 0;

        // Update parameters
        if (totalCount > 0) {
            for (int i = 0; i < counts.length; i++) {
                oldLogProb += (counts[i] * Math.log(pi[i]));
                pi[i] = counts[i] / (double) totalCount;
                newLogProb += (counts[i] * Math.log(pi[i]));
            }
        }

        // Clear statistics
        totalCount = 0;
        for (int i = 0; i < counts.length; i++) {
            counts[i] = 0;
        }

        return (newLogProb - oldLogProb);
    }

    /**
     * Returns an integer chosen at random according to this distribution.
     *
     * @return
     */
    @Override
    public int sample() {
        double target = rand.nextDouble();
        double cumProb = 0;
        for (int i = 0; i < pi.length; i++) {
            cumProb += pi[i];
            if (target < cumProb) {
                return i;
            }
        }
        return (pi.length - 1); // this shouldn't ever be executed
    }

    /**
     * Called when this object is read in from a stream through the
     * serialization API. It allocates a <code>counts</code> array of the
     * appropriate size.
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        counts = new int[pi.length];
    }

    public static void main(String[] args) {
        Random rn = new Random(0);
        //Multinomial multinomial = new Multinomial(5, -1);
        Multinomial multinomial = new Multinomial(new double[]{0.5, 0.10, 0.4}, rn);

        for (int i = 0; i < 10; i++) {
            System.out.print(multinomial.sample() + "\t");
        }
    }
}
