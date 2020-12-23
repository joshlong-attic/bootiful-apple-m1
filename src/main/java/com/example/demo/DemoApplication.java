package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    private final String message = "Hello, world!";

    @Bean
    RouterFunction<ServerResponse> routes(CustomerRepository cr) {
        return route()
                .GET("/hello", r -> ServerResponse.ok().bodyValue(this.message))
                .GET("/customers", r -> ServerResponse.ok().body(cr.findAll(), Customer.class))
                .build();
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> ready() {
        return event -> System.out.println(this.message);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> dbReady(DatabaseClient dbc, CustomerRepository customerRepository) {
        return event -> {

            var sql =
                    """
                       create table CUSTOMER(
                           id serial primary key , 
                           name varchar(255) not null
                       ) 
                    """;

            var count = dbc.sql(sql).fetch().rowsUpdated();

            var names = Flux
                    .just("A", "B", "C")
                    .map(name -> new Customer(null, name))
                    .flatMap(customerRepository::save);
            var all = customerRepository.findAll();

            count
                    .thenMany(names)
                    .thenMany(all)
                    .subscribe(System.out::println);

        };
    }
}

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {
    @Id
    private Integer id;
    private String name;
}
