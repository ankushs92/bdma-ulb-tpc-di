package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.Financial
import org.springframework.data.jpa.repository.JpaRepository

interface FinancialRepository extends JpaRepository<Financial, Integer> {
}
