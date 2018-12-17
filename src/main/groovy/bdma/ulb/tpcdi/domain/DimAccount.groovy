package bdma.ulb.tpcdi.domain

import bdma.ulb.tpcdi.domain.enums.BatchId
import bdma.ulb.tpcdi.domain.enums.Status
import bdma.ulb.tpcdi.domain.enums.TaxStatus

import javax.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "DimAccount")
class DimAccount {

    @Id
    @Column(name = "SK_AccountId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @Column(name = "AccountId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer accountId

    @Column(name = "SK_BrokerId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skBrokerId

    @Column(name = "SK_CustomerId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skCustomerId

    @Column(name = "Status", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT ''")
    Status status

    @Column(name = "AccountDesc", columnDefinition = "VARCHAR(50) DEFAULT ''")
    String accountDesc

    @Column(name = "TaxStatus", nullable = false, columnDefinition = " INT(1) UNSIGNED")
    TaxStatus taxStatus

    @Column(name = "IsCurrent", nullable = false, columnDefinition = " TINYINT(1) UNSIGNED ")
    boolean isCurrent

    @Column(name = "BatchId", nullable = false, columnDefinition = " INT(5) UNSIGNED")
    BatchId batchId

    @Column(name = "EffectiveDate", nullable = false)
    LocalDate effectiveDate

    @Column(name = "EndDate", nullable = false)
    LocalDate endDate
}
