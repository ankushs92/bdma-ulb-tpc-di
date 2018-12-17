package bdma.ulb.tpcdi.domain

import bdma.ulb.tpcdi.domain.enums.BatchId
import groovy.transform.ToString
import org.springframework.data.domain.Persistable

import javax.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "DimBroker")
@ToString(includeNames = true)
class DimBroker implements Persistable<Integer> {

    @Id
    @Column(name = "SK_BrokerId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @Column(name = "BrokerId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer brokerId

    @Column(name = "ManagerId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer managerId

    @Column(name = "FirstName", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
    String firstName

    @Column(name = "LastName", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
    String lastName

    @Column(name = "MiddleInitial", nullable = false, columnDefinition = "VARCHAR(1) DEFAULT ''")
    String middleInitial

    @Column(name = "Branch", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
    String branch

    @Column(name = "Office", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
    String office

    @Column(name = "Phone", nullable = false, columnDefinition = "VARCHAR(14) DEFAULT ''")
    String phone

    @Column(name = "IsCurrent", nullable = false, columnDefinition = "TINYINT(1) UNSIGNED")
    boolean isCurrent

    @Column(name = "BatchId", nullable = false, columnDefinition = "INT(5) UNSIGNED")
    BatchId batchId

    @Column(name = "EffectiveDate", nullable = false)
    LocalDate effectiveDate

    @Column(name = "EndDate", nullable = false)
    LocalDate endDate


    @Override
    boolean isNew() {
        true
    }
}
