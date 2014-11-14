package it.genomering.render;

import it.genomering.gui.GenomeRingPanel;

import java.util.Timer;
import java.util.TimerTask;

public class DashMover {

	protected static int i=0;
	public static GenomeRingPanel grp;
	
	protected static Timer t; 
	
	public static boolean isActive() {
		return t!=null;
	}
	
	public static void start(long millis, final long duration) {
		if (t!=null)
			t.cancel();
		t = new Timer("Dash animator", true);
		i=(int)(2*duration);
		t.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if (i>0) 
					i--;
				grp.repaint();
				if (i<=0)
					stop();
			}
		}, 0l, millis);
	}
	
	public static void stop() {
		t.cancel();
		t=null;
		grp.repaint();
	}

	public static int getPosition() {		
		return i;
	}
	
}
