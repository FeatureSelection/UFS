
package solorio.dataGenerators.distrib;

/**
 *
 * @author SSF
 */

public interface IntegerDist {
	
    int sample();
    double getProb(int n);
    double getLogProb(int n);
}
