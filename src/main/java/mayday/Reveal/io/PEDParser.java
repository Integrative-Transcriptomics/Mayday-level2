package mayday.Reveal.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import mayday.Reveal.data.Haplotypes;
import mayday.Reveal.data.HaplotypesList;
import mayday.Reveal.data.Subject;
import mayday.Reveal.data.SubjectList;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;

/**
 * @author jaeger
 *
 */
public class PEDParser extends AbstractDataParser {

	private BooleanSetting binaryAffectionCodingSetting;
	private BooleanSetting hasFamilyIDSetting;
	private BooleanSetting hasParentsSetting;
	private BooleanSetting hasSexSetting;
	private BooleanSetting hasPhenotypeSetting;
	private BooleanSetting compoundGenotypesSetting;
	
	protected HaplotypesList haploList;
	protected SubjectList persons;
	protected HierarchicalSetting setting;
	
	/**
	 * default constructor
	 */
	public PEDParser() {
		setting = new HierarchicalSetting("PED File Settings");
		setting.addSetting(binaryAffectionCodingSetting = new BooleanSetting("Use 0/1 for affection states", null, false));
		setting.addSetting(hasFamilyIDSetting = new BooleanSetting("FamilyID column", null, true));
		setting.addSetting(hasParentsSetting = new BooleanSetting("Paternal and maternal ID columns", null, true));
		setting.addSetting(hasSexSetting = new BooleanSetting("Sex column", null, true));
		setting.addSetting(hasPhenotypeSetting = new BooleanSetting("Phenotype column", null, true));
		setting.addSetting(compoundGenotypesSetting = new BooleanSetting("Compound genotypes?", null, false));
	}
	
	public Setting getSetting() {
		return this.setting;
	}

	/**
	 * @param pedFile
	 */
	public void read(File pedFile) {
		try {
			int numberOfLines = numberOfLines(pedFile);
			
			BufferedReader br = new BufferedReader(new FileReader(pedFile));
			String line = null;
			
			haploList = new HaplotypesList(numberOfLines);
			persons = new SubjectList(numberOfLines);
			
			int personCounter = 0;
			
			while((line = br.readLine()) != null) {
				//exclude comment lines from parsing
				if(line.startsWith("#") || line.trim().equals("")) {
					continue;
				}
				
				
				StringTokenizer st = tokenize(line);
				int numTokens = st.countTokens();
				
				if(st.countTokens() < 6) {
					br.close();
					throw new FileFormatUnknownException();
				}
					
				
				int idCounter = 0;
				
				String familyID = "0";
				String individualID = "0";
				
				if(!hasFamilyIDSetting.getBooleanValue()) {
					//if the family id is missing, the first field is taken to be the individual id 
					//and the family id is automatically set to be the same as the individual id
					individualID = st.nextToken();
					familyID = individualID;
					idCounter++;
				} else {
					familyID = st.nextToken();
					individualID = st.nextToken();
					idCounter+=2;
				}
				
				//-1 indicates that there are no paternal and maternal ID codes
				//all individuals are assumed to be founders
				String paternalID = "-1";
				String maternalID = "-1";
				
				if(hasParentsSetting.getBooleanValue()) {
					paternalID = st.nextToken();
					maternalID = st.nextToken();
					idCounter+=2;
				}
				
				//-1 indicates that there is no sex information available
				int sex = -1;
				
				if(hasSexSetting.getBooleanValue()) {
					sex = Integer.parseInt(st.nextToken());
					idCounter++;
				}
				
				//null indicates that the phenotype information is missing
				Boolean affection = null;
				
				if(hasPhenotypeSetting.getBooleanValue()) {
					int affectionCode = Integer.parseInt(st.nextToken());
					idCounter++;
					
					if(binaryAffectionCodingSetting.getBooleanValue()) {
						switch(affectionCode) {
						case 0: 
							affection = false; 
							break;
						case 1: 
							affection = true; 
							break;
						default:
							affection =  null;
						}
					} else {
						/*
						 * 0: missing
						 * 1: unaffected
						 * 2: affected
						 * -9: missing
						 */
						switch(affectionCode) {
						case 1: 
							affection = false; 
							break;
						case 2: 
							affection = true; 
							break;
						default:
							affection = null;
						}
					}
				}
				
				if(compoundGenotypesSetting.getBooleanValue()) {
					//PED file is in the form AG rather than A G
					int numGenotypes = numTokens - idCounter + 1;
					Haplotypes h = new Haplotypes(numGenotypes);
					for(int i = idCounter; i < numTokens; i++) {
						String nucleotideAB = st.nextToken();
						char nucleotideA = nucleotideAB.charAt(0);
						char nucleotideB = nucleotideAB.charAt(1);
						h.addSNPA(i, nucleotideA);
						h.addSNPB(i, nucleotideB);
						
						haploList.add(h);
					}
				} else {
					int numGenotypes = (numTokens - idCounter + 1) / 2;
					Haplotypes h = new Haplotypes(numGenotypes);
					for(int i = idCounter; i < numTokens; i+=2) {
						char nucleotideA = st.nextToken().charAt(0);
						char nucleotideB = st.nextToken().charAt(0);
						h.addSNPA(i, nucleotideA);
						h.addSNPB(i, nucleotideB);
					}
					haploList.add(h);
				}
				
				//FIXME maybe we also have to set the sex of the person sometime in the future?!
				Subject p = new Subject(familyID, individualID, paternalID, maternalID, affection);
				persons.add(p);
				p.setIndex(personCounter++);
			}
			br.close();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * @return persons contained in the ped file
	 */
	public SubjectList getPersons() {
		return this.persons;
	}
	
	/**
	 * @return the haplotypes specified in the ped file
	 */
	public HaplotypesList getHaplotypes() {
		return this.haploList;
	}

	@Override
	public void write(File output) {
		// TODO implement a PED file writer function
	}

	@Override
	public String getName() {
		return "PED File Parser";
	}

	@Override
	public String getType() {
		return "data.ped";
	}

	@Override
	public String getDescription() {
		return "Read/Write PLINK PED files";
	}

	@Override
	public String getCategory() {
		return "Data Import & Export";
	}
}
