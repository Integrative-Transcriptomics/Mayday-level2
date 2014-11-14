/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mayday.expressionmapping.io.reader;

/**
 *
 * @author Stephan Gade <stephan.gade@googlemail.com>
 */
public interface DataSource {

	public int getDim();

	public boolean hasNext();

	public double[] next();

}
