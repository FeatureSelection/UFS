
package solorio.gui.wekaBeans;

/**
 * Interface to something that has a visible (via BeanVisual) reprentation
 *
 * @author <a href="mailto:mhall@cs.waikato.ac.nz">Mark Hall</a>
 * @version $Revision: 8034 $
 * @since 1.0
 */
public interface Visible {

  /**
   * Use the default visual representation
   */
  void useDefaultVisual();

  /**
   * Set a new visual representation
   *
   * @param newVisual a <code>BeanVisual</code> value
   */
  void setVisual(BeanVisual2 newVisual);

  /**
   * Get the visual representation
   *
   * @return a <code>BeanVisual</code> value
   */
  BeanVisual2 getVisual();
}
