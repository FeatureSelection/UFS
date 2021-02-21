
package solorio.dataGenerators.distrib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author ssf
 */
public class MultivariateMultinomialDistribution {

    List<Multinomial> multinomialDistList;

    public MultivariateMultinomialDistribution(Random random, int[] attributeValues) {
        initializeWithUniformProbs(random, attributeValues);
    }

    public MultivariateMultinomialDistribution(Random random, double[][] attributeProbabilities) {
        initializeWithgivenProbs(random, attributeProbabilities);
    }

    private void initializeWithUniformProbs(Random random, int[] attributeValues) {
        multinomialDistList = new ArrayList<Multinomial>();
        for (int i = 0; i < attributeValues.length; i++) {
            int attributeValue = attributeValues[i];
            multinomialDistList.add(new Multinomial(attributeValue, random));

        }
    }

    private void initializeWithgivenProbs(Random random, double[][] attributeProbabilities) {
        multinomialDistList = new ArrayList<Multinomial>();
        for (double[] attributeProbability : attributeProbabilities) {
            multinomialDistList.add(new Multinomial(attributeProbability, random));
        }

    }

    public int[] sample() {
        int[] values = new int[multinomialDistList.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = multinomialDistList.get(i).sample();
        }
        return values;
    }

    public static void main(String[] args) {
        double[][] vals2 = new double[][]{{0.3, 0.2, 0.2,0.3}, {0.4, 0.3, 0.2, 0.1},{1}};
        Random random = new Random(4);
        MultivariateMultinomialDistribution multivariateMultinomialDistribution = new MultivariateMultinomialDistribution(random, vals2);
        int[] sample = multivariateMultinomialDistribution.sample();
        System.out.println("Values: " + Arrays.toString(sample));
    }

}
