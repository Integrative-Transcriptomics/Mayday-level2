package it.genomering.gui;

import it.genomering.gui.actions.ChangeGenomeNamesAction;
import it.genomering.render.RingDimensions;
import it.genomering.structure.SuperGenome;
import it.genomering.visconnect.ConnectionManager;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;

import mayday.core.gui.MaydayFrame;
import mayday.vis3.ZoomController;
import mayday.vis3.components.PlotScrollPane;
import mayday.vis3.gui.actions.ExportPlotAction;
import mayday.vis3.gui.actions.ExportVisibleAreaAction;

@SuppressWarnings("serial")
public class GenomeRingFrame extends MaydayFrame {

	public GenomeRingFrame(SuperGenome superGenome, RingDimensions ringdim) {
		setLayout(new BorderLayout());
		setup(superGenome, ringdim);		
	}

	protected void setup(SuperGenome superGenome, RingDimensions ringdim) {		
		getContentPane().removeAll();
		
		ConnectionManager cm = new ConnectionManager(superGenome);
		
		GenomeRingPanel grp = new GenomeRingPanel(superGenome, ringdim, cm);
		JScrollPane jsp = new PlotScrollPane(grp);
		ZoomController zc = new ZoomController();
		zc.setTarget(grp);

		add(jsp, BorderLayout.CENTER);		
		add(new GenomeLegend(superGenome, grp), BorderLayout.SOUTH);
		
		setSize(800, 600);
		
		JMenu mFile = new JMenu("File");
		mFile.add(superGenome.new LoadFromFileAction());
		mFile.add(superGenome.new SaveToBlocksFileAction());
		mFile.add(new ExportPlotAction(getContentPane()));
		mFile.add(new ExportVisibleAreaAction(getContentPane()));
		
		JMenu mSuperGenome = new JMenu("SuperGenome");
		mSuperGenome.add(cm.new ConfigureConnectionsAction());
		
		JMenu optimizeMenu = new JMenu("Optimize block order");
		optimizeMenu.add(new OptimizeJumpsAction(superGenome, ringdim, cm));
		optimizeMenu.add(new OptimizeBlockAction(superGenome, ringdim, cm));
		optimizeMenu.add(new OptimizeAnglesAction(superGenome, ringdim, cm));
		
		mSuperGenome.add(optimizeMenu);
		mSuperGenome.add(new OptimizeManuallyAction(superGenome, ringdim, cm));
		
		JMenu saveLoadOrderingMenu = new JMenu("Restore previous order");
		AbstractAction aa = new RememberOrderingAction(superGenome, saveLoadOrderingMenu);
		mSuperGenome.add(aa);
		mSuperGenome.add(saveLoadOrderingMenu);		
		
		JMenu mView = new JMenu("View");
		Component visSettingMenu = grp.setting.getMenuItem(this);
		mView.add(visSettingMenu);
		mView.add(new ChangeGenomeNamesAction(superGenome));
		
		setJMenuBar(new JMenuBar());
		getJMenuBar().add(mFile);
		getJMenuBar().add(mSuperGenome);
		getJMenuBar().add(mView);
		
		setTitle("GenomeRing");
		
		this.invalidate();
		this.validate();
	}
}
