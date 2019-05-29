package com.lulu.odsTocsv.batch;

import com.lulu.odsTocsv.model.Brand;
import com.lulu.odsTocsv.service.BrandItemProcesser;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Configurable
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/muthu");
        dataSource.setUsername("root");
        dataSource.setPassword("");

        return dataSource;
    }

    @Bean
    public JdbcCursorItemReader<Brand> reader() {
        JdbcCursorItemReader<Brand> reader = new JdbcCursorItemReader<Brand>();
        reader.setDataSource(dataSource());
        reader.setSql("");
        reader.setRowMapper(new BrandRowMapper());

        return reader;
    }

    public class BrandRowMapper implements RowMapper<Brand> {


        @Override
        public Brand mapRow(ResultSet rs, int i) throws SQLException {
            Brand brand = new Brand();
            brand.setBrand_id(rs.getLong("brand_id"));
            brand.setBrand_name((rs.getString("brand_name")));
            brand.setBrand_sid((rs.getString("brand_sid")));
            brand.setDelete_status((rs.getBoolean("delete_status")));

            return brand;
        }
    }

    @Bean
    public BrandItemProcesser processor() {
        return new BrandItemProcesser();
    }

    @Bean
    public FlatFileItemWriter<Brand> writer() {
        FlatFileItemWriter<Brand> writer = new FlatFileItemWriter<Brand>();
        writer.setResource(new ClassPathResource("users.csv"));
        writer.setLineAggregator(new DelimitedLineAggregator<Brand>() {{
            setDelimiter(",");
            setFieldExtractor(new BeanWrapperFieldExtractor<>() {{
                setNames(new String[]{"brand_id", "brand_name", "brand_sid", "delete_status"});
            }});
        }});

        return writer;
    }

    public Step step1(){
        return stepBuilderFactory.get("step1").<Brand, Brand> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job exportBrandJob() {
        return jobBuilderFactory.get("exportBrandJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }
}
