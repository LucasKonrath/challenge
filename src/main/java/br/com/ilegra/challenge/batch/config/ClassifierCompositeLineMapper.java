package br.com.ilegra.challenge.batch.config;

import org.springframework.batch.item.file.LineMapper;
import org.springframework.classify.Classifier;

public class ClassifierCompositeLineMapper implements LineMapper<Object> {

    private final Classifier<String, LineMapper<?>> classifier;

    public ClassifierCompositeLineMapper(final Classifier<String, LineMapper<?>> classifier) {
        this.classifier = classifier;
    }

    @Override
    public Object mapLine(final String line, final int lineNumber) throws Exception {
        return classifier.classify(line).mapLine(line, lineNumber);
    }
}