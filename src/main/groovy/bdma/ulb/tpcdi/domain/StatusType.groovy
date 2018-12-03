package bdma.ulb.tpcdi.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "StatusType")
class StatusType {

    @Id
    @Column(name = "ST_Id", columnDefinition = "VARCHAR(4) DEFAULT ''")
    String id

    @Column(name = "ST_Name", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT ''")
    String name

}
