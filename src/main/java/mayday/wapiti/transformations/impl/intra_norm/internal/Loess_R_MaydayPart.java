package mayday.wapiti.transformations.impl.intra_norm.internal;

import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;

public class Loess_R_MaydayPart {

	private HierarchicalSetting mySetting;

	private DoubleSetting Span;
	private IntSetting iterations;
	
	public Loess_R_MaydayPart() {
		Span = new DoubleSetting("Span",null,0.3);
		iterations = new IntSetting("Iterations",null,4);
		mySetting = new HierarchicalSetting("Loess parameters").addSetting(Span).addSetting(iterations);
	}
	
	public Setting getSetting() {
		return mySetting;
	}
	
	
	public AbstractVector performLoess(AbstractVector m, AbstractVector a) {
		
		double[][] ret = Loess_R_Rpart.loessFit(a.toArray(), m.toArray(), Span.getDoubleValue(), iterations.getIntValue());
		DoubleVector residuals = new DoubleVector(ret[1]);		
		return residuals;
		
	}
	
}