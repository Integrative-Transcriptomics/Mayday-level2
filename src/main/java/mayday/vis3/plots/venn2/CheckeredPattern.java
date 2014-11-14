package mayday.vis3.plots.venn2;

import java.awt.Color;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class CheckeredPattern extends BufferedImage {
	
	private TexturePaint tp;

	public CheckeredPattern(Color probeList, Color selection) {
		super(4,4, BufferedImage.TYPE_INT_ARGB);
		
		for(int i=0; i!= 4; ++i)
		{
			for(int j=0; j!= 4; ++j)
			{
				if(i<2 && j<2)
					setRGB(i, j, selection.getRGB());
				if(i>=2 && j<2)
					setRGB(i, j, probeList.getRGB());
				if(i<2 && j>=2)
					setRGB(i, j, probeList.getRGB());
				if(i>=2 && j>=2)
					setRGB(i, j, selection.getRGB());
			}
		}
		tp = new TexturePaint(this, new Rectangle2D.Double(0,0,4,4));
	}
	
	public TexturePaint getTexturePaint() {
		return tp; 
	}

}
