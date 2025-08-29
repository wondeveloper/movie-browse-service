package com.vivek.imdb.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id; // <-- Spring Data annotation, not JPA
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Getter @Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
@Builder
@Table("movie")
public class Movie implements Persistable<String> {

    @Id
    private String id;      // use String if you store UUID as text; otherwise UUID type works too
    private String title;

    @Column("release_year")
    private String releaseYear;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;
    @Transient
    private boolean isNew = true;   // mark new by default for creations

    @Override
    public boolean isNew() {
        return isNew || id == null;
    }
}