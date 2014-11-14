package mayday.tiala.multi.gui.views;

import java.awt.Component;

import javax.swing.JTabbedPane;

/**
 * 
 * @author jaeger
 *
 */
public class StatisticsViewPane extends JTabbedPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 191358522154679124L;

	public Component add(Component c) {
		return this.add("", c);
	}
}
