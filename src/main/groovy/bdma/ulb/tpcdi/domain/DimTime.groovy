package bdma.ulb.tpcdi.domain

import org.springframework.data.domain.Persistable

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import java.time.LocalTime

@Entity
@Table(name = "DimTime")
class DimTime implements Persistable<Integer> {

    @Id
    @Column(name ="SK_TimeID")
    Integer id

    @Column(name = "TimeValue", nullable = false)
    LocalTime time

    @Column(name = "HourId", nullable = false, columnDefinition = "INT(2) UNSIGNED")
    Integer hourId

    @Column(name = "HourDesc", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT ''")
    String hourDesc

    @Column(name = "MinuteId", nullable = false, columnDefinition = "INT(2) UNSIGNED")
    Integer minuteId

    @Column(name = "MinuteDesc", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT ''")
    String minuteDesc

    @Column(name = "SecondId", nullable = false, columnDefinition = "INT(2) UNSIGNED")
    Integer secondId

    @Column(name = "SecondDesc", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT ''")
    String secondDesc

    @Column(name = "MarketHoursFlag", columnDefinition = "TINYINT(1) UNSIGNED")
    boolean isMarketHours

    @Column(name = "OfficeHoursFlag", columnDefinition = "TINYINT(1) UNSIGNED")
    boolean isOfficeHours

    @Override
    boolean isNew() {
        true
    }
}
