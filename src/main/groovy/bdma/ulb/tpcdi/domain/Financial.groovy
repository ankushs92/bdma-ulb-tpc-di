package bdma.ulb.tpcdi.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "Financial")
class Financial {

    @Id
    @Column(name = "SK_CompanyId")
    Integer id

    @Column(name = "FI_Year", nullable = false, columnDefinition = "INT(4) UNSIGNED")
    Integer fiYear

    @Column(name = "FI_Qtr", nullable = false, columnDefinition = "TINYINT(1) UNSIGNED")
    Integer fiQtr

    @Column(name = "FI_Qtr_Start_Date", nullable = false, columnDefinition = "DATE")
    LocalDate startDate

    @Column(name = "FI_Revenue", nullable = false, columnDefinition = "DECIMAL(15,2)")
    Double revenue

    @Column(name = "FI_Net_Earn", nullable = false, columnDefinition = "DECIMAL(15,2)")
    Double fiNetEarn

    @Column(name = "FI_Basic_Eps", nullable = false, columnDefinition = "DECIMAL(10,2)")
    Double fiBasicEps

    @Column(name = "FI_Dilut_Eps", nullable = false, columnDefinition = "DECIMAL(10,2)")
    Double fiDilutEps

    @Column(name = "FI_Margin", nullable = false, columnDefinition = "DECIMAL(10,2)")
    Double fiMargin

    @Column(name = "FI_Inventory", nullable = false, columnDefinition = "DECIMAL(15,2)")
    Double fiInventory

    @Column(name = "FI_Assets", nullable = false, columnDefinition = "DECIMAL(15,2)")
    Double fiAssets

    @Column(name = "FI_Liability", nullable = false, columnDefinition = "DECIMAL(15,2)")
    Double fiLiability

    @Column(name = "FI_Out_Basic", nullable = false, columnDefinition = "INT(12)")
    Integer fiOutBasic

    @Column(name = "FI_Out_Dilut", nullable = false, columnDefinition = "INT(12)")
    Integer fiOutDilut

}
