package pl.dk.optimistic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.dk.optimistic.base.BaseRepositoryImpl;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)
public class OptimisticApplication {

    public static void main(String[] args) {
        SpringApplication.run(OptimisticApplication.class, args);
    }

}
