package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.TaxRate
import org.springframework.data.jpa.repository.JpaRepository

interface TaxRateRepository extends JpaRepository<TaxRate, Integer> {
}
