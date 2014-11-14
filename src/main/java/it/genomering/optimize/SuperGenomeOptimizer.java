package it.genomering.optimize;

import it.genomering.render.GenomeShape;
import it.genomering.render.RingDimensions;
import it.genomering.structure.Block;
import it.genomering.structure.Genome;
import it.genomering.structure.SuperGenome;
import it.genomering.visconnect.ConnectionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import mayday.core.tasks.AbstractTask;

/**
 * @author jaeger
 *
 */
public class SuperGenomeOptimizer {

	/**
	 * minimize the number of jumps (edges between non-consecutive blocks)
	 */
	public static final int OPTIMIZE_JUMPS = 0;
	/**
	 * minimize the number of skipped blocks between two connected blocks
	 */
	public static final int OPTIMIZE_BLOCKS = 1;
	/**
	 * minimize the total edge length, which is the sum of angle degrees of each edge 
	 */
	public static final int OPTIMIZE_ANGLES = 2;
	
	/**
	 * method: switch blocks randomly
	 */
	public static final int SWITCH_RANDOMLY = 0;
	/**
	 * method: switch blocks exhaustively -> all n^2 combinations
	 */
	public static final int SWITCH_BLOCKS_EXHAUSTIVELY = 1;
	/**
	 * method: first switch blocks exhaustively then insert blocks exhaustively -> all n^2 combinations
	 */
	public static final int SWITCH_FIRST_INSERT_LATER = 2;
	
	private double oldValue = Double.MAX_VALUE;
	private List<Block> tmpBlocks;
	
	/**
	 * @param method
	 * @param variable
	 * @param s
	 * @param ringDim
	 * @param cm
	 */
	public void optimize(final int method, final int variable, final SuperGenome s, final RingDimensions ringDim, final ConnectionManager cm) {
		
		AbstractTask task = new AbstractTask("Optimizing GenomeRing Block Order") {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				List<Block> original = new ArrayList<Block>(s.getBlocks());
				//first round: get costs from the given confirmation
				oldValue = Double.MAX_VALUE;
				tmpBlocks = null;
				calculateCosts(variable, s, ringDim, cm);

				s.setSilent(true);
				
				double converged = -1;
				int roundCount = 0;
				while(converged != oldValue) {
					System.out.println("Iteration: round " + (1+roundCount));
					converged = oldValue;
					//try to optimize according to the current configuration
					switch(method) {
					case SWITCH_RANDOMLY: switchBlocksRandomly(variable, s, ringDim, cm); break;
					case SWITCH_BLOCKS_EXHAUSTIVELY: switchBlocksExhaustively(variable, s, ringDim, cm); break;
					case SWITCH_FIRST_INSERT_LATER: switchFirstInsertLater(variable, s, ringDim, cm); break;
					}
					
					//try to optimize according to super genome sorting 
					boolean better = getCost(method, s.getInitialBlockOrder(), s, ringDim, cm);
					if(better)
						tmpBlocks = new ArrayList<Block>(s.getInitialBlockOrder());
					
					switch(method) {
					case SWITCH_RANDOMLY: switchBlocksRandomly(variable, s, ringDim, cm); break;
					case SWITCH_BLOCKS_EXHAUSTIVELY: switchBlocksExhaustively(variable, s, ringDim, cm); break;
					case SWITCH_FIRST_INSERT_LATER: switchFirstInsertLater(variable, s, ringDim, cm); break;
					}
				
					//try to optimize according to all the other genomes
					for(Genome g : new LinkedList<Genome>(s.getGenomes())) {
						g.resortSuperGenome();
					
						better = calculateCosts(variable, s, ringDim, cm);
						if(better)
							tmpBlocks = new ArrayList<Block>(s.getBlocks());
						
						switch(method) {
						case SWITCH_RANDOMLY: switchBlocksRandomly(variable, s, ringDim, cm); break;
						case SWITCH_BLOCKS_EXHAUSTIVELY: switchBlocksExhaustively(variable, s, ringDim, cm); break;
						case SWITCH_FIRST_INSERT_LATER: switchFirstInsertLater(variable, s, ringDim, cm); break;
						}
					}
					roundCount++;
					
					if(tmpBlocks != null) {
						s.setBlocks(tmpBlocks);
					}
				}
				
				System.out.println("Converged after " + (roundCount) + " round(s)!");
				
				s.setSilent(false);
				
				if (tmpBlocks==null)
					tmpBlocks=original;
				
				s.setBlocks(tmpBlocks);	
			}
			
		};
		
		task.start();
	}
	
	protected void switchBlocksRandomly(int variable, SuperGenome s,
			RingDimensions ringDim, ConnectionManager cm) {
		
	}

	protected void createRandomOrder(SuperGenome s,
			RingDimensions ringDim, ConnectionManager cm) {
		List<Block> blocks = s.getBlocksInternal();
		
		for(int i = 0; i < 100000; i++) {
			createRandomOrder(blocks);
			s.blockOrderChanged();
			
			boolean foundBetter = calculateCosts(OPTIMIZE_BLOCKS, s, ringDim, cm);
			if(foundBetter) {
//					tmpBlocks = new ArrayList<Block>(blocks);
				i = 0;
				System.out.println("better: " + i + " ... keep current and continue");
				break;
			}
		}
	}

	protected boolean calculateCosts(int costID, SuperGenome s, RingDimensions ringDim, ConnectionManager cm) {
		List<Genome> genomes = s.getGenomes();
		
		double[] costs = new double[3];
		
		for (int gidx=0; gidx!=genomes.size(); ++gidx) {
			GenomeShape gs = new GenomeShape(genomes.get(gidx), ringDim, cm);
			double[] subcost = gs.getCosts();
			for (int j=0; j!=costs.length; ++j)
				costs[j]+=subcost[j];
		}
		
		if(costs[costID] < oldValue) {
			oldValue = costs[costID];
			System.out.println("Better costs found\tJumps="+costs[0]+"\tBlocks="+costs[1]+"\tAngles="+costs[2]);
			return true;
		}
		
		return false;
	}
	
	protected void switchBlocks(List<Block> blocks, int blockID1, int blockID2) {
		Block tmp = blocks.get(blockID1);
		blocks.set(blockID1, blocks.get(blockID2));
		blocks.set(blockID2, tmp);
	}
	
	protected void switchBlocksRandomly(List<Block> blocks) {
		Random r = new Random();
		int blockID1 = r.nextInt(blocks.size());
		int blockID2 = blockID1;
		while(blockID2 == blockID1) {
			blockID2 = r.nextInt(blocks.size());
		}
		switchBlocks(blocks, blockID1, blockID2);
	}
	
	protected void createRandomOrder(List<Block> blocks) {
		Collections.shuffle(blocks);
	}
	
	protected void switchBlocksExhaustively(int variable, SuperGenome s, RingDimensions ringDim, ConnectionManager cm) {
		List<Block> blocks = new ArrayList<Block>(s.getBlocks());
		
		for(int i = 0; i < blocks.size() - 1; i++) {
			for(int j = i+1; j < blocks.size(); j++) {
				switchBlocks(blocks, i, j);
				boolean foundBetter = getCost(variable, blocks, s, ringDim, cm);
				if(!foundBetter) {
					switchBlocks(blocks, i, j);
//					System.out.println("not better ... switch back");
				} else {
					tmpBlocks = new ArrayList<Block>(blocks);
					i = 0;
					System.out.println("better: " + i + "  & " + j + " ... keep current and continue");
					break;
				}
			}
		}
	}
	
	protected void switchFirstInsertLater(int variable, SuperGenome s, RingDimensions ringDim, ConnectionManager cm) {
		List<Block> blocks = new ArrayList<Block>(s.getBlocks());
		
		for(int i = 0; i < blocks.size() - 1; i++) {
			for(int j = i+1; j < blocks.size(); j++) {
				switchBlocks(blocks, i, j);
				boolean better = getCost(variable, blocks, s, ringDim, cm);
				if(better) {
					tmpBlocks = new ArrayList<Block>(blocks);
					//System.out.println("better by switching: " + blocks.get(i).getIndex() + " -- " + blocks.get(j).getIndex());
					break;
				} 
			}
		}
		
		changeInsertions(variable, blocks, s, ringDim, cm);
	}
	
	private void changeInsertions(int variable, List<Block> blocks, SuperGenome s,
			RingDimensions ringDim, ConnectionManager cm) {
		for(int i = 0; i < blocks.size(); i++) {
			for(int j = 0; j < blocks.size(); j++) {
				if(i == j) continue;
				Block tmp = blocks.get(i);
				blocks.remove(i);
				blocks.add(j, tmp);
				boolean better = getCost(variable, blocks, s, ringDim, cm);
				if(better) {
					tmpBlocks = new ArrayList<Block>(blocks);
					//System.out.println("Better by inserting block " + tmp.getIndex() + " in front of " + blocks.get(j).getIndex());
					break;
				}
				//reverse insertion
				tmp = blocks.get(j);
				blocks.remove(j);
				blocks.add(i, tmp);
			}
		}
	}

	private boolean getCost(int variable, List<Block> blocks, SuperGenome s, RingDimensions ringDim, ConnectionManager cm) {
		s.setBlocks(blocks);
		return calculateCosts(variable, s, ringDim, cm);
	}
}
