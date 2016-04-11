package mayday.wapiti.experiments.generic.reads.bamsam;

import mayday.core.*;
import mayday.core.pluginrunner.ProbeListPluginRunner;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.tasks.AbstractTask;
import mayday.vis3.tables.ExpressionTable;

import org.apache.commons.compress.compressors.FileNameUtil;
import picard.analysis.AlignmentSummaryMetrics;
import picard.analysis.CollectAlignmentSummaryMetrics;
import picard.analysis.CollectRnaSeqMetrics;
import picard.analysis.RnaSeqMetrics;
import picard.util.TabbedTextFileWithHeaderParser;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
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
     * Ask user about wanted settings.
    ASSUME_SORTED
    REF_FLAT
    STRAND_SPECIFICITY
    {NONE, FIRST_READ_TRANSCRIPTION_STRAND, SECOND_READ_TRANSCRIPTION_STRAND}

     Set individual for each file:
     //INPUT
     //OUTPUT
    */
    private List<String> createBasicPicardSettings() {
        List<String> settings = new ArrayList<>();

        HierarchicalSetting hs = new HierarchicalSetting("Picard Settings");

        BooleanSetting sorted = new BooleanSetting("Sorted?",
                "Are the files sorted", true);
        ObjectSelectionSetting strand = new ObjectSelectionSetting(
                "Strand specificity", null, 1, MODES
        );
        PathSetting ref = new PathSetting("REF_FLAT", null, "", false, true, false);

        hs.addSetting(strand)
                .addSetting(ref)
                .addSetting(sorted)
                .addSetting(explanation());

        SettingDialog sd = new SettingDialog(null, "Stat Settings", hs);
        sd.showAsInputDialog();
        if (sd.canceled()) {
            return null;
        }

        settings.add("ASSUME_SORTED=" + sorted.getBooleanValue());
        settings.add("REF_FLAT=" + ref.getStringValue());
        settings.add("STRAND_SPECIFICITY=" + strand.getStringValue());

        return settings;
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
     * @param settings
     */
    private void runRNAMetric(String in, String out, List<String> settings) {
        // temporary settings object for this run
        List<String> runSettings =  new ArrayList<>(settings);
        runSettings.add("INPUT=" + in);
        runSettings.add("OUTPUT=" + out);

        String[] s = runSettings.toArray(new String[0]);
        // run without exit
        CollectRnaSeqMetrics prog = new CollectRnaSeqMetrics();
        //RnaSeqMetrics
        (new CollectRnaSeqMetrics()).instanceMain(s);
    }

    /**
     * Run Picard's RNASeqMetric for the specified bam file 'in'.
     * @param in
     * @param out
     * @param settings
     */
    private void runSummary(String in, String out, List<String> settings) {
        // temporary settings object for this run
        List<String> runSettings =  new ArrayList<>();
        runSettings.add("INPUT=" + in);
        runSettings.add("OUTPUT=" + out);

        // Keep RNAseqMetric specific settings out
        // (Only AssumeSorted is of interest)
        for (String set : settings) {
            if (set.startsWith("ASSUME_SORTED=")) {
                runSettings.add(set);
            }
        }

        String[] s = runSettings.toArray(new String[0]);
        // run without exit
        (new CollectAlignmentSummaryMetrics()).instanceMain(s);
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
        List<String> settings = createBasicPicardSettings();
        if (settings ==  null) {
            // user cancelled
            return;
        }
        // only one tmp file, will be overwritten by each picard run
        String tmp = newTMP();
        for (String sam : samFiles) {
            // Prepare Map for Storage
            Map<String, Double> data = new HashMap<>();
            statistics.put(sam, data);
            // run RNASeqMetrics
            runRNAMetric(sam, tmp, settings);
            readPicard(data, tmp);
            // run Summary tool (overwrite last tmp)
            runSummary(sam, tmp, settings);
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
