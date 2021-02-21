
package solorio.dataGenerators.redgenerators;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.CapabilitiesHandler;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionHandler;
import weka.core.RevisionUtils;
import weka.core.SerializedObject;
import weka.core.Utils;

/**
 *
 * @author SSF
 */
public abstract class RedundantGenerator implements Serializable, OptionHandler, CapabilitiesHandler, RevisionHandler, Cloneable {

    private boolean isSyntheticData = true;
    private Instances m_inputInstances;
    private int numOfRedundantAtt;
    private HashMap<Attribute, Double[]> redundantAtt;
    Random random = null;

    /**
     * random number generator seed
     */
    protected int m_Seed;
    /**
     * enables debugging output
     */
    protected boolean m_Debug = false;

    public RedundantGenerator() {
        setSeed(defaultSeed());
    }

    public boolean isIsSyntheticData() {
        return isSyntheticData;
    }

    public void setIsSyntheticData(boolean isSyntheticData) {
        this.isSyntheticData = isSyntheticData;
    }

    public abstract String globalInfo();

    public abstract void setParams(String[][] params) throws Exception;

    public abstract void buildGenerator(Instances data) throws Exception;

    public HashMap<Attribute, Double[]> getRedundantAttributes() {
        return redundantAtt;
    }

    protected void setRedundantAtt(HashMap<Attribute, Double[]> redundantAtt) {
        this.redundantAtt = redundantAtt;
    }

    /**
     * returns the default seed
     *
     * @return the default seed
     */
    protected final int defaultSeed() {
        return 1;
    }

    //public abstract HashMap<Attribute, Double[]> getRedundantAttributes()  throws Exception;
    @Override
    public Enumeration listOptions() {
        Vector result;
        result = new Vector();
        result.addElement(new Option(
                "\tEnables debugging output (if available) to be printed.\n"
                + "\t(default: off)",
                "D", 0, "-D"));
        return result.elements();
    }

    @Override
    public void setOptions(String[] options) throws Exception {
        setDebug(Utils.getFlag('D', options));

        Utils.checkForRemainingOptions(options);
    }

    @Override
    public String[] getOptions() {
        Vector result;
        result = new Vector();

        if (getDebug()) {
            result.add("-D");
        }
        return (String[]) result.toArray(new String[result.size()]);
    }

    @Override
    public Capabilities getCapabilities() {
        Capabilities result = new Capabilities(this);
        result.enableAll();
        return result;
    }

    @Override
    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1 $");
    }

    public static RedundantGenerator makeCopy(RedundantGenerator absGen) throws Exception {
        return (RedundantGenerator) new SerializedObject(absGen).getObject();
    }

    public static RedundantGenerator forName(String name, String[] options)
            throws Exception {
        return (RedundantGenerator) Utils.forName(RedundantGenerator.class, name, options);
    }

    public boolean getDebug() {
        return m_Debug;
    }

    public void setDebug(boolean m_Debug) {
        this.m_Debug = m_Debug;
    }

    public Instances getInputInstances() {
        return m_inputInstances;
    }

    public void setInputInstances(Instances inputInstances) {
        this.m_inputInstances = inputInstances;
    }

    public Random getRandom() {
        if (random == null) {
            random = new Random(getSeed());
        }
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public int getSeed() {
        return m_Seed;
    }

    public final void setSeed(int newSeed) {
        this.m_Seed = newSeed;
        random = new Random(newSeed);
    }

    public int getNumOfRedundantAtt() {
        return numOfRedundantAtt;
    }

    public void setNumOfRedundantAtt(int numOfRedundantAtt) {
        this.numOfRedundantAtt = numOfRedundantAtt;
    }

    /**
     * initializes variables etc.
     *
     * @param data	the data to use
     */
    protected void initVars(Instances data) {
        Instances copyOfInstances = new Instances(data);
        setInputInstances(copyOfInstances);
    }

}
