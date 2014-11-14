package mayday.vis3d.plots.radialprofileplot;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.IntSetting;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class AnimatorSetting extends HierarchicalSetting {

	protected IntSetting animationSpeed;
	
	/**
	 * 
	 */
	public AnimatorSetting() {
		super("Animator");
		
		addSetting(animationSpeed = new IntSetting("rotation speed", "Set the rotation speed around the pivot axis", 0, 0, 100, true, true).setLayoutStyle(IntSetting.LayoutStyle.SLIDER));
		setLayoutStyle(HierarchicalSetting.LayoutStyle.TREE);
	}
	
	/**
	 * @return rotation angle for each step
	 */
	public double getRotationSpeed() {
		return this.animationSpeed.getIntValue() / 10.0d;
	}
	
	/**
	 * @return true, if scene should be animated, else false
	 */
	public boolean animateScene() {
		return this.animationSpeed.getIntValue() > 0;
	}
	
	public AnimatorSetting clone() {
		AnimatorSetting as = new AnimatorSetting();
		as.fromPrefNode(this.toPrefNode());
		return as;
	}
}
