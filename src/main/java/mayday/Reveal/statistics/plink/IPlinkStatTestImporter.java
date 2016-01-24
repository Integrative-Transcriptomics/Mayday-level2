package mayday.Reveal.statistics.plink;

import java.io.File;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.meta.StatisticalTestResult;

public interface IPlinkStatTestImporter {

	public StatisticalTestResult importTestResults(DataStorage ds, File plinkFile, boolean header, String separator) throws Exception;
}
