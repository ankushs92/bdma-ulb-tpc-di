package bdma.ulb.tpcdi.domain

import bdma.ulb.tpcdi.domain.enums.BatchId

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "FactWatches")
class FactWatches {

    @Id
    @Column(name = "SK_FactCashBalanceId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @Column(name = "SK_CustomerId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skCustomerId

    @Column(name = "SK_SecurityID", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skSecurityId

    @Column(name = "SK_DateID_DatePlaced", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skDatePlacedId

    @Column(name = "SK_DateID_DateRemoved", columnDefinition = "INT(11) UNSIGNED")
    Integer skDateRemovedId

    @Column(name = "BatchId", nullable = false, columnDefinition = "INT(5) UNSIGNED")
    BatchId batchId


}
