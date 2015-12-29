package mayday.wapiti.experiments.properties.channels;

import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.PropertyParticle;

public class ChannelCount extends PropertyParticle {

	protected int numChannels;
	protected String[] channelNames;
	
	public ChannelCount(int numChannels, String[] channelNames) {
		this.numChannels = numChannels;
		this.channelNames = channelNames!=null?channelNames:new String[numChannels];
	}
	
	@Override
	protected boolean compatibleWith(PropertyParticle other) {
		// by default, all channel counts are incompatible with each other
		return (!(other instanceof ChannelCount));
	}

	@Override
	public String toString() {
		if (numChannels==1)
			return "1 channel";
		else 
			return numChannels+" channels";
	}
	
	public int getCount() {
		return numChannels;
	}
	
	public String[] getNames() {
		return channelNames;
	}

	public static boolean isMultiChannel(DataProperties p) {
		ChannelCount c = (ChannelCount)p.getType(ChannelCount.class);
		return (c!=null && c.getCount()>1);
	}
	
	public static int getChannelCount(DataProperties p) {
		ChannelCount c = (ChannelCount)p.getType(ChannelCount.class);
		if (c==null)
			return 1;
		return c.getCount();
	}
	
}
