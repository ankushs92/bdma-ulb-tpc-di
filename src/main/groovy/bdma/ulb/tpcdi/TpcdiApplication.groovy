package bdma.ulb.tpcdi

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class TpcdiApplication {

    static void main(String[] args) {
        SpringApplication.run TpcdiApplication, args
    }

}
