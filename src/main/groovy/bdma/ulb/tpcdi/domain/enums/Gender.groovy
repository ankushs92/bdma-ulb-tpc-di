package bdma.ulb.tpcdi.domain.enums

import bdma.ulb.tpcdi.util.Assert
import bdma.ulb.tpcdi.util.Strings

enum Gender {
    U,
    M,
    F

    static Gender from(String code) {
        def result = U
        if(Strings.hasText(code)) {
            switch (code) {
                case "m" :
                case "M" :
                case "male" :
                case "Male" :
                    result = M
                    break
                case "F" :
                case "f" :
                case "female" :
                case "Female" :
                    result = F
                    break
                default :
                    result = U
            }
        }
        result
    }
}
