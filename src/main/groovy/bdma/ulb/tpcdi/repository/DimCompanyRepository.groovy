package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.DimCompany
import org.springframework.data.jpa.repository.JpaRepository

interface DimCompanyRepository extends JpaRepository<DimCompany, Integer> {

}
