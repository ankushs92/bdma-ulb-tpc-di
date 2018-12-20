package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.DimAccount
import org.springframework.data.jpa.repository.JpaRepository

interface DimAccountRepository extends JpaRepository<DimAccount, Integer> {

}
