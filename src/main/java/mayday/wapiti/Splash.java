package mayday.wapiti;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;

public class Splash {
	
	@SuppressWarnings("deprecation")
	public static void show() {
		final JWindow jwin = new JWindow();
		
		JPanel content = (JPanel)(jwin.getContentPane());
		content.setLayout(new BorderLayout());
		content.setBackground(Color.white);
		content.setBorder(BorderFactory.createLineBorder(Color.black, 5));
	    
	    int l_height = 360;
	    int l_width = 360;
	    
	    // compute window coordinates (center window on screen)
    	jwin.setSize(l_width, l_height);
        MaydayDefaults.centerWindowOnScreen(jwin);

        JLabel jlabel = new JLabel();
        jlabel.setIcon(PluginInfo.getIcon("mayday/seasight/logo.png", 320, 320));
        jlabel.setVerticalAlignment( JLabel.TOP );
        jlabel.setHorizontalAlignment( JLabel.CENTER);
        jlabel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

    	content.add( jlabel, BorderLayout.CENTER );
    	jwin.setVisible(true);
    	jwin.setAlwaysOnTop(true);
    	
    	final Timer t = new Timer("SeaSight splash visibility");
    	t.schedule(new TimerTask() {
			@Override
			public void run() {
				jwin.dispose();
				t.cancel();
			}
		}, 3000);
	}

}
