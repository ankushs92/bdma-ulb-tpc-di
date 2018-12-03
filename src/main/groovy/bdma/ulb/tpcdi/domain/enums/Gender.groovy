package bdma.ulb.tpcdi.domain.enums

import bdma.ulb.tpcdi.util.Assert

enum Gender {
    U,
    M,
    F

    static Gender from(String code) {
        Assert.notEmptyString(code, "gender code cannot ne null or empty")
        def result
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
        result
    }
}
