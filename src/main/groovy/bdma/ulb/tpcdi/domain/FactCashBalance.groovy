package bdma.ulb.tpcdi.domain

import bdma.ulb.tpcdi.domain.enums.BatchId

import javax.persistence.*

@Entity
@Table(name = "FactCashBalance")
class FactCashBalance {

    @Id
    @Column(name = "SK_FactCashBalanceId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @Column(name = "SK_CustomerId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skCustomerId

    @Column(name = "SK_AccountId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skAccountId

    @Column(name = "SK_DateId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skDateId

    @Column(name = "Cash", nullable = false, columnDefinition = "DECIMAL(15,2)")
    Double cash

    @Column(name = "BatchId", nullable = false, columnDefinition = "INT(5) UNSIGNED")
    BatchId batchId


}
