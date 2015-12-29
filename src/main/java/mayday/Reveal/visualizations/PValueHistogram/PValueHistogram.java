package mayday.Reveal.visualizations.PValueHistogram;
//package mayday.GWAS.visualizations.PValueHistogram;
//
//import java.awt.Graphics;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//
//import javax.swing.BoxLayout;
//
//import mayday.GWAS.data.SNP;
//import mayday.GWAS.data.SNPList;
//import mayday.GWAS.io.GWASFileReader;
//import mayday.GWAS.setting.PValHistSetting;
//import mayday.GWAS.visualizations.GWASVisualization;
//import mayday.core.gui.components.VerticalLabel;
//import mayday.core.settings.generic.HierarchicalSetting;
//import mayday.vis3.gui.PlotContainer;
//import mayday.vis3.model.ViewModelEvent;
//
///**
// * @author jaeger
// *
// */
//@SuppressWarnings("serial")
//public class PValueHistogram extends GWASVisualization implements MouseListener {
//
//	protected SNPList snpList;
//	protected PValColumn[] columns;
//	protected PValHistSetting setting;
//	
//	/**
//	 * @param gwasfr
//	 * @param pvalThreshold 
//	 */
//	public PValueHistogram(GWASFileReader gwasfr) {
//		super("P Value Histogram");
//		snpList = gwasfr.getSNPs();
//		this.setting = new PValHistSetting(this);
//		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
//		this.initialize();
//	}
//	
//	protected void initialize() {
//		this.columns = new PValColumn[snpList.size()];
//		double maxPValue = PVHistUtilities.getMaxLogPValue(snpList);
//		int maxLabelLength = PVHistUtilities.getMaxLabelLength(snpList);
//		for(int i = 0; i < columns.length; i++) {
//			SNP snp = snpList.get(i);
//			//FIXME p-values!
//			if(0 <= setting.getPValueThreshold() && 0 != -1) {
//				PValBox box = new PValBox(0, maxPValue);
//				VerticalLabel snpName = new VerticalLabel(snp.getID());
//				columns[i] = new PValColumn(i, box, snpName, maxLabelLength);
//				columns[i].addMouseListener(this);
//				this.add(columns[i]);
//			}
//		}
//	}
//
//	public void paint(Graphics g) {
//		for(int i = 0; i < snpList.size(); i++) {
//			if(columns[i] != null)
//				columns[i].repaint();
//		}
//	}
//
//	@Override
//	public void mouseClicked(MouseEvent e) {
//		if(e.getSource().getClass().equals(PValColumn.class)) {
//			int id = ((PValColumn)e.getSource()).getID();
//			SNP snp = snpList.get(id);	
//			System.out.println("SNP " + snp.getID() + " has been selected!");
//		}
//	}
//
//	@Override
//	public void mousePressed(MouseEvent e) {}
//
//	@Override
//	public void mouseReleased(MouseEvent e) {}
//
//	@Override
//	public void mouseEntered(MouseEvent e) {}
//
//	@Override
//	public void mouseExited(MouseEvent e) {}
//
//	@Override
//	public void viewModelChanged(ViewModelEvent vme) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setup(PlotContainer plotContainer) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public HierarchicalSetting setupSettings() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void updatePlot() {
//		repaint();
//	}
//}
