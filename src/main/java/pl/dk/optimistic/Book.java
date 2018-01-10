package pl.dk.optimistic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Getter
@NoArgsConstructor
@Entity
@OptimisticLocking
class Book {

    @Id
    private String title;

    @Setter
    private String rentedBy;

    @Version
    private Long entityVersion;

    Book(String title) {
        this.title = title;
    }

}
