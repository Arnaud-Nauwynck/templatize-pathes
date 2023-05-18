package fr.an.test;

import java.io.*;
import java.util.*;

public class TemplatizePathesMain {

    public static void main(String[] args) {
        PathTemplatizer templatizer = new PathTemplatizer();
        Map<String,TemplateEntryCounter> templateCounters = new HashMap<>();
        int maxSamples = 100;
        int samplingFreq = 50;

        // read all lines from file, templatize, increment count templates
        File file = new File("src/test/pathes.txt");
        try(BufferedReader lineReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            for(;;) {
                String line = lineReader.readLine();
                if (line == null) {
                    break;
                }
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String template = templatizer.templatize(line);
                TemplateEntryCounter templateEntry = templateCounters.computeIfAbsent(template, k -> new TemplateEntryCounter(k, maxSamples, samplingFreq));
                templateEntry.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // show templates  (order by count desc)
        List<TemplateEntryCounter> sortedTemplates = new ArrayList<>(templateCounters.values());
        Collections.sort(sortedTemplates,
                (o1,o2) -> - Integer.compare(o1.count, o2.count)
                );
        int index = 1;
        for(TemplateEntryCounter entry : sortedTemplates) {
            System.out.println("[" + index + "] " + entry.template + "  count:" + entry.count
                    // + entry.samples
                    );
            index++;
        }
    }

}
