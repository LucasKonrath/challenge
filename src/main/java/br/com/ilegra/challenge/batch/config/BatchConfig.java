package br.com.ilegra.challenge.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import br.com.ilegra.challenge.batch.tasklet.ReportTasklet;
import br.com.ilegra.challenge.batch.writer.MemoryItemWriter;
import br.com.ilegra.challenge.database.MemoryDatabase;
import br.com.ilegra.challenge.domain.Customer;
import br.com.ilegra.challenge.domain.Sale;
import br.com.ilegra.challenge.domain.Salesman;


@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Value("input/*.dat")
    private Resource[] inputResources;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MemoryDatabase memoryDatabase;

    @Bean
    public Job readDatFilesJob() {

        memoryDatabase.tearDown();
        
        return jobBuilderFactory
            .get("readDatFilesJob")
            .incrementer(new RunIdIncrementer())
            .start(step1())
            .next(step2())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").<Object, Object>chunk(5)
            .reader(multiResourceItemReader())
            .writer(writer())
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet(finalReportTasklet())
            .build();
    }

    @Bean
    public MultiResourceItemReader<Object> multiResourceItemReader() {
        final MultiResourceItemReader<Object> resourceItemReader = new MultiResourceItemReader<>();
        resourceItemReader.setResources(inputResources);
        resourceItemReader.setDelegate(reader());
        return resourceItemReader;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public FlatFileItemReader<Object> reader() {

        //Create reader instance
        final FlatFileItemReader<Object> reader = new FlatFileItemReader<>();

        //Set number of lines to skips. Use it if file has header rows.
        reader.setLinesToSkip(0);

        final Classifier<String, LineMapper<?>> classifier = new Classifier<String, LineMapper<? extends Object>>() {
            private static final long serialVersionUID = -9043814681344863328L;

            @Override
            public LineMapper<?> classify(final String classifiable) {
                if (classifiable.startsWith("001")) {
                    return new DefaultLineMapper() {
                        {
                            //4 columns in each row
                            setLineTokenizer(new DelimitedLineTokenizer("ç") {
                                {
                                    setNames(new String[]{"id", "cpf", "name", "salary"});
                                }
                            });
                            //Set values in SalesMan class
                            setFieldSetMapper(new BeanWrapperFieldSetMapper<Object>() {
                                {
                                    setTargetType(Salesman.class);
                                }
                            });
                        }
                    };
                }

                if (classifiable.startsWith("002")) {
                    return new DefaultLineMapper() {
                        {
                            //4 columns in each row
                            setLineTokenizer(new DelimitedLineTokenizer("ç") {
                                {
                                    setNames(new String[]{"id", "cnpj", "name", "businessArea"});
                                }
                            });
                            //Set values in SalesMan class
                            setFieldSetMapper(new BeanWrapperFieldSetMapper<Object>() {
                                {
                                    setTargetType(Customer.class);
                                }
                            });
                        }
                    };
                }

                if (classifiable.startsWith("003")) {
                    return new DefaultLineMapper() {
                        {
                            //4 columns in each row
                            setLineTokenizer(new DelimitedLineTokenizer("ç") {
                                {
                                    setNames(new String[]{"id", "saleId", "saleItems", "salesmanName"});
                                }
                            });
                            //Set values in SalesMan class
                            setFieldSetMapper(new BeanWrapperFieldSetMapper<Object>() {
                                {
                                    setTargetType(Sale.class);
                                    setDistanceLimit(0);
                                }
                            });

                        }
                    };
                }

                return new PassThroughLineMapper(); // or any other default line mapper
            }
        };

        final ClassifierCompositeLineMapper compositeLineMapper = new ClassifierCompositeLineMapper(classifier);
        reader.setLineMapper(compositeLineMapper);

        return reader;
    }

    @Bean
    public MemoryItemWriter<Object> writer() {
        return new MemoryItemWriter<>();
    }

    @Bean
    public ReportTasklet<Object> finalReportTasklet() {
        return new ReportTasklet<>();
    }
}