package mayday.expressionmapping.view;

import java.awt.Dimension;
import javax.swing.JComponent;

public interface WindowComponent {
	
	public JComponent getComponent();
        
        public void setSize(int width, int height);
        
        public void setPreferredSize(Dimension size);
            
   
}
