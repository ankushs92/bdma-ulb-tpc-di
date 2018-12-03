package bdma.ulb.tpcdi.domain

import org.springframework.data.domain.Persistable

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "DimDate")
class DimDate implements Persistable<Integer> {


    @Id
    @Column(name ="SK_DateId")
    Integer id

    @Column(name = "DateValue", nullable = false)
    LocalDate date

    @Column(name = "DateDesc", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT ''")
    String desc

    @Column(name = "CalenderYearId", nullable = false, columnDefinition = "INT(4) UNSIGNED")
    Integer calenderYearId

    @Column(name = "CalenderYearDesc", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT ''")
    String calenderYearDesc

    @Column(name = "CalenderQtrlId", nullable = false, columnDefinition = "INT(5) UNSIGNED")
    Integer calenderQtrlId

    @Column(name = "CalenderQtrlDesc", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT ''")
    String calenderQtrlDesc

    @Column(name ="CalenderMonthId", nullable = false, columnDefinition = "INT(6) UNSIGNED ")
    Integer calenderMonthId

    @Column(name = "CalenderMonthDesc", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT ''")
    String calenderMonthDesc

    @Column(name = "DayOfWeekNum", nullable = false, columnDefinition = " INT(1) UNSIGNED")
    Integer dayOfWeek

    @Column(name = "DayOfWeekDesc", nullable = false, columnDefinition =  "VARCHAR(20) DEFAULT ''")
    String dayOfWeekDesc

    @Column(name ="FiscalYearID", nullable = false, columnDefinition = "INT(4) UNSIGNED")
    Integer fiscalYearNum

    @Column(name = "FiscalYearDesc", nullable = false, columnDefinition =  "VARCHAR(20) DEFAULT ''")
    String fiscalYearDesc

    @Column(name = "FiscalQtrID", nullable = false, columnDefinition =  "INT(5) UNSIGNED")
    Integer fiscalQtrlId // e.g. 20051

    @Column(name = "FiscalQtrDesc", nullable = false, columnDefinition =  "VARCHAR(20) DEFAULT ''")
    String fiscalQtrlDesc // e.g. “2005 Q1”

    @Column(name = "Holiday", columnDefinition = "TINYINT(1) UNSIGNED")
    boolean isHoliday

    @Override
    boolean isNew() {
        return true
    }



}
