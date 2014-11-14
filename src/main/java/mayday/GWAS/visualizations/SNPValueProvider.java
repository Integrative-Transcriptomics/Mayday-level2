package mayday.GWAS.visualizations;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import mayday.GWAS.data.Gene;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.data.meta.Genome;
import mayday.GWAS.data.meta.SingleLocusResult;
import mayday.GWAS.data.meta.StatisticalTestResult;
import mayday.GWAS.viewmodel.RevealViewModel;

public class SNPValueProvider {

	public interface Provider {
		public double getValue(SNP s);
		public String getName();
		public int getSourceType();
	}
	
	public class ChromosomalLocationProvider implements Provider {
		@Override
		public double getValue(SNP s) {
			String chromosome = s.getChromosome();
			int position = s.getPosition();
			
			Genome genome = viewModel.getDataStorage().getGenome();
			
			if(genome != null) {
				long globalPos = genome.getGlobalPosition(chromosome, position);
				double finalPos = globalPos / (double)genome.getTotalLength(); 
				return finalPos;
			}
			
			//if no chromosomal location can be used? use the index instead!
			return s.getIndex();
		}

		@Override
		public String getName() {
			return "Chromosomal Location";
		}

		@Override
		public int getSourceType() {
			return SNPValueProviderSetting.CHROMOSOMAL_LOCATION_VALUE;
		}
	}
	
	public class SNPIndexProvider implements Provider {
		@Override
		public double getValue(SNP s) {
			return s.getIndex();
		}

		@Override
		public String getName() {
			return "SNP Index";
		}

		@Override
		public int getSourceType() {
			return SNPValueProviderSetting.SNP_INDEX_VALUE;
		}
	}
	
	public class SingleLocusResultProvider implements Provider {
		
		private Gene gene;
		
		public SingleLocusResultProvider(Gene gene) {
			super();
			this.gene = gene;
		}
		
		@Override
		public double getValue(SNP s) {
			SingleLocusResult slr = setting.getSelectedSingleLocusResult(gene);
			if(slr == null)
				return 0;
			
			double p = slr.get(s).p;
			
			if(Double.isNaN(p)) { //ignore NaNs
				return 0;
			}
			
			return -Math.log10(p);
		}

		@Override
		public String getName() {
			return "-log(p) for " + gene.getName();
		}

		@Override
		public int getSourceType() {
			return SNPValueProviderSetting.SINGLE_LOCUS_RESULT_VALUE;
		}

		public double getMaxValue() {
			SingleLocusResult slr = setting.getSelectedSingleLocusResult(gene);
			return -Math.log10(slr.minPValue);
		}
	}
	
	public class StatisticalTestResultProvider implements Provider {
		
		@Override
		public double getValue(SNP s) {
			StatisticalTestResult statResult = setting.getSelectedStatResult();
			if(statResult == null) {
				return Double.NaN;
			} else {
				return -Math.log10(statResult.getPValue(s));
			}
		}

		@Override
		public String getName() {
			StatisticalTestResult statResult = setting.getSelectedStatResult();
			if(statResult != null)
				return "-log(p) from " + statResult.getStatTestName();
			return "-log(p) from statistical test";
		}

		@Override
		public int getSourceType() {
			return SNPValueProviderSetting.STAT_TEST_RESULT_VALUE;
		}
	}
	
	protected RevealViewModel viewModel;
	protected String title;
	protected Provider provider;
	protected SNPValueProviderSetting setting;
	
	private EventListenerList eventListenerList = new EventListenerList();
	
	public void addChangeListener(ChangeListener cl) {
		eventListenerList.add(ChangeListener.class, cl);
	}
	
	public void removeChangeListener(ChangeListener cl) {
		eventListenerList.remove(ChangeListener.class, cl);
	}
	
	public double getValue(SNP s) {
		return provider.getValue(s);
	}
	
	public String getSourceName() {
		return provider.getName();
	}
	
	public Provider getProvider() {
		return provider;
	}
	
	public RevealViewModel getViewModel() {
		return viewModel;
	}
	
	public void setProvider(Provider p) {
		provider = p;
		makeSetting();
		fireChanged();
	}
	
	protected void fireChanged() {
		Object[] l_listeners = this.eventListenerList.getListenerList();

		if (l_listeners.length==0)
			return;
		
		ChangeEvent event = new ChangeEvent(this);

		// process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = l_listeners.length-2; i >= 0; i-=2 )  {
			if ( l_listeners[i] == ChangeListener.class )  {
				ChangeListener list = ((ChangeListener)l_listeners[i+1]);
				list.stateChanged(event);
			}
		}
	}
	
	public SNPValueProvider(RevealViewModel viewModel, String menuTitle) {
		this.viewModel = viewModel;
		title = menuTitle;
		provider = new ChromosomalLocationProvider();
		makeSetting();
	}
	
	protected void makeSetting() {
		setting = new SNPValueProviderSetting(title, null, this, viewModel);
	}
	
	public String getMenuTitle() {
		return title;
	}
	
	public SNPValueProviderSetting getSetting() {
		return setting;
	}

	public int getSourceType() {
		
		return provider.getSourceType();
	}

	public double getMinValue(SNPList snps) {
		double min = Double.MAX_VALUE;
		for(SNP s : snps) {
			double value = getValue(s);
			if(value < min)
				min = value;
		}
		return min;
	}
	
	public double getMaxValue(SNPList snps) {
		double max = Double.MIN_VALUE;
		for(SNP s : snps) {
			double value = getValue(s);
			if(value > max)
				max = value;
		}
		return max;
	}
}
