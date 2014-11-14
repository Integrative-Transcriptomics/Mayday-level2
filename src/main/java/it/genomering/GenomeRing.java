package it.genomering;

import it.genomering.gui.GenomeRingFrame;
import it.genomering.render.RingDimensions;
import it.genomering.structure.Block;
import it.genomering.structure.CoveredBlock;
import it.genomering.structure.Genome;
import it.genomering.structure.SuperGenome;

import javax.swing.JFrame;

public class GenomeRing {

	public void start()  {

		SuperGenome superGenome = new SuperGenome();

		createExample(superGenome);

		int radius = 100 * superGenome.getNumberOfGenomes();
		
		RingDimensions ringdim = new RingDimensions(20, radius, 1, 5, superGenome);

		GenomeRingFrame grf = new GenomeRingFrame(superGenome, ringdim);
		
//		grf.addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosing(WindowEvent e) {
//				if (JOptionPane.showConfirmDialog(null, "Exit Genome Ring?", "Confirm exit", 
//						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
//					PluginManager.getInstance().shutdown();
//					try {
//						Preferences.userRoot().flush();
//					} catch (BackingStoreException e1) {
//						e1.printStackTrace();
//					}
//					System.out.println("Thank you for using Genome Ring.");
//					System.exit(0);
//				}		
//			}
//		});
		grf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		grf.setVisible(true);
	}
	
	public static void createExample(SuperGenome superGenome) {

		Block blockA = new Block(superGenome, "A", 100);
		Block blockB = new Block(superGenome, "B", 150);
		Block blockC = new Block(superGenome, "C", 200);
		Block blockD = new Block(superGenome, "D", 80);

		superGenome.addBlock( blockA );
		superGenome.addBlock( blockB );
		superGenome.addBlock( blockC );
		superGenome.addBlock( blockD );

		Genome g1 = new Genome(superGenome, false, "Red");
		g1.addCoveredBlock(new CoveredBlock(blockD, true));
		g1.addCoveredBlock(new CoveredBlock(blockB, false));
		Genome g2 = new Genome(superGenome, false, "Blue");
		g2.addCoveredBlock(new CoveredBlock(blockB, true));
		g2.addCoveredBlock(new CoveredBlock(blockA, false));
		g2.addCoveredBlock(new CoveredBlock(blockC, false));
		Genome g3 = new Genome(superGenome, false, "Green");
		g3.addCoveredBlock(new CoveredBlock(blockA, true));
		g3.addCoveredBlock(new CoveredBlock(blockC, true));
		g3.addCoveredBlock(new CoveredBlock(blockD, true));
		superGenome.addGenome(g1);
		superGenome.addGenome(g2);
		superGenome.addGenome(g3);
	}

}