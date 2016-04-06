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
import mayday.vis3.plots.heatmap2.HeatMap;
import mayday.vis3.tables.ExpressionTable;
import picard.analysis.CollectRnaSeqMetrics;

import javax.swing.*;
import javax.swing.text.Document;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.InflaterInputStream;

/**
 * Computes some statistics for SAM files and shows them to the user.
 * Created by adrian on 4/6/16.
 */
public class RNAseqStat extends AbstractTask {
    /**
     * paths of sam files.
     */
    private List<String> samFiles;

    /**
     * Map File -> Property -> Value
     */
    private Map<String, Map<String, Double>> statistics = new HashMap<>();

    /**
     * Subset of Picard's values to show.
     * Full list:
     * http://broadinstitute.github.io/picard/picard-metric-definitions.html#RnaSeqMetrics
     */
    private final String[] SELECTION = new String[]{
            "MEDIAN_CV_COVERAGE",
            "PCT_RIBOSOMAL_BASES",
            "PCT_CODING_BASES"
    };


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
                "Strand specificity", null, 1,
                new String[] {"NONE", "FIRST_READ_TRANSCRIPTION_STRAND",
                        "SECOND_READ_TRANSCRIPTION_STRAND"}
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
        //For strand-specific library prep. For unpaired reads, use FIRST_READ_TRANSCRIPTION_STRAND if the reads are expected to be on the transcription strand.
        return new ComponentPlaceHolderSetting("", new JTextArea("Hello"));
    }

    /**
     * Run Picard for the specified bam file 'in'.
     * @param in
     * @param out
     * @param settings
     */
    private void runPicard(String in, String out, List<String> settings) {
        // temporary settings object for this run
        List<String> runSettings =  new ArrayList<>(settings);
        runSettings.add("INPUT=" + in);
        runSettings.add("OUTPUT=" + out);

        String[] s = runSettings.toArray(new String[0]);
        // run without exit
        (new CollectRnaSeqMetrics()).instanceMain(s);
    }

    /**
     * Proccess a Picard file and fill the SELECTION fields into the given map.
     * @param storage
     * @param path
     */
    private void readPicard(Map<String, Double> storage, String path) {
        final String DELIM = "\t";
        try {
            File result = new File(path);
            List<String> lines = Files.readAllLines(result.toPath());

            // The file has only two relevant lines
            String[] header = null;
            String[] values = null;
            for (String l : lines) {
                // ignore comments
                if (l.length() == 0 || l.startsWith("#")) {
                    continue;
                }
                if (header == null) {
                    header = l.split(DELIM);
                } else {
                    // -1 limit to include every field, event trailing empty ones!
                    values = l.split(DELIM, -1);
                }
            }
            // Fill storage
            assert header.length == values.length;
            for (int i=0; i < header.length; i++) {
                String f = values[i];
                // use nan for empty fields
                Double value = f.length() == 0
                        ? Double.NaN
                        : Double.valueOf(f);
                storage.put(header[i], value);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not read Picard results: " + path);
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
        for (String sam : samFiles) {
            // Prepare Map for Storage
            Map<String, Double> data = new HashMap<>();
            statistics.put(sam, data);
            // run Picard
            String tmp = newTMP();
            if(tmp == null) {
                return;
            }
            runPicard(sam, tmp, settings);
            readPicard(data, tmp);
            // cleanup
            delTMP(tmp);
        }
        showData();
    }

    /**
     * Create a path to a temporary file.
     * @return
     */
    private String newTMP() {
        try {
            return File.createTempFile("mayday-", ".tsv").getAbsolutePath();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not create temporary file for Picard.");
        }
        return null;
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
            // TODO only basename
            tmt.addExperiment(new Experiment(tmt, sam));
        }
        // Fill rows
        for (String row : SELECTION) {
            Probe pb = new Probe(tmt, true);
            // TODO map it
            pb.setName(row);

            double[] values = new double[samFiles.size()];
            for(int i=0; i < samFiles.size(); i++) {
                values[i] = statistics.get(samFiles.get(i)).get(row);
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
