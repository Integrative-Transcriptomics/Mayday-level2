package mayday.motifsearch.tool;

import static org.junit.Assert.*;

import mayday.motifsearch.MotifSearch;

import org.junit.Test;


public class motifTester {

    @Test
    public void testIsSynonym() {
	assertTrue(MotifSearch.isValidSynonymName("1JFHTH"));
	assertTrue(MotifSearch.isValidSynonymName("G67H5H"));
	assertTrue(MotifSearch.isValidSynonymName("G67H5H000"));
	assertTrue(MotifSearch.isValidSynonymName("1JFHTH-FF"));
	assertTrue(MotifSearch.isValidSynonymName("1JFHTH-F"));
	assertTrue(MotifSearch.isValidSynonymName("1JFHTH-1F"));
	assertFalse(MotifSearch.isValidSynonymName("1JFHTH-"));
	assertFalse(MotifSearch.isValidSynonymName("1JfHTH"));

    }

}
