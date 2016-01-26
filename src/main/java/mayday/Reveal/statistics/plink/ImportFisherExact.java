package mayday.Reveal.statistics.plink;

public class ImportFisherExact extends AbstractPlinkStatImporter {

	@Override
	public void setName() {
		this.name = "FischersExact-PLINK";
	}

	@Override
	public void setSNPIDIndex() {
		this.snpid_field = 1;
	}

	@Override
	public void setPIndex() {
		this.p_field = 7;
	}
}
