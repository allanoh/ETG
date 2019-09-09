package configs;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.etgtest.data.model.test_data;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class Csv_processor {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Value("classPath:/input/hotels.csv")
    private Resource inputResource;

    @Bean
    public Job readCSVFileJob() {
        return jobBuilderFactory
                .get("readCSVFileJob")
                .incrementer(new RunIdIncrementer())
                .start(step())
                .build();
    }

    @Bean
    public Step step() {
        return stepBuilderFactory
                .get("step")
                .<test_data, test_data>chunk(5)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public ItemProcessor<test_data, test_data> processor() {
        return new database_logger();
    }

    @Bean
    public FlatFileItemReader<test_data> reader() {
        FlatFileItemReader<test_data> itemReader = new FlatFileItemReader<test_data>();
        itemReader.setLineMapper(lineMapper());
        itemReader.setLinesToSkip(1);
        itemReader.setResource(inputResource);
        return itemReader;
    }


    @Bean
    public JdbcBatchItemWriter<test_data> writer() {

        JdbcBatchItemWriter<test_data> itemWriter = new JdbcBatchItemWriter<test_data>();

        itemWriter.setDataSource(dataSource());
        itemWriter.setSql("INSERT INTO Test_Data ( ID,NAME,DESCRIPTION,DATE) VALUES ( :id, :name, :description, :date)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<test_data>());
        return itemWriter;
    }

    @Bean
    public LineMapper<test_data> lineMapper() {
        DefaultLineMapper<test_data> lineMapper = new DefaultLineMapper<test_data>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        BeanWrapperFieldSetMapper<test_data> fieldSetMapper = new BeanWrapperFieldSetMapper<test_data>();

        lineTokenizer.setNames(new String[]{"id", "name", "description", "date"});
        lineTokenizer.setIncludedFields(new int[]{0, 1, 2, 3});
        fieldSetMapper.setTargetType(test_data.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    public DataSource dataSource() {

        EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();

        return embeddedDatabaseBuilder.addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql")
                .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
                .addScript("classpath:hotels.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }


}
