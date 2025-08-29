package com.vivek.imdb.util;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.github.javafaker.Faker;
import com.vivek.imdb.entity.Movie;
import com.vivek.imdb.repository.PaginationAndSearchingRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Year;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class MovieCreationRunner implements CommandLineRunner {

    private final PaginationAndSearchingRepository repository;

    private static final Logger log = LoggerFactory.getLogger(MovieCreationRunner.class);
    private static final Faker faker = Faker.instance();

    @Value("${app.seed.movie.count:100}")
    private int totalMovie;

    @Value("${app.seed.movie.year:1985}")
    private int startYear;

    @Override
    public void run(String... args) throws Exception {

        int endYear = Year.now().getValue();
        repository.count()
                        .flatMapMany(count -> {
                            if (count > 0) return Flux.empty();
                            log.info("Inserting the data -{}", count);
                            return Flux.range(0, totalMovie)
                                    .delayElements(Duration.ofMillis(500))
                                    .map(p -> Movie.builder()
                                            .id(UUID.randomUUID().toString())
                                            .title(faker.artist().name())
                                            .isNew(true)
                                            .releaseYear(String.valueOf(faker.number().numberBetween(startYear, endYear)))
                                            .build());
                        }).transform(this.repository::saveAll)
                .doOnNext(m -> log.debug("Inserted: {}", m.getTitle()))
                .doOnError(e -> log.error("Seeding failed", e))
                .doOnComplete(() -> log.info("Seeding complete."))
                .subscribe(); // fire-and-forget so app can finish starting


    }

    @Bean
    public Jdk8Module jdk8Module() {
        return new Jdk8Module();
    }
}
