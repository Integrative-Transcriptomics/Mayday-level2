package mayday.wapiti.experiments.generic.reads.bamsam;

import com.google.common.io.Files;
import mayday.core.*;
import mayday.core.pluginrunner.ProbeListPluginRunner;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.tasks.AbstractTask;
import mayday.vis3.tables.ExpressionTable;

import org.apache.commons.lang3.StringEscapeUtils;
import picard.analysis.CollectAlignmentSummaryMetrics;
import picard.analysis.CollectRnaSeqMetrics;
import picard.sam.SortSam;
import picard.sam.ViewSam;
import picard.util.TabbedTextFileWithHeaderParser;

import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Computes some statistics for SAM files and shows them to the user.
 * Created by adrian on 4/6/16.
 *
 * This classes uses the picard library. The individual tools are run with
 * a String array list of options {"Option=Value", ...}.
 */
public class RNAseqStat extends AbstractTask {

    /**
     * List of modes for strand specificity.
     */
    public static final String[] MODES
            = new String[] {"NONE", "FIRST_READ_TRANSCRIPTION_STRAND",
                    "SECOND_READ_TRANSCRIPTION_STRAND"};

    /**
     * paths of sam files.
     */
    private List<String> samFiles;

    /**
     * Map File -> Property -> Value
     */
    private Map<String, Map<String, Double>> statistics = new HashMap<>();


    /*
     The settings from the user.
     */
    private String strand;
    private String refflat;
    private String reference;

    /**
     * Subset of Picard's values to show and their user-friendly explanation.
     * Full list:
     * http://broadinstitute.github.io/picard/picard-metric-definitions.html#AlignmentSummaryMetrics
     * http://broadinstitute.github.io/picard/picard-metric-definitions.html#RnaSeqMetrics
     */
    private HashMap<String, String> selection = new HashMap<String, String>() {{
        // From Alignment Summary
        put("TOTAL_READS", "# reads");
        put("PF_READS_ALIGNED", "# aligned inc. ambiguities");
        put("PF_HQ_ALIGNED_READS", "# hq reads, error chance < 1%");
        put("MEAN_READ_LENGTH", "Mean read length");
        put("PF_MISMATCH_RATE", "Mismatch rate for all reads");
        put("PF_HQ_ERROR_RATE", "Mismatch rate hq");
        put("PF_INDEL_RATE", "insertions/deletions per 100 aligned bases");
        // From RNAseqMetrics
        put("PCT_CODING_BASES", "% coding bases");
        put("PCT_UTR_BASES", "% UTR bases");
        put("PCT_INTRONIC_BASES", "% intronic bases");
        put("PCT_INTERGENIC_BASES", "% intergenic bases");
        put("MEDIAN_CV_COVERAGE", "Mean covariance top 1k transcripts (ideally 0)");
        put("INCORRECT_STRAND_READS", "# reads aligned with incorrect strand.");
    }};

    public RNAseqStat(List<String> samFiles) {
        super("Create Statistics for SAM files");
        this.samFiles = samFiles;
    }


    /**
     * Check if a SAM/BAM file is sorted by coordinate.
     */
    public boolean isSorted(String path) {
        // Stream to collect picard output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        // Work, pipe output to 'ps'
        PrintStream original = System.out;
        try {
            System.setOut(ps);
            (new ViewSam()).instanceMain(new String[]{
                            "INPUT=" + StringEscapeUtils.escapeJava(path),
                            "HEADER_ONLY=true",
                            "QUIET=true"
                    }
            );
        } finally {
            // what-ever happens, make sure to restore System.out!!!
            System.setOut(original);
        }
        // Parse
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            BufferedReader br = new BufferedReader(new InputStreamReader(bais));
            String line;
            // find "@HD ... SO:coordinate" header line
            while ((line = br.readLine()) != null) {
                if (line.startsWith("@hd") && line.endsWith("SO:coordinate")) {
                    // is coordinate sorted
                    return true;
                }
            }
        } catch (IOException e) { }
        // nothing matched or HD flag is not set -> probably not sorted
        return false;
    }

    /**
     * Creates a sorted version of the specified file. Returns path to new file.
     * @param path
     * @return
     */
    public String sortSam(String path) {
        String newpath = pathForSorting(path);
        (new SortSam()).instanceMain(new String[]{
                "INPUT=" + StringEscapeUtils.escapeJava(path),
                "OUTPUT=" + StringEscapeUtils.escapeJava(newpath),
                "SORT_ORDER=coordinate"
        });
        return newpath;
    }


    /**
     * Find path to a new SAM file close to original 'path', that does
     * not overwrite anything.
     * @param path
     * @return
     */
    public String pathForSorting(String path) {
        File f = new File(path);
        String name = Files.getNameWithoutExtension(path);

        File result = new File(f.getParent(), name + "_coordinatesorted.bam");
        // If needed add numbers to filename
        int i=1 ;
        while (result.exists() && i > 0) {
            result = new File(f.getParent(),
                    name + "_coordinatesorted_" + i + ".bam");
            i += 1;
        }
        if (i <= 0) {
            throw new RuntimeException("Was unable to create a new file of pattern: " +
                    "<oldname>_coordinatesorted[_<number>].bam");
        }
        return result.getAbsolutePath();
    }

    /**
     * Ask user about wanted settings.
     * @return  false if cancel
     */
    private boolean askPicardSettings() {
        List<String> settings = new ArrayList<>();

        HierarchicalSetting hs = new HierarchicalSetting("Picard Settings");

        ObjectSelectionSetting strand = new ObjectSelectionSetting(
                "Strand specificity", null, 1, MODES
        );
        PathSetting refflat = new PathSetting("REF_FLAT", null, "", false, true,
                // prohibit empty
                false);
        PathSetting reference = new PathSetting("Reference fasta (optional)", null, "", false, true,
                // allow empty
                true);

        hs.addSetting(strand)
                .addSetting(refflat)
                .addSetting(reference)
                .addSetting(explanation());

        SettingDialog sd = new SettingDialog(null, "Stat Settings", hs);
        sd.showAsInputDialog();
        if (sd.canceled()) {
            return false;
        }

        // store results
        this.strand = strand.getStringValue();
        this.refflat = refflat.getStringValue();

        if(new File(reference.getStringValue()).exists()) {
            this.reference = reference.getStringValue();
        } else {
            this.reference = null;
        }

        // not canceled
        return true;
    }

    /**
     * Create Explanation Text for Settigns Dialog.
     * @return
     */
    private ComponentPlaceHolderSetting explanation() {
        // TODO http://hgdownload.cse.ucsc.edu/downloads.html
        //For strand-specific library prep. For unpaired reads, use
        // FIRST_READ_TRANSCRIPTION_STRAND if the reads are expected to be on
        // the transcription strand.
        return new ComponentPlaceHolderSetting("", new JTextArea("Hello"));
    }

    /**
     * Run Picard's RNASeqMetric for the specified bam file 'in'.
     * @param in
     * @param out
     */
    private void runRNAMetric(String in, String out) {
        (new CollectRnaSeqMetrics()).instanceMain(new String[]{
                "INPUT=" + StringEscapeUtils.escapeJava(in),
                "OUTPUT=" + StringEscapeUtils.escapeJava(out),
                "REF_FLAT=" + StringEscapeUtils.escapeJava(refflat),
                "STRAND_SPECIFICITY=" + strand
        });
    }

    /**
     * Run Picard's RNASeqMetric for the specified bam file 'in'.
     * @param in
     * @param out
     */
    private void runSummary(String in, String out) {
        // run without exit
        (new CollectAlignmentSummaryMetrics()).instanceMain(new String[]{
                "INPUT=" + StringEscapeUtils.escapeJava(in),
                "OUTPUT=" + StringEscapeUtils.escapeJava(out),
                "REFERENCE_SEQUENCE=" + reference
        });
    }

    /**
     * Proccess a Picard file and fill the SELECTION fields into the given map.
     * @param storage
     * @param path
     */
    private void readPicard(Map<String, Double> storage, String path) {
        // Load file
        TabbedTextFileWithHeaderParser parser
                = new TabbedTextFileWithHeaderParser(new File(path));
        // Load first row
        Iterator<TabbedTextFileWithHeaderParser.Row> iter = parser.iterator();
        TabbedTextFileWithHeaderParser.Row row = iter.next();
        // Skip to correct row (tool specific)
        // RNASeqMetrics -> First one (nothing to do)
        // AlignmentSummary -> 'PAIR' or 'UNPAIRED' line
        // Characteristic for AlignmentSummary is the existance of the CATEGORY
        // colum.
        if (parser.hasColumn("CATEGORY")) {
            while (!(row.getField("CATEGORY").equals("UNPAIRED") ||
                    row.getField("CATEGORY").equals("PAIR"))) {
                row = iter.next();
            }
        }
        for (String metric : selection.keySet()) {
            try {
                storage.put(metric, Double.valueOf(row.getField(metric)));
            } catch (NumberFormatException e) {
                System.err.println("Non numeric value for metric " + metric + "found, will use NAN");
                storage.put(metric, Double.NaN);
            } catch (NoSuchElementException e) {
                // Field could be in other file -> ignore
            }
        }
    }

    @Override
    protected void doWork() throws Exception {
        // Ask for general Settings
        if (!askPicardSettings()) {
            // user cancelled
            return;
        }
        // only one tmp file, will be overwritten by each picard run
        String tmp = newTMP();
        for (String sam : samFiles) {
            // Prepare Map for Storage
            Map<String, Double> data = new HashMap<>();
            statistics.put(sam, data);
            // create sorted SAM if needed
            if (!isSorted(sam)) {
                // sort and get new path
                sam = sortSam(sam);
            }
            // run RNASeqMetrics
            runRNAMetric(sam, tmp);
            readPicard(data, tmp);
            // run Summary tool (overwrite last tmp)
            runSummary(sam, tmp);
            readPicard(data, tmp);
        }
        // cleanup
        delTMP(tmp);
        showData();
    }

    /**
     * Create a path to a temporary file.
     * @return
     */
    private String newTMP() throws IOException{
        try {
            return File.createTempFile("mayday-", ".tsv").getAbsolutePath();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not create temporary file for Picard.");
            throw e;
        }
    }

    /**
     * Delete the specified temporary file.
     * @param path
     */
    private void delTMP(String path) {
        File f = new File(path);
        f.delete();
    }

    /**
     * Create a virtual Dataset and use the ExpressionTabular to view it.
     */
    private void showData() {
        DataSet ds = new DataSet();
        MasterTable tmt = new MasterTable(ds);
        ProbeList data = new ProbeList(ds, true);
        // create Columns
        for (String sam : samFiles) {
            String basename = new File(sam).getName();
            tmt.addExperiment(new Experiment(tmt, basename));
        }
        // Fill rows
        for (String metric : selection.keySet()) {
            Probe pb = new Probe(tmt, true);
            pb.setName(selection.get(metric));

            double[] values = new double[samFiles.size()];
            for(int i=0; i < samFiles.size(); i++) {
                values[i] = statistics.get(samFiles.get(i)).get(metric);
            }

            pb.setValues(values);
            data.addProbe(pb);
        }
        // Run the plugin
        PluginInfo plugin = PluginManager.getInstance().getPluginFromID(ExpressionTable.PLID);
        List<ProbeList> pl = new ArrayList<ProbeList>();
        pl.add(data);
        ProbeListPluginRunner plpr = new ProbeListPluginRunner(plugin,
                pl , ds);
        plpr.execute();
    }

    @Override
    protected void initialize() {    }
}
