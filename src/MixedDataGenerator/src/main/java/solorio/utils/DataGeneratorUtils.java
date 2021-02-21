
package solorio.utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import weka.core.Attribute;
import weka.core.Instances;

/**
 *
 * @author SSF
 */
public class DataGeneratorUtils {

    public static int getNumFromInterval(int infLim, int supLim, Random ran) {
        double num = Math.floor(ran.nextDouble() * (supLim - infLim + 1) + infLim);
        return (int) num;
    }

    public static double getNumFromInterval(double infLim, double supLim, Random ran) {
        double num = ran.nextDouble() * (supLim - infLim + 1) + infLim;
        return num;
    }

    public static double[] getMean(double infLim, double supLim, int dim, Random ran) {
        double[] mean = new double[dim];
        DecimalFormat newFormat = new DecimalFormat("#.###");
        for (int i = 0; i < dim; i++) {
            mean[i] = Double.valueOf(newFormat.format(ran.nextDouble() * (supLim - infLim + 1) + infLim));
        }
        return mean;
    }

    public static double[][] getCovariances(double infLim, double supLim, int dim, Random ran) {
        double[][] covariance = new double[dim][dim];
        DecimalFormat newFormat = new DecimalFormat("#.###");

        for (int i = 0; i < dim; i++) {
            for (int j = 0; j <= i; j++) {
                if (i == j) {
                    covariance[i][j] = Double.valueOf(newFormat.format(ran.nextDouble() * (supLim - infLim + 1) + infLim));
                } else {
                    covariance[i][j] = covariance[j][i] = 0;
                }
            }
        }
        return covariance;
    }

    public static String getNominalVale(int i) {

        if (i < 0 || i > 25) {
            return null;
        }
        String[] values = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1", "i1", "j1", "k1", "l1", "m1", "n1", "o1", "p1", "q1", "r1", "s1", "t1", "u1", "v1", "w1", "x1", "y1", "z1"};
        return values[i];
    }

    public static int getNumOf(int total, int percent) throws IOException {
        if (percent < 0 || percent > 100) {
            throw new IOException("Percent out of range!");
        }
        return total * percent / 100;
    }

    public static double[] getProbVector(int numOfValues, Random ran) {
        DecimalFormat newFormat = new DecimalFormat("#.###");
        double[] prob = new double[numOfValues];
        double sum = 0;
        for (int i = 0; i < prob.length; i++) {
            double nextDouble = Double.valueOf(newFormat.format(ran.nextDouble()));
            prob[i] = nextDouble;
            sum += nextDouble;
        }

        for (int j = 0; j < prob.length; j++) {
            prob[j] = prob[j] / sum;
        }

        return prob;
    }

    public static double[] getLinearCombCoef(int numOfValues, double supLim, double infLim, Random ran) {
        DecimalFormat newFormat = new DecimalFormat("#.##");
        double[] vec = new double[numOfValues];
        double nextDouble;
        for (int i = 0; i < vec.length; i++) {
            nextDouble = 0;
            while (nextDouble == 0) {
                nextDouble = Double.valueOf(newFormat.format(getNumFromInterval(supLim, infLim, ran)));
            }
            vec[i] = nextDouble;
        }
        return vec;
    }

    public static double[][] getProbabilityMatrixForRelevantFeatures(Attribute[] relevantAttributes, Random ran) {
        Attribute attribute;
        double[][] probMatrix = new double[relevantAttributes.length][];
        for (int i = 0; i < relevantAttributes.length; i++) {
            attribute = relevantAttributes[i];
            probMatrix[i] = getProbVector(attribute.numValues(), ran);
        }
        return probMatrix;
    }

    public static Attribute[] getAttributesFromInstances(Instances instances, String attType, String type) {
        ArrayList<Attribute> attList = new ArrayList<>();
        Attribute att;

        int ATT_TYPE;
        if (type.equals("Numeric")) {
            ATT_TYPE = 0;
        } else {
            ATT_TYPE = 1;
        }

        for (int i = 0; i < instances.numAttributes() - 1; i++) {
            att = instances.attribute(i);
            if (isAtt(att.name(), attType, type) || attType.equals("") && att.type() == ATT_TYPE) {
                attList.add(att);
            }
        }
        if (attList.isEmpty()) {
            return null;
        }
        Attribute[] nomRelAttArray = new Attribute[attList.size()];
        for (int i = 0; i < attList.size(); i++) {
            nomRelAttArray[i] = attList.get(i);
        }
        return nomRelAttArray;
    }

    public static String[] getAttributesNamesFromInstances(Instances instances, String attType, String type) {
        ArrayList<String> attNameList = new ArrayList<>();
        Attribute att;

        int ATT_TYPE;
        if (type.equals("Numeric")) {
            ATT_TYPE = 0;
        } else {
            ATT_TYPE = 1;
        }
        for (int i = 0; i < instances.numAttributes() - 1; i++) {
            att = instances.attribute(i);
            if (isAtt(att.name(), attType, type) || attType.equals("") && att.type() == ATT_TYPE) {
                attNameList.add(att.name());
            }
        }
        if (attNameList.isEmpty()) {
            return null;
        }
        String[] nomRelAttArray = new String[attNameList.size()];
        for (int i = 0; i < attNameList.size(); i++) {
            nomRelAttArray[i] = attNameList.get(i);
        }
        return nomRelAttArray;
    }

    public static boolean isAtt(String name, String attType, String type) {
        StringTokenizer st;
        st = new StringTokenizer(name, "_");
        int cont = 0;
        while (st.hasMoreElements()) {
            String nextElement = st.nextToken();
            if (nextElement.equals(attType) || nextElement.equals(type)) {
                cont++;
            }
        }
        return cont == 2;
    }

    public static boolean isEqualArraySize(int[] instancesPerClusterArray) {
        int diff;
        for (int i = 0; i < instancesPerClusterArray.length; i++) {
            for (int j = 0; j < i; j++) {
                diff = instancesPerClusterArray[i] - instancesPerClusterArray[j];
                if (diff != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int[] getValuesOfFeatures(Attribute[] att) {
        Attribute attribute;
        int[] values = new int[att.length];
        for (int i = 0; i < att.length; i++) {
            attribute = att[i];
            values[i] = attribute.numValues();
        }
        return values;
    }

    public static ArrayList<String> getAttributeValues(Attribute att) {
        ArrayList<String> values = new ArrayList<>();
        for (int i = 0; i < att.numValues(); i++) {
            values.add(i, att.value(i));
        }
        return values;
    }

    public static Double[] convertToDouble(double[] values) {
        Double[] converted = new Double[values.length];
        for (int i = 0; i < converted.length; i++) {
            converted[i] = values[i];
        }
        return converted;
    }
    
        
    public static boolean checkAttributeType(Instances ins, int attType) {

        if (ins.classIndex() != -1) {
            for (int i = 0; i < ins.numAttributes() - 1; i++) {
                if (ins.attribute(i).type() == attType) {
                    return true;
                }
            }

        } else {
            for (int i = 0; i < ins.numAttributes(); i++) {
                if (ins.attribute(i).type() == attType) {
                    return true;
                }
            }
        }
        return false;
    }


    public static void main(String[] args) throws Exception {
        Random ran = new Random(5);
        JDKRandomGenerator rng = new JDKRandomGenerator();

        double[] mean = getMean(-5, 5, 10, ran);
        System.out.println("mean: " + Arrays.toString(mean));

        double[][] covariances = getCovariances(0.7, 1.5, 10, ran);
        System.out.println("Covariance matrix: \n" + Arrays.deepToString(covariances).replace("], ", "]\n"));

        MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(rng, mean, covariances);
        System.out.println("Multivariate sample: " + Arrays.toString(mnd.sample()));

        UniformRealDistribution urd = new UniformRealDistribution(rng, 0, 5);
        System.out.println("Uniform sample: " + urd.sample());

        System.out.println("Num of: " + getNumOf(109, 100));
        System.out.println("Num from interval: " + getNumFromInterval(0, 5, ran));

        System.out.println("Prob: " + Arrays.toString(getProbVector(3, ran)));

    }
}
