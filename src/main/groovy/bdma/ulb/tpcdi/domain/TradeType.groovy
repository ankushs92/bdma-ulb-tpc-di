package bdma.ulb.tpcdi.domain

import org.springframework.data.domain.Persistable

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "TradeType")
class TradeType implements Persistable<String> {


    @Id
    @Column(name = "TT_Id", columnDefinition = "VARCHAR(3) DEFAULT ''")
    String id

    @Column(name = "TT_Name", nullable = false, columnDefinition = "VARCHAR(12) DEFAULT ''")
    String name

    @Column(name = "TT_IsSell", nullable = false, columnDefinition = "TINYINT(1) UNSIGNED")
    boolean isSell

    @Column(name = "TT_IsMrkt", nullable = false, columnDefinition = "TINYINT(1) UNSIGNED")
    boolean isMarketOrder

    @Override
    boolean isNew() {
        true
    }
}
