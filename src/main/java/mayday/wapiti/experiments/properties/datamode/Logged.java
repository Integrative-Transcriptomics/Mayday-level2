package mayday.wapiti.experiments.properties.datamode;

import mayday.wapiti.experiments.properties.DataProperties;

public class Logged extends AbstractDataMode {

	protected double base;
	
	public Logged(double base) {
		this.base = base;
	}
	
	@Override
	public String toString() {
		if ((base-Math.round(base))==0)
			return "log "+(int)base;
		return "log "+base;
	}
	
	public double getLogBase() {
		return base;
	}

	public static boolean isLogged(DataProperties p) {
		Logged c = (Logged)p.getType(Logged.class);
		return (c!=null);
	}
	
	public static boolean isLogN(DataProperties p, double base) {
		Logged c = (Logged)p.getType(Logged.class);
		return (c!=null && c.getLogBase()==base);
	}
	
}
