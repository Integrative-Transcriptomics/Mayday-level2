package mayday.Reveal.statistics.plink;

public class ImportLikelihoodRatio extends AbstractPlinkStatImporter {

	@Override
	public void setName() {
		this.name = "LikelihoodRatio-PLINK";
	}

	@Override
	public void setSNPIDIndex() {
		this.snpid_field = 1;
	}

	@Override
	public void setPIndex() {
		this.p_field = 8;
	}
}
