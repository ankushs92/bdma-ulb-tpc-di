package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.DimCustomer
import org.springframework.data.jpa.repository.JpaRepository

interface DimCustomerRepository extends JpaRepository<DimCustomer, Integer> {
}
