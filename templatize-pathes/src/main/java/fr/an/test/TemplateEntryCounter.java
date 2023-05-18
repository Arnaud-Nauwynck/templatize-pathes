package fr.an.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TemplateEntryCounter {

    public final String template;

    public final int maxSamples;
    public final int samplingFreq;

    protected int count;

    protected Random replaceIdxRandomGenerator = new Random(0);
    protected final List<String> samples = new ArrayList<>();

    //---------------------------------------------------------------------------------------------

    public TemplateEntryCounter(String template, int maxSamples, int samplingFreq) {
        this.template = template;
        this.maxSamples = maxSamples;
        this.samplingFreq = samplingFreq;
    }

    //---------------------------------------------------------------------------------------------

    public void add(String line) {
        count++;
        if (samples.size() < maxSamples) {
            samples.add(line);
        } else {
            if ((count % samplingFreq) == 0) {
                int index = replaceIdxRandomGenerator.nextInt(maxSamples);
                samples.set(index, line);
            }
        }
    }

}
