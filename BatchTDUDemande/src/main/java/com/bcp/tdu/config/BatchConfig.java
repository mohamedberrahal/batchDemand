package com.bcp.tdu.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.bcp.tdu.itemwriter.ElasticsearchItemWriter;
import com.bcp.tdu.model.Person;
import com.bcp.tdu.repository.EsPersonRepository;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
  @Autowired
  private EsPersonRepository personRepository;

  @Bean
  public ItemReader<Person> orderItemReader(EntityManagerFactory entityManagerFactory){
    JpaPagingItemReader<Person> reader = new JpaPagingItemReader<Person>();
    String sqlQuery = "select * from person";
    try {
      JpaNativeQueryProvider<Person> queryProvider = new JpaNativeQueryProvider<Person>();
      queryProvider.setSqlQuery(sqlQuery);
      queryProvider.setEntityClass(Person.class);
      queryProvider.afterPropertiesSet();
      reader.setEntityManagerFactory(entityManagerFactory);
      reader.setPageSize(10000);
      reader.setQueryProvider(queryProvider);
      reader.afterPropertiesSet();
      reader.setSaveState(true);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return reader;
  }

  @Bean
  public ElasticsearchItemWriter itemWriter(){
    return new ElasticsearchItemWriter(personRepository);
  }

  @Bean
  public Step step(StepBuilderFactory stepBuilderFactory,
           ItemReader itemReader,
           ItemWriter itemWriter){
    return stepBuilderFactory
        .get("step1")
        .chunk(10000)
        .reader(itemReader)
        .writer(itemWriter)
        .build();
  }

  @Bean
  public Job job(JobBuilderFactory jobBuilderFactory, Step step){
    return jobBuilderFactory
        .get("importJob")
        .incrementer(new RunIdIncrementer())
        .flow(step)
        .end()
        .build();
  }

  /**
   * spring batch dataSource
   * @param dataSource
   * @param manager
   * @return
   */
  @Bean
  public JobRepository jobRepository(DataSource dataSource, PlatformTransactionManager manager){
    JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
    jobRepositoryFactoryBean.setDataSource(dataSource);
    jobRepositoryFactoryBean.setTransactionManager(manager);
    jobRepositoryFactoryBean.setDatabaseType("postgres");
    try {
      return jobRepositoryFactoryBean.getObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}