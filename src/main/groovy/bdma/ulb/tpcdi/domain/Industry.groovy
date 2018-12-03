package bdma.ulb.tpcdi.domain

import org.springframework.data.domain.Persistable

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "Industry")
class Industry implements Persistable<String> {

    @Id
    @Column(name = "IN_Id", columnDefinition = "VARCHAR(2) DEFAULT ''")
    String id

    @Column(name = "IN_Name", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
    String name

    @Column(name = "IN_Sc_Id", nullable = false, columnDefinition = "VARCHAR(4) DEFAULT ''")
    String scId

    @Override
    boolean isNew() {
        true
    }
}
