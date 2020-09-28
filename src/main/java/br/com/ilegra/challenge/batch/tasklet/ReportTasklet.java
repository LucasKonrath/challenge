package br.com.ilegra.challenge.batch.tasklet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.ilegra.challenge.database.MemoryDatabase;

@Component
public class ReportTasklet<T> implements Tasklet {

    @Autowired
    private MemoryDatabase memoryDatabase;

    @Override
    public RepeatStatus execute(final StepContribution stepContribution, final ChunkContext chunkContext)
        throws Exception {

        File file = new File("outputData.dat");

        if (file.createNewFile()) {
            System.out.println("File created: " + file.getName());
        } else {

            file.delete();

            System.out.println("File already exists.");

            file = new File("outputData.dat");

        }

        try {
            final FileWriter myWriter = new FileWriter(file);

            final Optional<Entry<Integer, Double>> maxEntry = memoryDatabase.getSalesMap().entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue));

            final Optional<Entry<String, Double>> minEntry = memoryDatabase.getSalesmen().entrySet()
                .stream()
                .min(Comparator.comparing(Map.Entry::getValue));

            myWriter.write(String.format("Salesmen: %d\n"
                    + "Consumers: %d\n"
                    + "Biggest sale: Id: %d, Value: %f\n"
                    + "Worst salesman: %s with %f sold",

                memoryDatabase.getSalesmenQuantity(), memoryDatabase.getCustomerQuantity(),
                maxEntry.get().getKey(), maxEntry.get().getValue(),
                minEntry.get().getKey(), minEntry.get().getValue()));

            myWriter.close();

            System.out.println("Successfully wrote to the file.");
        } catch (final IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        memoryDatabase.tearDown();

        return RepeatStatus.FINISHED;
    }
}