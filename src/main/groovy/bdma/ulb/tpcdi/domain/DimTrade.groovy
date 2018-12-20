package bdma.ulb.tpcdi.domain

import bdma.ulb.tpcdi.domain.enums.BatchId
import org.springframework.data.domain.Persistable

import javax.persistence.*

@Entity
@Table(name = "DimTrade")
class DimTrade implements Persistable<Integer> {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @Column(name = "TradeID", nullable = false)
    Integer tradeId

    @Column(name = "SK_BrokerID")
    Integer skBrokerId

    @Column(name = "SK_CreateDateID", nullable = false)
    Integer skCreateDateId

    @Column(name = "SK_CreateTimeID", nullable = false)
    Integer skCreateTimeId

    @Column(name = "SK_CloseDateID")
    Integer skCloseDateId

    @Column(name = "SK_CloseTimeID")
    Integer skCloseTimeId

    @Column(name = "Status", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT ''")
    String status

    @Column(name = "Type", nullable = false, columnDefinition = "VARCHAR(12) DEFAULT ''")
    String type

    @Column(name = "CashFlag", nullable = false, columnDefinition = " TINYINT(1) UNSIGNED ")
    boolean isCash

    @Column(name = "SK_SecurityID", nullable = false)
    Integer skSecurityId

    @Column(name = "SK_CompanyId", nullable = false)
    Integer skCompanyId

    @Column(name = "Quantity", nullable = false, columnDefinition = "INT(6) UNSIGNED")
    Integer quantity

    @Column(name = "BidPrice", nullable = false, columnDefinition = "DECIMAL(8,2)")
    Double bidPrice

    @Column(name = "SK_CustomerId", nullable = false)
    Integer skCustomerId

    @Column(name = "SK_AccountID", nullable = false)
    Integer skAccountId

    @Column(name = "ExecutedBy", nullable = false, columnDefinition = "VARCHAR(64) DEFAULT ''")
    String executedBy

    @Column(name = "TradePrice", columnDefinition = "DECIMAL(8,2)")
    Double tradePrice

    @Column(name = "Fee", columnDefinition = "DECIMAL(10,2) UNSIGNED")
    Double fee

    @Column(name = "Commission", columnDefinition = "DECIMAL(10,2) UNSIGNED")
    Double commission

    @Column(name = "Tax", columnDefinition = "DECIMAL(10,2) UNSIGNED")
    Double tax

    @Column(name = "BatchId", nullable = false, columnDefinition = "INT(5) UNSIGNED")
    BatchId batchId
//    TradePrice NUM(8,2) Unit priceat whichthesecuritywastraded.
//
//    Fee NUM(10,2) Fee charged for placing this trade request
//
//    Commission NUM(10,2) Commission earned on thistrade
//
//    Tax NUM(10,2)
    @Override
    boolean isNew() {
        true
    }
}
