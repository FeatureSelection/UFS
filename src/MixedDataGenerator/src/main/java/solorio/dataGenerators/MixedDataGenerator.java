
package solorio.dataGenerators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import solorio.dataGenerators.distrib.MultivariateMultinomialDistribution;
import solorio.dataGenerators.distrib.MultivariateUniformRealDistribution;
import solorio.dataGenerators.redgenerators.LinearCombNumericalRedundantGenerator;
import solorio.dataGenerators.redgenerators.MixedNominalRedundantGenerator;
import solorio.dataGenerators.redgenerators.NonMatchNominalRedundantGenerator;
import solorio.dataGenerators.redgenerators.RedundantGenerator;
import static solorio.utils.DataGeneratorUtils.*;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.datagenerators.ClassificationGenerator;

/**
 *
 * @author SSF
 */
public final class MixedDataGenerator extends ClassificationGenerator {

    protected Instances inputFormat;

    protected int m_NumOfNumericRelevantAttributes;

    protected int m_NumOfNominalRelevantAttributes;

    protected int m_NumOfNominalIrrelevantAttributes;

    protected int m_NumOfNumericIrrelevantAttributes;

    protected int m_NumOfNominalRedundantAttributes;

    protected int m_NumOfNumericRedundantAttributes;

    protected int m_maxNumOfValuesForNominalAttributes;

    protected double classBalanceRate;
    /**
     * Number of Classes the dataset should have
     */
    protected int m_NumClasses;

    protected boolean m_equalIntervalForIrrelevantNumericAttributes;

    //For numeric data parameters
    private RandomGenerator rng;
    private double minValueForMeanOfNumericFeatures;
    private double maxValueForMeanOfNumericFeatures;
    private double minValueForCovarianceOfNumericFeatures;
    private double maxValueForCovarianceOfNumericFeatures;

    private int numOfRelevantAndIrrelevantAttributes;

    private final HashMap<Integer, Integer> components = new LinkedHashMap<>(); //Map for containing the number of samples per cluster
    private MultivariateMultinomialDistribution irrelevantMmdGenerator = null; //For nominal irrelevant features
    private MultivariateUniformRealDistribution irrelevantMurdGenerator = null; //For numeric irrelevant features

    private RedundantGenerator m_redundantNumericalGen; //Numerical redundant feature Generator 
    private RedundantGenerator m_redundantNominalGen; // Nominal redundant feature Generator 

    public MixedDataGenerator() throws IOException {
        super();
        setInputFormat(null);
        setNumOfNumericRelevantAttributes(defaultNumberOfNumericRelevantAttributes());
        setNumOfNominalRelevantAttributes(defaultNumberOfNominalRelevantAttributes());
        setNumOfNumericIrrelevantAttributes(defaultNumberOfNumericIrrelevantAttributes());
        setNumOfNominalIrrelevantAttributes(defaultNumberOfNominalIrrelevantAttributes());
        setNumOfNumericRedundantAttributes(defaultNumOfNumericRedundantAttributesToAdd());
        setNumOfNominalRedundantAttributes(defaultNumOfNominalRedundantAttributesToAdd());

        setRedundantNominalGen(defaultRedundantNominalGenerator());
        setRedundantNumericalGen(defaultRedundantNumericGenerator());

        setClassBalanceRate(defaultClassBalanceRate());
        setNumClasses(defaultNumClasses());
        setEqualIntervalForIrrelevantNumericAttributes(defaultEqualIntervalForIrrelevantNumericAttributesValue());

        setMaxNumOfValuesForNominalAttributes(defaultmaxNumOfValuesForNominalAttributes());
        initializeComponents();
    }

    public MixedDataGenerator(Instances inputFormat) throws IOException {
        super();
        this.inputFormat = inputFormat;
        setNumOfNumericRelevantAttributes(defaultNumberOfNumericRelevantAttributes());
        setNumOfNominalRelevantAttributes(defaultNumberOfNominalRelevantAttributes());
        setNumOfNumericIrrelevantAttributes(defaultNumberOfNumericIrrelevantAttributes());
        setNumOfNominalIrrelevantAttributes(defaultNumberOfNominalIrrelevantAttributes());
        setNumOfNumericRedundantAttributes(defaultNumOfNumericRedundantAttributesToAdd());
        setNumOfNominalRedundantAttributes(defaultNumOfNominalRedundantAttributesToAdd());

        setRedundantNominalGen(defaultRedundantNominalGenerator());
        setRedundantNumericalGen(defaultRedundantNumericGenerator());

        setClassBalanceRate(defaultClassBalanceRate());
        setNumClasses(defaultNumClasses());
        setEqualIntervalForIrrelevantNumericAttributes(defaultEqualIntervalForIrrelevantNumericAttributesValue());

        setMaxNumOfValuesForNominalAttributes(defaultmaxNumOfValuesForNominalAttributes());
        initializeComponents();
    }

    private void initializeComponents() {
        rng = new JDKRandomGenerator();
        rng.setSeed(getSeed());

        minValueForMeanOfNumericFeatures = -5;
        maxValueForMeanOfNumericFeatures = 4;
        minValueForCovarianceOfNumericFeatures = 0.7;
        maxValueForCovarianceOfNumericFeatures = 6;
    }

    protected int defaultNumberOfNumericRelevantAttributes() {
        return 5;
    }

    protected int defaultNumberOfNominalRelevantAttributes() {
        return 5;
    }

    protected int defaultNumberOfNominalIrrelevantAttributes() {
        return 0;
    }

    protected int defaultNumberOfNumericIrrelevantAttributes() {
        return 0;
    }

    protected int defaultmaxNumOfValuesForNominalAttributes() {
        return 5;
    }

    protected double defaultClassBalanceRate() {
        return 1.0;
    }

    protected int defaultNumClasses() {
        return 2;
    }

    protected boolean defaultEqualIntervalForIrrelevantNumericAttributesValue() {
        return true;
    }

    protected int defaultNumOfNominalRedundantAttributesToAdd() {
        return 10;
    }

    protected int defaultNumOfNumericRedundantAttributesToAdd() {
        return 10;
    }

    protected RedundantGenerator defaultRedundantNominalGenerator() {
        // return new MixedNominalRedundantGenerator();
        return new NonMatchNominalRedundantGenerator();
    }

    protected RedundantGenerator defaultRedundantNumericGenerator() {
        return new LinearCombNumericalRedundantGenerator();
    }

    public int getNumOfNumericRelevantAttributes() {
        return m_NumOfNumericRelevantAttributes;
    }

    public void setNumOfNumericRelevantAttributes(int m_NumOfNumericRelevantAttributes) throws IOException {
        if (m_NumOfNumericRelevantAttributes < 0) {
            throw new IOException("Negative number of numeric relevant attributes!");
        }
        this.m_NumOfNumericRelevantAttributes = m_NumOfNumericRelevantAttributes;
    }

    public int getNumOfNominalRelevantAttributes() {
        return m_NumOfNominalRelevantAttributes;
    }

    public void setNumOfNominalRelevantAttributes(int m_NumOfNominalRelevantAttributes) throws IOException {
        if (m_NumOfNominalRelevantAttributes < 0) {
            throw new IOException("Negative number of nominal relevant attributes!");
        }
        this.m_NumOfNominalRelevantAttributes = m_NumOfNominalRelevantAttributes;
    }

    public int getNumOfNominalIrrelevantAttributes() {
        return m_NumOfNominalIrrelevantAttributes;
    }

    public void setNumOfNominalIrrelevantAttributes(int m_NumOfNominalIrrelevantAttributes) throws IOException {
        if (m_NumOfNominalIrrelevantAttributes < 0) {
            throw new IOException("Number of nominal irrelevant attributes is out of range: " + m_NumOfNominalIrrelevantAttributes + ". (0>= valid values)");
        }
        this.m_NumOfNominalIrrelevantAttributes = m_NumOfNominalIrrelevantAttributes;
    }

    public int getNumOfNumericIrrelevantAttributes() {
        return m_NumOfNumericIrrelevantAttributes;
    }

    public void setNumOfNumericIrrelevantAttributes(int m_NumOfNumericIrrelevantAttributes) throws IOException {
        if (m_NumOfNumericIrrelevantAttributes < 0) {
            throw new IOException("Number of numeric irrelevant attributes is out of range: " + m_NumOfNumericIrrelevantAttributes + ". (0>= valid values)");
        }
        this.m_NumOfNumericIrrelevantAttributes = m_NumOfNumericIrrelevantAttributes;
    }

    public int getNumOfNominalRedundantAttributes() {
        return m_NumOfNominalRedundantAttributes;
    }

    public void setNumOfNominalRedundantAttributes(int m_NumOfNominalRedundantAttributes) throws IOException {
        if (m_NumOfNominalRedundantAttributes < 0) {
            throw new IOException("We cannot add a negative number " + "(" + m_NumOfNominalRedundantAttributes + ")" + " of nominal attributes to the dataset." + " Valid values >=0");
        }
        this.m_NumOfNominalRedundantAttributes = m_NumOfNominalRedundantAttributes;
    }

    public int getNumOfNumericRedundantAttributes() {
        return m_NumOfNumericRedundantAttributes;
    }

    public void setNumOfNumericRedundantAttributes(int m_NumOfNumericRedundantAttributes) throws IOException {
        if (m_NumOfNumericRedundantAttributes < 0) {
            throw new IOException("We cannot add a negative number " + "(" + m_NumOfNumericRedundantAttributes + ")" + " of numerical attributes to the dataset." + " Valid values >=0");
        }
        this.m_NumOfNumericRedundantAttributes = m_NumOfNumericRedundantAttributes;
    }

    public int getMaxNumOfValuesForNominalAttributes() {
        return m_maxNumOfValuesForNominalAttributes;
    }

    public void setMaxNumOfValuesForNominalAttributes(int m_maxNumOfValuesForNominalAttributes) throws IOException {
        if (m_maxNumOfValuesForNominalAttributes < 2) {
            throw new IOException("Maximun number of values for attributes is <2: " + m_maxNumOfValuesForNominalAttributes + "!");
        }
        this.m_maxNumOfValuesForNominalAttributes = m_maxNumOfValuesForNominalAttributes;
    }

    public double getClassBalanceRate() {
        return classBalanceRate;
    }

    public void setClassBalanceRate(double classBalanceRate) throws IOException {
        if (classBalanceRate < 0 || classBalanceRate > 1) {
            throw new IOException("Class balance rate out of range: " + classBalanceRate + ". (0.0>= valid values <=1.0)");
        }
        this.classBalanceRate = classBalanceRate;
    }

    public boolean isEqualIntervalForIrrelevantNumericAttributes() {
        return m_equalIntervalForIrrelevantNumericAttributes;
    }

    public void setEqualIntervalForIrrelevantNumericAttributes(boolean m_equalIntervalForNumericAttributes) {
        this.m_equalIntervalForIrrelevantNumericAttributes = m_equalIntervalForNumericAttributes;
    }

    /**
     * Sets the number of classes the dataset should have.
     *
     * @param numClasses the new number of classes
     * @throws java.io.IOException
     */
    public void setNumClasses(int numClasses) throws IOException {
        if (numClasses < 2) {
            throw new IOException("Number of classes is <2: " + numClasses + "!");
        }
        m_NumClasses = numClasses;
    }

    /**
     * Gets the number of classes the dataset should have.
     *
     * @return the number of classes the dataset should have
     */
    public int getNumClasses() {
        return m_NumClasses;
    }

    public RedundantGenerator getRedundantNumericalGen() {
        return m_redundantNumericalGen;
    }

    public void setRedundantNumericalGen(RedundantGenerator redNumericalGen) {
        this.m_redundantNumericalGen = redNumericalGen;
    }

    public RedundantGenerator getRedundantNominalGen() {
        return m_redundantNominalGen;
    }

    public void setRedundantNominalGen(RedundantGenerator redNominalGen) {
        this.m_redundantNominalGen = redNominalGen;
    }

    public Instances getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(Instances inputFormat) {
        this.inputFormat = inputFormat;
    }

    /**
     * Generates a comment string that documentates the data generator. By
     * default this string is added at the beginning of the produced output as
     * ARFF file type, next after the options.
     *
     * @return string contains info about the generated rules
     * @throws java.lang.Exception
     */
    @Override
    public String generateStart() throws Exception {
        return "";
    }

    /**
     * Compiles documentation about the data generation. This is the number of
     * irrelevant attributes and the decisionlist with all rules. Considering
     * that the decisionlist might get enhanced until the last instance is
     * generated, this method should be called at the end of the data generation
     * process.
     *
     * @return string with additional information about generated dataset
     * @throws Exception no input structure has been defined
     */
    @Override
    public String generateFinished() throws Exception {
        return "";
    }

    /**
     * Gets the single mode flag.
     *
     * @return true if methode generateExample can be used.
     * @throws java.lang.Exception
     */
    @Override
    public boolean getSingleModeFlag() throws Exception {
        return false;
    }

    /**
     * Returns the revision string.
     *
     * @return the revision
     */
    @Override
    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1$");
    }

    @Override
    public Instance generateExample() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Generate all examples of the dataset.
     *
     * @return the instance generated
     * @throws Exception if format not defined or generating <br/>
     * examples one by one is not possible, because voting is chosen
     */
    @Override
    public Instances generateExamples() throws Exception {
        Instances format;
        if (inputFormat == null) {
            Random random = getRandom();
            format = getDatasetFormat();
            if (format == null) {
                throw new Exception("Dataset format not defined.");
            }
            int[] samplesByComponent = getNumberOfInstancesForCluster(getClassBalanceRate(), random);
            for (int k = 0; k < samplesByComponent.length; k++) {
                components.put(k, samplesByComponent[k]);
            }

            //Irrelevant attributes generators
            if (m_NumOfNumericIrrelevantAttributes > 0) {
                irrelevantMurdGenerator = new MultivariateUniformRealDistribution(rng, m_NumOfNumericIrrelevantAttributes, minValueForMeanOfNumericFeatures, maxValueForMeanOfNumericFeatures, isEqualIntervalForIrrelevantNumericAttributes());
            }
            if (m_NumOfNominalIrrelevantAttributes > 0) {
                Attribute[] irrelNominalAttributes = getAttributesFromInstances(format, "Irrelevant", "Nominal");
                int[] valuesOfIrrelevatFeatures = getValuesOfFeatures(irrelNominalAttributes);
                irrelevantMmdGenerator = new MultivariateMultinomialDistribution(random, valuesOfIrrelevatFeatures);
            }
            // generate values for all attributes
            format = generateInstances(random, format);

        } else {
            format = inputFormat;
        }

        // Adding Redundant attributes 
        if (m_NumOfNominalRedundantAttributes > 0) {
            addRedundantAttributes(format, AttributeType.NOMINAL_REDUNDANT, m_NumOfNominalRedundantAttributes);
        }

        if (m_NumOfNumericRedundantAttributes > 0) {
            addRedundantAttributes(format, AttributeType.NUMERIC_REDUNDANT, m_NumOfNumericRedundantAttributes);
        }
        return format;
    }

    private Instances generateInstances(Random random, Instances format) throws Exception {
        if (format == null) {
            throw new Exception("Dataset format not defined.");
        }
        //Data generation for each cluster
        Integer numOfInstancesForClusterK;
        for (int k = 0; k < components.size(); k++) {
            numOfInstancesForClusterK = components.get(k);
            insertInstancesForClusterK(format, numOfInstancesForClusterK, k, random);
        }

        return format;
    }

    private void insertInstancesForClusterK(Instances format, int numOfInstances, int k, Random random) {

        MultivariateNormalDistribution mnd = null;
        MultivariateMultinomialDistribution mmd = null;

        //Numeric relevant feature generator for cluster k
        if (m_NumOfNumericRelevantAttributes > 0) {
            double[] mean = getMean(minValueForMeanOfNumericFeatures, maxValueForMeanOfNumericFeatures, m_NumOfNumericRelevantAttributes, random);
            double[][] covariances = getCovariances(minValueForCovarianceOfNumericFeatures, maxValueForCovarianceOfNumericFeatures, m_NumOfNumericRelevantAttributes, random);
            mnd = new MultivariateNormalDistribution(rng, mean, covariances);        //Multivariate normal distribution;
        }

        //Nominal relevant feature generator for cluster k
        if (m_NumOfNominalRelevantAttributes > 0) {
            Attribute[] nominalRelevantAttributes = getAttributesFromInstances(format, "Relevant", "Nominal");
            double[][] probabilityMatrixForRelevantFeatures = getProbabilityMatrixForRelevantFeatures(nominalRelevantAttributes, random);
            mmd = new MultivariateMultinomialDistribution(random, probabilityMatrixForRelevantFeatures); //Multivariate multinomial distribution
        }
        //Set instances
        for (int i = 0; i < numOfInstances; i++) {
            Instance instance = getInstance(mnd, mmd, k, format);
            instance.setDataset(format);
            format.add(instance);
        }
    }

    private Instance getInstance(MultivariateNormalDistribution mnd, MultivariateMultinomialDistribution mmd, int component, Instances format) {
        double[] attributes;
        Instance example;
        int i = 0;
        int j = 0;
        int k = 0;

        attributes = new double[numOfRelevantAndIrrelevantAttributes + 1];
        if (mnd != null) {
            double[] mndValues = mnd.sample();
            for (i = 0; i < mndValues.length; i++) {
                attributes[i] = mndValues[i];
            }
        }
        if (mmd != null) {
            int[] mmdValues = mmd.sample();
            for (j = 0; j < mmdValues.length; j++) {
                attributes[i + j] = (double) mmdValues[j];
            }
        }

        if (irrelevantMurdGenerator != null) {
            double[] irrelevantMurdGeneratorValues = irrelevantMurdGenerator.sample();
            for (k = 0; k < irrelevantMurdGeneratorValues.length; k++) {
                attributes[i + j + k] = irrelevantMurdGeneratorValues[k];
            }
        }

        if (irrelevantMmdGenerator != null) {
            int[] irrelevantMmdGeneratorValues = irrelevantMmdGenerator.sample();
            for (int l = 0; l < irrelevantMmdGeneratorValues.length; l++) {
                attributes[i + j + k + l] = (double) irrelevantMmdGeneratorValues[l];
            }
        }

        example = new DenseInstance(1.0, attributes);
        example.setDataset(format);
        example.setClassValue(component);
        return example;
    }

    private int[] getNumberOfInstancesForCluster(double classBalanceRate, Random random) {
        int[] instancesPerClusterArray = new int[getNumClasses()];
        int equalSamplesPerComponentValue = getNumExamplesAct() / getNumClasses();

        int desbalacePortion = (int) (equalSamplesPerComponentValue * (1.0 - classBalanceRate));

        for (int i = 0; i < instancesPerClusterArray.length; i++) {
            instancesPerClusterArray[i] = equalSamplesPerComponentValue - desbalacePortion;
        }
        if (classBalanceRate > 0 && classBalanceRate < 1) {
            while (isEqualArraySize(instancesPerClusterArray)) {
                for (int k = 0; k < instancesPerClusterArray.length; k++) {
                    int uniformNumFromInterval = getNumFromInterval(0, instancesPerClusterArray.length - 1, random);
                    instancesPerClusterArray[uniformNumFromInterval] += desbalacePortion;
                }
            }
        }

        return instancesPerClusterArray;
    }

    /**
     * Initializes the format for the dataset produced.
     *
     * @return the output data format
     * @throws Exception data format could not be defined
     */
    @Override
    public Instances defineDataFormat() throws Exception {
        Instances dataset;
        Random random = new Random(getSeed());
        setRandom(random);

        // number of examples is the same as given per option
        setNumExamplesAct(getNumExamples());

        if (inputFormat != null) {
            dataset = inputFormat;
        } else {
            // define dataset
            dataset = defineDataset(random);
        }

        return dataset;
    }

    /**
     * Returns a dataset header.
     *
     * @param random random number generator
     * @return dataset header
     * @throws Exception if something goes wrong
     */
    private Instances defineDataset(Random random) throws Exception {
        numOfRelevantAndIrrelevantAttributes = m_NumOfNumericRelevantAttributes + m_NumOfNominalRelevantAttributes + m_NumOfNumericIrrelevantAttributes + m_NumOfNominalIrrelevantAttributes;
        Instances dataset;
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        ArrayList<String> classValues = new ArrayList<String>(getNumClasses());

        HashMap<String, ArrayList<String>> attributeHeaders = getAttributeHeaders(random);
        Iterator<Map.Entry<String, ArrayList<String>>> iterator = attributeHeaders.entrySet().iterator();
        Attribute attribute;
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<String>> next = iterator.next();

            if (next.getValue() == null) {//Numeric attribute
                attribute = new Attribute(next.getKey());
            } else { //Nominal attribute
                attribute = new Attribute(next.getKey(), next.getValue());
            }
            attributes.add(attribute);
        }

        for (int i = 0; i < getNumClasses(); i++) {
            classValues.add("c" + i);
        }
        attribute = new Attribute("class", classValues);
        attributes.add(attribute);

        dataset = new Instances(getRelationNameToUse(), attributes,
                getNumExamplesAct());
        dataset.setClassIndex(numOfRelevantAndIrrelevantAttributes);
        //System.out.println(dataset.toString());
        // set dataset format of this class
        Instances format = new Instances(dataset, 0);
        setDatasetFormat(format);

        return dataset;
    }

    private HashMap<String, ArrayList<String>> getAttributeHeaders(Random ran) throws IOException {
        HashMap<String, ArrayList<String>> attMap = new LinkedHashMap<String, ArrayList<String>>();
        int maxNumOfValuesForNominalAttributes = getMaxNumOfValuesForNominalAttributes();

        //RELEVANT FEATURES
        //Getting numerical relevant attributes
        String[] numericRelevanAtt = AttributeType.NUMERIC_RELEVANT.getAttributesHeads(m_NumOfNumericRelevantAttributes);
        for (String numericRelevanAtt1 : numericRelevanAtt) {
            attMap.put(numericRelevanAtt1, null);
        }

        //Getting nominal relevant attributes
        HashMap<String, ArrayList<String>> nominalRelevantAtt = AttributeType.NOMINAL_RELEVANT.getAttributesHeads(m_NumOfNominalRelevantAttributes, maxNumOfValuesForNominalAttributes, ran);
        Iterator<Map.Entry<String, ArrayList<String>>> iterator = nominalRelevantAtt.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<String>> next = iterator.next();
            attMap.put(next.getKey(), next.getValue());
        }

        //IRRELEVANT FEATURES
        //Getting numerical irrelevant attributes
        String[] numericIrrelevantAtt = AttributeType.NUMERIC_IRRELEVANT.getAttributesHeads(m_NumOfNumericIrrelevantAttributes);
        for (String numericIrrelevantAtt1 : numericIrrelevantAtt) {
            attMap.put(numericIrrelevantAtt1, null);
        }

        //Getting nominal irrelevant attributes
        HashMap<String, ArrayList<String>> nominalIrrelevantAtt = AttributeType.NOMINAL_IRRELEVANT.getAttributesHeads(m_NumOfNominalIrrelevantAttributes, maxNumOfValuesForNominalAttributes, ran);
        Iterator<Map.Entry<String, ArrayList<String>>> iterator1 = nominalIrrelevantAtt.entrySet().iterator();
        while (iterator1.hasNext()) {
            Map.Entry<String, ArrayList<String>> next = iterator1.next();
            attMap.put(next.getKey(), next.getValue());
        }

        return attMap;
    }

    private void addRedundantAttributes(Instances format, AttributeType attType, int numOfRedundantToAdd) throws Exception {
        HashMap<Attribute, Double[]> redundantAttributes = null;

        switch (attType) {
            case NUMERIC_REDUNDANT:
                m_redundantNumericalGen.setRandom(getRandom());
                m_redundantNumericalGen.setNumOfRedundantAtt(numOfRedundantToAdd);

                if (inputFormat != null) {
                    m_redundantNumericalGen.setIsSyntheticData(false);
                }

                m_redundantNumericalGen.buildGenerator(format);
                redundantAttributes = m_redundantNumericalGen.getRedundantAttributes();
                break;
            case NOMINAL_REDUNDANT:
                m_redundantNominalGen.setRandom(getRandom());
                m_redundantNominalGen.setNumOfRedundantAtt(numOfRedundantToAdd);
                
                if (inputFormat != null) {
                    m_redundantNominalGen.setIsSyntheticData(false);
                }
                m_redundantNominalGen.buildGenerator(format);
                redundantAttributes = m_redundantNominalGen.getRedundantAttributes();
                break;
            default:
                break;
        }

        if (redundantAttributes != null) {

            Iterator<Map.Entry<Attribute, Double[]>> it = redundantAttributes.entrySet().iterator();
            Attribute attribute;
            Double[] values;

            while (it.hasNext()) {
                Map.Entry<Attribute, Double[]> next = it.next();
                attribute = next.getKey();
                String attName = attribute.name();
                values = next.getValue();

                //Adding the new attribute to dataset
                format.insertAttributeAt(attribute, format.classIndex());

                Instance instance;
                for (int i = 0; i < format.numInstances(); i++) {
                    instance = format.instance(i);
                    instance.setValue(format.attribute(attName), values[i]);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        MixedDataGenerator mixedDataGenerator = new MixedDataGenerator();
        mixedDataGenerator.setSeed(2);
        mixedDataGenerator.setNumClasses(5);
        mixedDataGenerator.defineDataFormat();
        Instances instances = mixedDataGenerator.generateExamples();

        System.out.println(instances.toString());
    }

}
