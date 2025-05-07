/**
 * IFilter.java
 * 
 * Time-stamp: <2017-03-10 11:22:26 ftaiani>
 * @author Francois Taiani   <francois.taiani@irisa.fr>
 * $Id$
 * 
 */

package tp3;

import java.awt.image.*;

/**
 * @author Francois Taiani <francois.taiani@irisa.fr>
 */
public interface IFilter {
	public int getMargin();

	public void applyFilterAtPoint(int x, int y, BufferedImage imgIn, BufferedImage imgOut);
} // EndInterface IFilter
