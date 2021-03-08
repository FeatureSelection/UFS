
package solorio.dataGenerators.redgenerators;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import static solorio.utils.DataGeneratorUtils.*;

/**
 *
 * @author SSF
 */
public final class NonMatchNominalRedundantGenerator extends RedundantGenerator implements OptionHandler {

    private double[] nonMatchRateArray;
    private double m_InfLimOfNonMatchRate;
    private double m_SupLimOfNonMatchRate;

    public NonMatchNominalRedundantGenerator() {
        super();
        m_InfLimOfNonMatchRate = defaultInfNonMatchRate();
        m_SupLimOfNonMatchRate = defaultSupNonMatchRate();
    }

    public double getInfLimOfNonMatchRate() {
        return m_InfLimOfNonMatchRate;
    }

    public void setInfLimOfNonMatchRate(double m_infLimOfNonMatchRate) throws IOException {
        if (m_infLimOfNonMatchRate < 0 || m_infLimOfNonMatchRate > 1) {
            throw new IOException("InfLimOfNonMatchRate  out of range!");
        }
        if (m_infLimOfNonMatchRate > m_SupLimOfNonMatchRate) {
            throw new IOException("InfLimOfNonMatchRate is grater than m_SupLimOfNonMatchRate!");
        }
        this.m_InfLimOfNonMatchRate = m_infLimOfNonMatchRate;
    }

    public double getSupLimOfNonMatchRate() {
        return m_SupLimOfNonMatchRate;
    }

    public void setSupLimOfNonMatchRate(double m_supLimOfNonMatchRate) throws IOException {
        if (m_supLimOfNonMatchRate < 0 || m_supLimOfNonMatchRate > 1) {
            throw new IOException("InfLimOfNonMatchRate  out of range!");
        }
        if (m_supLimOfNonMatchRate < m_InfLimOfNonMatchRate) {
            throw new IOException("m_SupLimOfNonMatchRate is lower than InfLimOfNonMatchRate !");
        }
        this.m_SupLimOfNonMatchRate = m_supLimOfNonMatchRate;
    }

    protected double defaultInfNonMatchRate() {
        return (1.0 / 16.0);
    }

    protected double defaultSupNonMatchRate() {
        return (1.0 / 10.0);
    }

    @Override
    public String globalInfo() {
        return "Non-Match nominal redundant generator v1";
    }

    @Override
    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1 $");
    }

    @Override
    public void setParams(String[][] params) throws Exception {

        try {
//            m_neighborhoodRatio = Double.parseDouble(params[0]);

        } catch (Exception e) {
        }
    }

    @Override
    public void buildGenerator(Instances data) throws Exception {
        initVars(data);
        setNonMatchRateArray();
        Instances inputInstances = getInputInstances();
        HashMap<Attribute, Double[]> redundantAttToAdd = new HashMap<Attribute, Double[]>();

         Attribute[] nominalRelevantAttributes;
        if (isIsSyntheticData()) {
            nominalRelevantAttributes = getAttributesFromInstances(inputInstances, "Relevant", "Nominal");
        } else {
            nominalRelevantAttributes = getAttributesFromInstances(inputInstances, "", "Nominal");
        }

        if (nominalRelevantAttributes == null) {
            throw new Exception("There is not relevant nominal attributes for generating redundancy!!");
        }

        Attribute newRedundantAttribute;
        Double[] newRedundantAttributeValues;
        //Getting names and values of new attributes
        for (int i = 0; i < getNumOfRedundantAtt(); i++) {
            Attribute relevantAttribute = getRelevantAttribute(nominalRelevantAttributes, random);

            newRedundantAttribute = getNewRedundantAttribute(relevantAttribute, nonMatchRateArray[i], i);
            newRedundantAttributeValues = getAttributeInstanceValues(inputInstances.attributeToDoubleArray(relevantAttribute.index()), newRedundantAttribute, nonMatchRateArray[i], random);

            redundantAttToAdd.put(newRedundantAttribute, newRedundantAttributeValues);
        }
        setRedundantAtt(redundantAttToAdd);
    }

    private Attribute getNewRedundantAttribute(Attribute nominalRelevantAttribute, double nonMatchRate, int number) throws IOException {
        //String name = AttributeType.NOMINAL_REDUNDANT.getAttributeName() + "=(" + nominalRelevantAttribute.name() + "-With non Match rate of " + nonMatchRate + ")";
        String name = "Red-Nom_" + number + " = " + nominalRelevantAttribute.name() + "-(Non Match rate " + String.valueOf(nonMatchRate) + ")";
        Attribute newRedundantAttribute = new Attribute(name, getAttributeValues(nominalRelevantAttribute));
        return newRedundantAttribute;
    }

    private Attribute getRelevantAttribute(Attribute[] nominalRelevantAttributes, Random random) {
        int numFromInterval = getNumFromInterval(0, nominalRelevantAttributes.length - 1, random);
        return nominalRelevantAttributes[numFromInterval];
    }

    private Double[] getAttributeInstanceValues(double[] attributeValues, Attribute newRedundantAttribute, double nonMatchRate, Random random) throws IOException {
        double percent = nonMatchRate * 100.0;
        Double[] values;

        int numForChange = getNumOf(attributeValues.length, (int) percent);
        values = copyDoubleValues(attributeValues);

        Double newValue;
        for (int i = 0; i < numForChange; i++) {
            int index = getNumFromInterval(0, attributeValues.length - 1, random);
            newValue = getNewValue(attributeValues[index], newRedundantAttribute.numValues(), random);
            values[index] = newValue;
        }
        return values;
    }

    private Double getNewValue(double attributeValue, int numAllowedValues, Random random) {
        Double newValue;
        do {
            newValue = 1.0 * getNumFromInterval(0, numAllowedValues - 1, random);
        } while (newValue == attributeValue);

        return newValue;
    }

    private Double[] copyDoubleValues(double[] attributeValues) {
        Double[] values = new Double[attributeValues.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = attributeValues[i];
        }
        return values;
    }

    private void setNonMatchRateArray() {
        int numOfRedundantAttToAdd = getNumOfRedundantAtt();
        nonMatchRateArray = new double[numOfRedundantAttToAdd];
        for (int i = 0; i < numOfRedundantAttToAdd; i++) {
            nonMatchRateArray[i] = (getNumFromInterval(m_InfLimOfNonMatchRate * 10, m_SupLimOfNonMatchRate * 10, random) / 10);
        }
    }
}
