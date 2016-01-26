package mayday.Reveal.statistics.plink;

public class ImportHardyWeinberg extends AbstractPlinkStatImporter {

	@Override
	public void setName() {
		this.name = "HardyWeinberg-PLINK";
	}

	@Override
	public void setSNPIDIndex() {
		this.snpid_field = 0;
	}

	@Override
	public void setPIndex() {
		this.p_field = 7;
	}
}
