package mayday.vis3.plots.tagcloud;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.vis3.graph.actions.RoleAction;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.dialog.ComponentZoomFrame;
import mayday.vis3.graph.renderer.ComponentRenderer;

@SuppressWarnings("serial")
public class TagComponent extends MultiProbeComponent
{
	private static TagRenderer defaultRenderer=new TagRenderer();
	private Map<String, ComponentRenderer> rendererMap=new HashMap<String, ComponentRenderer>();
	
	
	public TagComponent(DefaultNode node) 
	{
		super((MultiProbeNode)node);
		DefaultNode dn=(DefaultNode)getNode();
		Tag t=new Tag(dn.getPropertyValue(TagConstants.TAG_KEY), Double.parseDouble(dn.getPropertyValue(TagConstants.TAG_FREQUENCY)));
		setToolTipText(t.getTag().toString()+" ("+dn.getPropertyValue(TagConstants.TAG_COUNT)+")");
		
		for(MouseWheelListener l:getMouseWheelListeners())
			removeMouseWheelListener(l);
	}

	@Override
	public void setVisible(boolean flag) 
	{

		super.setVisible(flag);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g1)
	{
		Graphics2D g=(Graphics2D)g1;
		if(getNode().getRole().equals(Nodes.Roles.PROBE_ROLE) || getNode().getRole().equals(Nodes.Roles.PROBES_ROLE))
		{
			getRendererMap().get(getNode().getRole()).draw(g, getNode(), new Rectangle(getSize()), getDisplayProbes(), labelComponent==null?getLabel():"", isSelected());
			return;
		}
		if(getNode().getRole().equals(TagConstants.TAG_ROLE))
			getRendererMap().get(getNode().getRole()).draw(g, getNode(), new Rectangle(getSize()), null,  labelComponent==null?getLabel():"", isSelected());
		else
			super.paint(g1);
	}


	public void setSize(Dimension d)
	{
		if(getNode().getRole().equals(TagConstants.TAG_ROLE))
		{
			DefaultNode dn=(DefaultNode)getNode();
			Tag t=new Tag(dn.getPropertyValue(TagConstants.TAG_KEY), Double.parseDouble(dn.getPropertyValue(TagConstants.TAG_FREQUENCY)));
			Dimension dim= defaultRenderer.getStringBoundingBox(getGraphics(), t);				
			setSize(dim.width,dim.height+10);					
		}
	}
	
	@Override
	public JPopupMenu setCustomMenu(JPopupMenu menu) 
	{
		menu=super.setCustomMenu(menu);
		menu.add(conversionMenu());
		return menu;
	}

	
	protected JMenu conversionMenu() 
	{
		JMenu menu=new JMenu("Display as");
		menu.add(new RoleAction(TagConstants.TAG_ROLE, this));
		menu.add(new RoleAction(Nodes.Roles.NODE_ROLE, this));
		menu.add(new RoleAction(Nodes.Roles.PROBES_ROLE, this));
		return menu;
	}

	public void mousePressed(MouseEvent event)
	{
		//check for popup menu
		if(event.getButton()==MouseEvent.BUTTON2)
		{
			zoom=new ComponentZoomFrame(this,getRendererMap().get(getNode().getRole()));
			zoom.setLocation(event.getXOnScreen(),event.getYOnScreen());
			zoom.setVisible(true);
			event.consume();
			return;
		}
		super.mousePressed(event);
	}
	
	public void setRenderer(Map<String,ComponentRenderer> rendererMap)
	{
		this.rendererMap=rendererMap;
	}

	public Map<String, ComponentRenderer> getRendererMap() {
		return rendererMap;
	}


}
