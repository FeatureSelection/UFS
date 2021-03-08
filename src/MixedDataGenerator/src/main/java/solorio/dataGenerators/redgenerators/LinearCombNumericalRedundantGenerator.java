
package solorio.dataGenerators.redgenerators;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.ProtectedProperties;
import weka.core.RevisionUtils;
import static solorio.utils.DataGeneratorUtils.*;

/**
 *
 * @author SSF
 */
public class LinearCombNumericalRedundantGenerator extends RedundantGenerator implements OptionHandler {


    private int m_maxNumberOfAttributesForLinearCombination = 1;
    private double m_supLimForCoefValues = 20;
    private double m_infLimFOrCoefValues = -20;

    public LinearCombNumericalRedundantGenerator() {
        super();
    }

    public int getMaxNumberOfAttributesForLinearCombination() {
        return m_maxNumberOfAttributesForLinearCombination;
    }

    public void setMaxNumberOfAttributesForLinearCombination(int maxNumberOfAttributesForLinearCombination) {
        this.m_maxNumberOfAttributesForLinearCombination = maxNumberOfAttributesForLinearCombination;
    }

    public double getSupLim() {
        return m_supLimForCoefValues;
    }

    public void setSupLim(double supLim) {
        this.m_supLimForCoefValues = supLim;
    }

    public double getInfLim() {
        return m_infLimFOrCoefValues;
    }

    public void setInfLim(double infLim) {
        this.m_infLimFOrCoefValues = infLim;
    }

    @Override
    public String globalInfo() {
        return "Attribute Linear combination redundant generator";
    }

    @Override
    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1 $");
    }

    @Override
    public void setParams(String[][] params) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void buildGenerator(Instances data) throws Exception {
        initVars(data);
        Instances inputInstances = getInputInstances();
        HashMap<Attribute, Double[]> redundantAttToAdd = new HashMap<Attribute, Double[]>();

        Attribute[] numericRelevantAttributes;
        if (isIsSyntheticData()) {
            numericRelevantAttributes = getAttributesFromInstances(inputInstances, "Relevant", "Numeric");
        } else {
            numericRelevantAttributes = getAttributesFromInstances(inputInstances, "", "Numeric");
        }

        if (numericRelevantAttributes == null) {
            throw new Exception("There are not numerical relevant attributes");
        }

        if (m_maxNumberOfAttributesForLinearCombination > numericRelevantAttributes.length) {
            throw new Exception("The max number of attributes for combining is grater than the number of numerical relevant attributes! ");
        }

        Attribute newRedundantAttribute;
        Double[] redundantAttributeValues = null;
        //Getting names and values of new attributes
        for (int i = 0; i < getNumOfRedundantAtt(); i++) {

            newRedundantAttribute = getNewRedundantAttribute(numericRelevantAttributes, i);
            redundantAttributeValues = getAttributeInstanceValues(inputInstances, newRedundantAttribute, random);
            redundantAttToAdd.put(newRedundantAttribute, redundantAttributeValues);
        }
        setRedundantAtt(redundantAttToAdd);
    }

    private Attribute getNewRedundantAttribute(Attribute[] relevantAttribute, int number) throws IOException {
        int numOfAttributesForCombination = getNumFromInterval(1, m_maxNumberOfAttributesForLinearCombination, random);
        double[] linearCombCoef = getLinearCombCoef(numOfAttributesForCombination, m_supLimForCoefValues, m_infLimFOrCoefValues, random);
        StringBuilder buffer = new StringBuilder();

        Properties props = new Properties();
        //buffer.append(AttributeType.NUMERIC_REDUNDANT.getAttributeName()).append("=(");
        buffer.append("Red-Num_");
        buffer.append(number).append(" = ");
        String[] featureNames = getFeatureNames(relevantAttribute);
        int featureIndex;
        //int linearCombCoefIndex;
        for (int k = 0; k < numOfAttributesForCombination; k++) {
            featureIndex = getNumFromInterval(0, featureNames.length - 1, random);
            //linearCombCoefIndex = getNumFromInterval(0, linearCombCoef.length-1, random);
            buffer.append(linearCombCoef[k]).append("*").append(featureNames[featureIndex]).append("+");
            props.put(featureNames[featureIndex], String.valueOf(linearCombCoef[k]));
        }
        buffer.append("gaussian noise");
        ProtectedProperties metadata = new ProtectedProperties(props);
        Attribute newRedundantAttribute = new Attribute(buffer.toString(), metadata);
        return newRedundantAttribute;
    }

    private Double[] getAttributeInstanceValues(Instances inputInstances, Attribute newRedundantAttribute, Random random) {
        int numInstances = inputInstances.numInstances();
        Double[] attValues = new Double[numInstances];

        double value;
        ProtectedProperties metadata;
        for (int i = 0; i < numInstances; i++) {
            value = 0;
            metadata = newRedundantAttribute.getMetadata();
            Iterator<Map.Entry<Object, Object>> iterator = metadata.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Object, Object> next = iterator.next();
                value += inputInstances.instance(i).value(inputInstances.attribute((String) next.getKey())) * Double.parseDouble((String) next.getValue());
            }
            value += random.nextGaussian();
            attValues[i] = value;
        }
        return attValues;
    }

    private String[] getFeatureNames(Attribute[] relevantAttribute) {
        String[] names = new String[relevantAttribute.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = relevantAttribute[i].name();
        }
        return names;
    }
}
