package bdma.ulb.tpcdi.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "TaxRate")
class TaxRate {

    @Id
    @Column(name = "TX_Id", columnDefinition = "VARCHAR(4) DEFAULT''" )
    String id

    @Column(name = "TX_Name", columnDefinition = "VARCHAR(50) DEFAULT''" )
    String name

    @Column(name = "TX_Rate", columnDefinition = "DECIMAL(6,5) UNSIGNED")
    Double rate
}
