
package solorio.dataGenerators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import solorio.utils.DataGeneratorUtils;

/**
 *
 * @author SSF
 */
public enum AttributeType {

    NUMERIC_RELEVANT, NUMERIC_IRRELEVANT, NUMERIC_REDUNDANT, NOMINAL_RELEVANT, NOMINAL_IRRELEVANT, NOMINAL_REDUNDANT;

    public String getAttributeName() throws IOException {
        String attributeName;
        switch (this) {
            case NUMERIC_RELEVANT:
                attributeName = "Relevant_Numeric_Attr_";
                break;
            case NUMERIC_IRRELEVANT:
                attributeName = "Irrelevant_Numeric_Attr_";
                break;
            case NUMERIC_REDUNDANT:
                attributeName = "Redundant_Numeric_Attr_";
                break;
            case NOMINAL_RELEVANT:
                attributeName = "Relevant_Nominal_Attr_";
                break;
            case NOMINAL_IRRELEVANT:
                attributeName = "Irrelevant_Nominal_Attr_";
                break;
            case NOMINAL_REDUNDANT:
                attributeName = "Redundant_Nominal_Attr_";
                break;
            default:
                throw new IOException("The type of feature is wrong!");
        }
        return attributeName;
    }

    public String[] getAttributesHeads(int num) throws IOException {

        String[] numericAttributes = new String[num];
        String attributeName = this.getAttributeName();

        for (int i = 0; i < numericAttributes.length; i++) {
            numericAttributes[i] = attributeName + i;
        }
        return numericAttributes;
    }

    public HashMap<String, ArrayList<String>> getAttributesHeads(int num, int getMaxNumOfValuesForNominalAttributes, Random ran) throws IOException {

        String attributeName = this.getAttributeName();
        HashMap<String, ArrayList<String>> nominalMap = new LinkedHashMap<>();
        for (int i = 0; i < num; i++) {
            int numOfValues = DataGeneratorUtils.getNumFromInterval(2, getMaxNumOfValuesForNominalAttributes, ran);
            ArrayList<String> values = new ArrayList<>();
            for (int k = 0; k < numOfValues; k++) {
                values.add(DataGeneratorUtils.getNominalVale(k));
            }

            nominalMap.put(attributeName + i, values);
        }
        return nominalMap;
    }

}
