
package solorio.dataGenerators.redgenerators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.filters.Filter;
import static solorio.utils.DataGeneratorUtils.*;

/**
 *
 * @author SSF
 */
public class MixedNominalRedundantGenerator extends RedundantGenerator implements OptionHandler {

    private int m_percentOfRedundantFromNumerical;

    private RedundantGenerator m_nmrg;
    private final weka.filters.unsupervised.attribute.Discretize unsupervisedDiscretizer;

    public MixedNominalRedundantGenerator() {
        super();
        this.m_percentOfRedundantFromNumerical = defaultPercentOfRedundantFromNumerical();
        this.m_nmrg = new NonMatchNominalRedundantGenerator();
        this.unsupervisedDiscretizer = new weka.filters.unsupervised.attribute.Discretize();
    }

    public int getPercentOfRedundantFromNumerical() {
        return m_percentOfRedundantFromNumerical;
    }

    public void setPercentOfRedundantFromNumerical(int m_percentOfRedundantFromNumerical) {
        this.m_percentOfRedundantFromNumerical = m_percentOfRedundantFromNumerical;
    }

    private int defaultPercentOfRedundantFromNumerical() {
        return 50;

    }

    public RedundantGenerator getnmrg() {
        return m_nmrg;
    }

    public void setnmrg(RedundantGenerator m_nmrg) {
        this.m_nmrg = m_nmrg;
    }

    @Override
    public String globalInfo() {
        return "Numerical Discretizer Nominal redundant generator";
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

        int numberOffeaturesGeneratedFromNumerical;
        int numberOffeaturesGeneratedFromNominal;
        if (checkAttributeType(inputInstances, Attribute.NOMINAL) && !checkAttributeType(inputInstances, Attribute.NUMERIC)) {
            numberOffeaturesGeneratedFromNominal = getNumOfRedundantAtt();
            numberOffeaturesGeneratedFromNumerical = 0;
        } else if (!checkAttributeType(inputInstances, Attribute.NOMINAL) && checkAttributeType(inputInstances, Attribute.NUMERIC)) {
            numberOffeaturesGeneratedFromNominal = 0;
            numberOffeaturesGeneratedFromNumerical = getNumOfRedundantAtt();

        } else {
            numberOffeaturesGeneratedFromNumerical = getNumOfRedundantAtt() * m_percentOfRedundantFromNumerical / 100;
            numberOffeaturesGeneratedFromNominal = getNumOfRedundantAtt() - numberOffeaturesGeneratedFromNumerical;
        }

        if (numberOffeaturesGeneratedFromNominal > 0) {      //Redundant features from nominal
            m_nmrg.setRandom(getRandom());
            m_nmrg.setNumOfRedundantAtt(numberOffeaturesGeneratedFromNominal);
            m_nmrg.setIsSyntheticData(this.isIsSyntheticData());
            m_nmrg.buildGenerator(data);

            redundantAttToAdd = m_nmrg.getRedundantAttributes();
        }
        if (numberOffeaturesGeneratedFromNumerical > 0) {         //Redundant features from numerical

            String[] numericRelevantAttributesNames;

            if (isIsSyntheticData()) {
                numericRelevantAttributesNames = getAttributesNamesFromInstances(inputInstances, "Relevant", "Numeric");

            } else {
                numericRelevantAttributesNames = getAttributesNamesFromInstances(inputInstances, "", "Numeric");
            }

            if (numericRelevantAttributesNames == null) {
                throw new Exception("There are not numerical relevant attributes for generating redundancy");
            }

            unsupervisedDiscretizer.setInputFormat(inputInstances);
            Instances discretizedInstances = Filter.useFilter(inputInstances, unsupervisedDiscretizer);

            Attribute newRedundantAttribute;
            Double[] newRedundantAttributeValues;
            List<String> attNameArray = new ArrayList<String>();
            for (int i = 0; i < numberOffeaturesGeneratedFromNumerical; i++) {
                String relevantAttribute = getRelevantAttribute(numericRelevantAttributesNames, random);
                if (isInList(attNameArray, relevantAttribute)) {
                    unsupervisedDiscretizer.setInputFormat(inputInstances);
                    unsupervisedDiscretizer.setBins(4 + i);
                    discretizedInstances = Filter.useFilter(inputInstances, unsupervisedDiscretizer);
                }
                newRedundantAttribute = getNewRedundantAttribute(relevantAttribute, getAttributeValues(discretizedInstances.attribute(relevantAttribute)), i + numberOffeaturesGeneratedFromNominal);
                newRedundantAttributeValues = convertToDouble(discretizedInstances.attributeToDoubleArray(discretizedInstances.attribute(relevantAttribute).index()));

                redundantAttToAdd.put(newRedundantAttribute, newRedundantAttributeValues);
                attNameArray.add(relevantAttribute);
            }
        }
        setRedundantAtt(redundantAttToAdd);

    }

    private boolean isInList(List<String> list, String value) {
        if (list.isEmpty()) {
            return false;
        }
        for (String array1 : list) {
            if (value.equals(array1)) {
                return true;
            }
        }
        return false;
    }

    private Attribute getNewRedundantAttribute(String attributeName, List<String> values, int number) throws IOException {
        //String name = AttributeType.NOMINAL_REDUNDANT.getAttributeName() + "=(" + nominalRelevantAttribute.name() + "-With non Match rate of " + nonMatchRate + ")";
        String name = "Red-Nom_" + number + " = " + attributeName + "-(Discretized-bins-" + unsupervisedDiscretizer.getBins() + ")";
        Attribute newRedundantAttribute = new Attribute(name, values);
        return newRedundantAttribute;
    }

    private String getRelevantAttribute(String[] nominalRelevantAttributes, Random random) {
        int numFromInterval = getNumFromInterval(0, nominalRelevantAttributes.length - 1, random);
        return nominalRelevantAttributes[numFromInterval];
    }

}
