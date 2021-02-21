
package solorio.dataGenerators.distrib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.commons.math3.distribution.AbstractMultivariateRealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * @author ssf
 */
public class MultivariateUniformRealDistribution extends AbstractMultivariateRealDistribution {

    List<UniformRealDistribution> uniformRealDistList;

    public MultivariateUniformRealDistribution(RandomGenerator rng, int n, double lower, double upper, boolean equalInterval) {
        super(rng, n);
        if (equalInterval) {
            initialize(rng, n, lower, upper);
        } else {
            IntervalGenerator intervalGenerator = new IntervalGenerator(lower, upper, n);
            initialize(rng, n, lower, upper, intervalGenerator);

        }
    }

    private void initialize(RandomGenerator rng, int n, double lower, double upper) {
        uniformRealDistList = new ArrayList<UniformRealDistribution>();

        for (int i = 0; i < n; i++) {
            uniformRealDistList.add(new UniformRealDistribution(rng, lower, upper));
        }
    }

    private void initialize(RandomGenerator rng, int n, double lower, double upper, IntervalGenerator intervalGenerator) {
        uniformRealDistList = new ArrayList<UniformRealDistribution>();

        for (int i = 0; i < n; i++) {
            double[] intervals = intervalGenerator.getIntervalsFor(i);
            uniformRealDistList.add(new UniformRealDistribution(rng, intervals[0], intervals[1]));
        }

    }

    @Override
    public double[] sample() {
        double[] values = new double[uniformRealDistList.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = uniformRealDistList.get(i).sample();

        }
        return values;
    }

    @Override
    public double density(double[] x) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void main(String[] args) {
        int[] vals = new int[]{3, 5, 8, 7};
        double[][] vals2 = new double[][]{{0.3, 0.2, 0.2, 0.3}, {0.4, 0.3, 0.2, 0.1}, {1}};
        Random random = new Random(16);

        RandomGenerator rng = new JDKRandomGenerator();
        rng.setSeed(0);

        MultivariateUniformRealDistribution mud = new MultivariateUniformRealDistribution(rng, 4, 5, 20, true);
        double[] sample = mud.sample();
        System.out.println("Values: " + Arrays.toString(sample));
    }

    public class IntervalGenerator {

        double lower;
        double upper;
        int n;

        public IntervalGenerator(double lower, double upper, int n) {
            this.lower = lower;
            this.upper = upper;
            this.n = n;
        }

        public double[] getIntervalsFor(int i) {
            double[] interval = new double[2];
            double range = upper - lower;
            double block = range / n;

            interval[0] = lower + (i * block);
            interval[1] = interval[0] + block;

            return interval;
        }
    }

}
