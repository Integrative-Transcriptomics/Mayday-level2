package mayday.wapiti.experiments.impl.legacy;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.wapiti.experiments.properties.PropertyParticle;
import mayday.wapiti.experiments.properties.datamode.AbstractDataMode;
import mayday.wapiti.experiments.properties.datamode.Log10;
import mayday.wapiti.experiments.properties.datamode.Log2;
import mayday.wapiti.experiments.properties.datamode.Unlogged;
import mayday.wapiti.experiments.properties.processing.AbstractProcessingStep;
import mayday.wapiti.experiments.properties.processing.BackgroundCorrected;
import mayday.wapiti.experiments.properties.processing.Normalized;
import mayday.wapiti.experiments.properties.processing.Raw;
import mayday.wapiti.experiments.properties.valuetype.AbsoluteExpression;
import mayday.wapiti.experiments.properties.valuetype.AbstractValueType;
import mayday.wapiti.experiments.properties.valuetype.RelativeExpression;

public class DataModeSetting extends HierarchicalSetting {
	
	protected ObjectSelectionSetting< AbstractDataMode > dataMode;
	protected ObjectSelectionSetting< AbstractValueType > valueType;
	protected ObjectSelectionSetting< AbstractProcessingStep > processingStep;
	
	protected static final AbstractDataMode[] availModes = new AbstractDataMode[]{
		new Log2(), new Log10(), new Unlogged()
	};
	
	protected static final AbstractValueType[] availTypes = new AbstractValueType[]{
		new AbsoluteExpression(), new RelativeExpression()
	};
	
	protected static final AbstractProcessingStep[] availSteps = new AbstractProcessingStep[]{
		new Raw(), new Normalized(), new BackgroundCorrected() // TODO not very nice because several could be combined
	};
	
	
	public DataModeSetting() {
		super("Data Properties");
		addSetting( dataMode = new ObjectSelectionSetting<AbstractDataMode>( "Data Mode",null,0, availModes ) );
		addSetting( valueType = new ObjectSelectionSetting<AbstractValueType>( "Data Type",null,0, availTypes ) );
		addSetting( processingStep = new ObjectSelectionSetting<AbstractProcessingStep>( "Processing state",null,0, availSteps ) );
	}
	
	public PropertyParticle[] getStateParticles() {
		return new PropertyParticle[]{
				dataMode.getObjectValue(),
				valueType.getObjectValue(),
				processingStep.getObjectValue()
		};
	}
	
	public DataModeSetting clone() {
		return (DataModeSetting)reflectiveClone();
	}

}
