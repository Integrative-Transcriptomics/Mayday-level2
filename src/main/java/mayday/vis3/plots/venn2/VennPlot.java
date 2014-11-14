package mayday.vis3.plots.venn2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.plugins.probe.ProbeMenu;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.ExtendableObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.vis3.components.AntiAliasPlotPanel;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

@SuppressWarnings("serial")
public class VennPlot extends AntiAliasPlotPanel implements MouseListener, ViewModelListener, SettingChangeListener, ProbeListListener
{
	
	private ViewModel viewModel;
	private double left, right, top, bottom;
	private double radius;
	private double center;
	private ProbeList plA, plB, plC;
	
	private Map<VennSet, VennComponent> components=new TreeMap<VennSet, VennComponent>();

	private BooleanSetting hide2Intersections=new BooleanSetting("Hide 2-set intersections", null, false);
	private BooleanSetting hide3Intersection=new BooleanSetting("Hide 3-set intersection", null, false);
	private BooleanSetting hideUnintersected=new BooleanSetting("Hide unintersected sets", null, false);	
	private BooleanSetting usePercentages=new BooleanSetting("Show percentages", null, false);

	private ExtendableObjectSelectionSetting<ProbeList> probeListA;
	private ExtendableObjectSelectionSetting<ProbeList> probeListB;
	private ExtendableObjectSelectionSetting<ProbeList> probeListC; // may be set to NONE
	
	private static final ProbeList NONE = new ProbeList(null, false);
	static {
		NONE.setName("None (compare only two lists)");
	}

	@Override
	public void setup(PlotContainer plotContainer) 
	{
		if(viewModel==null)
			create(plotContainer);
		
		plotContainer.setPreferredTitle("Venn Diagram", this);
		plotContainer.addViewSetting(probeListA, this);
		plotContainer.addViewSetting(probeListB, this);
		plotContainer.addViewSetting(probeListC, this);		
		plotContainer.addViewSetting(hideUnintersected, this);
		plotContainer.addViewSetting(hide2Intersections, this);
		plotContainer.addViewSetting(hide3Intersection, this);
		plotContainer.addViewSetting(usePercentages, this);
	}
	
	private void create(PlotContainer plotContainer)
	{
		viewModel=plotContainer.getViewModel();		
		List<ProbeList> probeLists= viewModel.getProbeLists(false);

		List<ProbeList> pls=viewModel.getProbeLists(false);
		ProbeList[] plists = pls.toArray(new ProbeList[0]);
		ProbeList[] plists2 = new ProbeList[plists.length+1];
		plists2[0] = NONE;
		System.arraycopy(plists, 0, plists2, 1, plists.length);

		probeListA=new ExtendableObjectSelectionSetting<ProbeList>("ProbeList A",null,0,plists);
		probeListB=new ExtendableObjectSelectionSetting<ProbeList>("ProbeList B",null,0,plists);
		probeListC=new ExtendableObjectSelectionSetting<ProbeList>("ProbeList C",null,0,plists2);

		probeListA.addChangeListener(this);
		probeListB.addChangeListener(this);
		probeListC.addChangeListener(this);
		
		hide2Intersections.addChangeListener(this);
		hide3Intersection.addChangeListener(this);
		hideUnintersected.addChangeListener(this);
		usePercentages.addChangeListener(this);
		
		switch(probeLists.size()) {
		case 1:
			setContents();
			probeListA.setSelectedIndex(0);
			probeListB.setSelectedIndex(0);
			probeListC.setSelectedIndex(0);
			break;
		case 2: 
			setContents(probeLists.get(0), probeLists.get(1));
			probeListA.setSelectedIndex(0);
			probeListB.setSelectedIndex(1);			
			probeListC.setSelectedIndex(0);
			break;
		case 3:
			probeListA.setSelectedIndex(0);
			probeListB.setSelectedIndex(1);
			probeListC.setSelectedIndex(3); // shift by one to make room for NONE
			setContents(probeLists.get(0), probeLists.get(1), probeLists.get(2));			
		}
		
//		ZoomController zoomController=new ZoomController();
//		zoomController.setTarget(this);
//		zoomController.setAllowXOnlyZooming(true);
//		zoomController.setAllowYOnlyZooming(true);
//		zoomController.setActive(true);
		
		viewModel.addViewModelListener(this);
		
		addMouseListener(this);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateTransformation();
				if (viewModel.getProbeLists(false).size()<2)
					setContents();
				else
				if (probeListC.getObjectValue()==NONE) {
					setContents(probeListA.getObjectValue(),probeListB.getObjectValue());
				} else {
					setContents(probeListA.getObjectValue(),probeListB.getObjectValue(),probeListC.getObjectValue());
				}
				updatePlot();
			}
		});
	}

	private void updateTransformation()	
	{
		double texth= getGraphics().getFontMetrics().getAscent()+getGraphics().getFontMetrics().getDescent();
		
		double usableW = getWidth(); // usable for circles excluding text
		double usableH = getHeight(); 
		if (probeListC.getObjectValue()!=NONE)  
			usableH -= 2*texth;
		else
			usableH *= 1.5;
		
		double w = Math.min(usableW, usableH);
		
		int padd = 15;
		// compute sizes for things		
		left = padd;
		right = w-padd;
		top = texth+padd;
		radius = (double)(right-left)/5.0*3.1;
		bottom = top+(right-left);
		
		center = w / 2;
		
		// finally, center plot horizontally and vertically
		double hfree = getWidth()-(right-left);
		if (hfree>0) {
			left+=hfree/2-padd;
			right+=hfree/2-padd;
			center+=hfree/2-padd;
		}
		double vfree = getHeight()-(bottom);
		if (vfree>0) {
			top+=vfree/2-padd;
			bottom+=vfree/2-padd;
		}
	}

	private void setContents(ProbeList... pls) {
		components.clear();
		
		if (plA!=null)
			plA.removeProbeListListener(this);
		if (plB!=null)
			plB.removeProbeListListener(this);
		if (plC!=null)
			plC.removeProbeListListener(this);
		
		plA=null;
		plB=null; 
		plC=null;
		
		if (pls.length>=2) {
			plA = pls[0];
			plB = pls[1];
			plA.addProbeListListener(this);
			plB.addProbeListListener(this);
		}
		if (pls.length>=3) {
			plC = pls[2];
			plC.addProbeListListener(this);
		}
		
		if (plA!=null && plB!=null) { // at least two probelists
			
			// ====== Construct the sets ===========
			
			Set<Probe> A=new HashSet<Probe>(plA.getAllProbes());		
			Set<Probe> B=new HashSet<Probe>(plB.getAllProbes());
			Set<Probe> AB=new HashSet<Probe>(A);
			AB.retainAll(B); 

			// for maybe later use
			Set<Probe> C = null;
			Set<Probe> AC = null;
			Set<Probe> BC = null;
			Set<Probe> ABC = null;
			
			if (plC!=null) { // three probelists
				C=new HashSet<Probe>(plC.getAllProbes());
				AB.removeAll(C);
				AC=new HashSet<Probe>(A);
				AC.retainAll(C);
				AC.removeAll(B);
				BC=new HashSet<Probe>(B);
				BC.retainAll(C);	
				BC.removeAll(A);
				ABC=new HashSet<Probe>(A);
				ABC.retainAll(B);
				ABC.retainAll(C);
				A.removeAll(plC.getAllProbes());
				B.removeAll(plC.getAllProbes());
				C.removeAll(plA.getAllProbes());
				C.removeAll(plB.getAllProbes());
			}

			A.removeAll(plB.getAllProbes());
			B.removeAll(plA.getAllProbes());
			
			// ====== Construct the basic shapes =====
			
			Ellipse2D class1=new Ellipse2D.Double();
			class1.setFrame(left,top,radius,radius);

			Ellipse2D class2=new Ellipse2D.Double();
			class2.setFrame(right-radius,top,radius,radius);

			Area justAB=new Area(class1);
			justAB.intersect(new Area(class2));	

			Area justA=new Area(class1);
			justA.subtract(new Area(class2));	

			Area justB=new Area(class2);
			justB.subtract(new Area(class1));
			
			// for maybe later use
			Area justABC=null;
			Area justAC=null;
			Area justBC=null;
			Area justC=null;
			
			if (plC!=null) {
				Ellipse2D class3=new Ellipse2D.Double();
				class3.setFrame(center-radius/2,bottom-radius,radius,radius);
				justABC=new Area(class1);
				justABC.intersect(new Area(class3));	
				justABC.intersect(new Area(class2));	

				justAC=new Area(class1);
				justAC.intersect(new Area(class3));	
				justAC.subtract(new Area(class2));

				justBC=new Area(class2);
				justBC.intersect(new Area(class3));	
				justBC.subtract(new Area(class1));

				justAB.subtract(new Area(class3));
				
				justA.subtract(new Area(class3));
				justB.subtract(new Area(class3));

				justC=new Area(class3);
				justC.subtract(new Area(class1));
				justC.subtract(new Area(class2));

			}

			Integer totalProbes = null;
			if (usePercentages.getBooleanValue()) {
				HashSet<Probe> all = new HashSet<Probe>();
				all.addAll(plA.getAllProbes());
				all.addAll(plB.getAllProbes());
				if (plC!=null)
					all.addAll(plC.getAllProbes());
				totalProbes = all.size();
			}
			
			// ========= Construct the components =======
			VennComponent vA=new VennComponent(A, totalProbes);
			vA.setColor(plA.getColor());

			VennComponent vB=new VennComponent(B, totalProbes);
			vB.setColor(plB.getColor());
			
			VennComponent vAB=new VennComponent(AB, totalProbes);
			vAB.setColor(blendColor(plA.getColor(), plB.getColor()));

			vA.setShape(justA);
			vB.setShape(justB);
			vAB.setShape(justAB);
						
			components.put(VennSet.A, vA);
			components.put(VennSet.B, vB);
			components.put(VennSet.AB, vAB);
			
			if (plC!=null) {
				VennComponent vC=new VennComponent(C, totalProbes);
				vC.setColor(plC.getColor());
				VennComponent vAC=new VennComponent(AC, totalProbes);
				vAC.setColor(blendColor(plA.getColor(), plC.getColor()));

				VennComponent vBC=new VennComponent(BC, totalProbes);
				vBC.setColor(blendColor(plB.getColor(), plC.getColor()));

				VennComponent vABC=new VennComponent(ABC, totalProbes);
				vABC.setColor(blendColor(blendColor(plB.getColor(), plC.getColor()),plA.getColor()));

				vC.setShape(justC);
				vAC.setShape(justAC);
				vBC.setShape(justBC);
				vABC.setShape(justABC);
				
				components.put(VennSet.C, vC);

				components.put(VennSet.AC, vAC);
				components.put(VennSet.BC, vBC);
				components.put(VennSet.ABC, vABC);
			}

		}
		
		updatePlot();
	}

	private Color blendColor(Color c1, Color c2)
	{
		return new Color(
				(c1.getRed()+c2.getRed())/2,
				(c1.getGreen()+c2.getGreen())/2,
				(c1.getBlue()+c2.getBlue())/2);
	}
	
	
	public static Rectangle2D placeStringAt(Graphics2D g, String s, double x, double y, float halign, float valign)
	{
		Rectangle2D bounds=g.getFontMetrics().getStringBounds(s, g);
		double h=g.getFontMetrics().getAscent();
		double d=g.getFontMetrics().getDescent();		
		double xp=x-(bounds.getWidth()*halign);		
		double yp=y-d+(h*(1-valign));
		g.drawString(s, (float)xp, (float)yp);
		bounds.setFrameFromCenter(x, y, x+bounds.getWidth()/2, y+bounds.getHeight()/2+d);
		return bounds;
	}

	@Override
	public void paintPlot(Graphics2D g) {

		g.clearRect(0, 0,getWidth(),getHeight());
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		switch (components.size()) {
		case 0:
			// no plot configured			
			g.setColor(Color.black);
			placeStringAt(g,"Please select two or three probelists.", center, top,.5f,0);
			break;
		case 3:
			// two probelists
			if(!hide2Intersections.getBooleanValue())
				components.get(VennSet.AB).paint(g);
			if(!hideUnintersected.getBooleanValue()) {			
				components.get(VennSet.A).paint(g);
				components.get(VennSet.B).paint(g);
			}	
			// place the labels
			placeStringAt(g, probeListA.getObjectValue().getName(), left,top-5,0,1);
			placeStringAt(g, probeListB.getObjectValue().getName(), right,top-5,1,1);
			break;
		case 7: 
			// three probelists
			if(!hide3Intersection.getBooleanValue())
				components.get(VennSet.ABC).paint(g);
			if(!hide2Intersections.getBooleanValue()) {
				components.get(VennSet.AB).paint(g);
				components.get(VennSet.AC).paint(g);
				components.get(VennSet.BC).paint(g);
			}
			if(!hideUnintersected.getBooleanValue()) {
				components.get(VennSet.C).paint(g);
				components.get(VennSet.A).paint(g);
				components.get(VennSet.B).paint(g);
			}
			// place the labels
			placeStringAt(g, probeListA.getObjectValue().getName(), left,top-5,0,1);
			placeStringAt(g, probeListB.getObjectValue().getName(), right,top-5,1,1);
			placeStringAt(g, probeListC.getObjectValue().getName(), center, bottom+5,.5f,0); 
			break;
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		// what area was clicked on?
		Point p=e.getPoint();
		Set<Probe> current;

		switch(e.getButton()) {
		case MouseEvent.BUTTON1:
			int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
			if (! ((e.getModifiers()&CONTROLMASK) == CONTROLMASK)) {
				for(VennComponent comp:components.values())
					comp.setSelected(false);
			}

			current =new TreeSet<Probe>();
			for(VennComponent comp:components.values())
			{
				if(comp.getShape().contains(p)) {
					comp.setSelected(true); 
					current.addAll(comp.getProbes());
				}
			}
			viewModel.setProbeSelection(current);	
			updatePlot();
			break;
		case MouseEvent.BUTTON3:
			current=new TreeSet<Probe>();
			for(VennComponent comp:components.values())	{
				if(comp.getShape().contains(p))
					current.addAll(comp.getProbes());
			}
			if (current.size()==0)
				return;
			List<Probe> probes=new ArrayList<Probe>(current);
			ProbeMenu menu=new ProbeMenu(probes,probes.get(0).getMasterTable());
			menu.getPopupMenu().show(this, e.getX(), e.getY());
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e){}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void viewModelChanged(ViewModelEvent vme) 
	{
		switch(vme.getChange()) {
		case ViewModelEvent.PROBE_SELECTION_CHANGED:
			for(VennComponent comp:components.values())
				comp.setSelected(false);

			Set<Probe> selected= viewModel.getSelectedProbes();
			for(VennComponent comp:components.values())
				if(!Collections.disjoint(comp.getProbes(), selected))
					comp.setSelected(true);

			updatePlot();
			break;

		case ViewModelEvent.PROBELIST_SELECTION_CHANGED:
			List<ProbeList> pl = new LinkedList<ProbeList>(viewModel.getProbeLists(false));
			probeListA.updatePredefined(pl);
			probeListB.updatePredefined(pl);
			pl.add(0, NONE);
			probeListC.updatePredefined(pl);
			
			if(probeListA.getSelectedIndex() < 0)
				probeListA.setSelectedIndex(0);
			if(probeListB.getSelectedIndex() < 0)
				probeListB.setSelectedIndex(0);
			if(probeListC.getSelectedIndex() < 0)
				probeListC.setSelectedIndex(0);
			updatePlot();
			break;
		}
	}

	@Override
	public void stateChanged(SettingChangeEvent e) 
	{
		updateTransformation();
		if(e.getSource()==probeListA || e.getSource()==probeListB ||  e.getSource()==probeListC || e.getSource()==usePercentages) {
			if (viewModel.getProbeLists(false).size()<2)
				setContents();
			else
			if (probeListC.getObjectValue()==NONE) {
				setContents(probeListA.getObjectValue(),probeListB.getObjectValue());
			} else {
				setContents(probeListA.getObjectValue(),probeListB.getObjectValue(),probeListC.getObjectValue());
			}
		}
		updatePlot();
	}

	@Override
	public void removeNotify() {
		viewModel.removeViewModelListener(this);
		super.removeNotify();
	}

	@Override
	public void probeListChanged(ProbeListEvent event) {
		switch (event.getChange()) {
		case ProbeListEvent.CONTENT_CHANGE: // fall through
		case ProbeListEvent.LAYOUT_CHANGE: // colors and such
			if (probeListC.getObjectValue()==NONE) {
				setContents(probeListA.getObjectValue(),probeListB.getObjectValue());
			} else {
				setContents(probeListA.getObjectValue(),probeListB.getObjectValue(),probeListC.getObjectValue());
			}
			updatePlot();
		}
	}


}
