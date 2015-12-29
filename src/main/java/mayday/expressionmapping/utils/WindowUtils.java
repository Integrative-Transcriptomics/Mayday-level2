package mayday.expressionmapping.utils;

import java.awt.*; 

 

/**
 * @author Stephan Gade
 * 
 */


public class WindowUtils 
{ 
  
    //from "Java ist auch eine Insel""
    public static void addComponent( Container pane, 
                            GridBagLayout gbl, 
                            Component c, 
                            int x, int y, 
                            int width, int height, 
                            double weightx, double weighty ) 
    { 
	GridBagConstraints gbc = new GridBagConstraints(); 
	gbc.fill = GridBagConstraints.BOTH; 
	gbc.gridx = x; gbc.gridy = y; 
	gbc.gridwidth = width; gbc.gridheight = height; 
	gbc.weightx = weightx; gbc.weighty = weighty; 
	gbl.setConstraints( c, gbc ); 
	pane.add( c ); 
    } 


}