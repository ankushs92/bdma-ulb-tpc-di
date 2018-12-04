package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.DimSecurity
import org.springframework.data.jpa.repository.JpaRepository

interface DimSecurityRepository extends JpaRepository<DimSecurity, Integer> {


}
