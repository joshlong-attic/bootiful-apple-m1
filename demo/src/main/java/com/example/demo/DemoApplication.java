package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> ready(
            DatabaseClient dbc,
            CustomerRepository cr) {
        return event -> {

            var sql = """
                    create table CUSTOMER(
                        id serial primary key,
                        name varchar(255) not null
                    )
                    """;

            var ddl = dbc.sql(sql).fetch().rowsUpdated();

            var names = Flux.just("A", "B", "C")
                    .map(name -> new Customer(null, name))
                    .flatMap(cr::save);

            var all = cr.findAll();

            ddl.thenMany(names).thenMany(all).subscribe(System.out::println);
        };
    }
}


interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
}

@Data
@AllArgsConstructor
class Customer {

    @Id
    public Integer id;
    public String name;
}