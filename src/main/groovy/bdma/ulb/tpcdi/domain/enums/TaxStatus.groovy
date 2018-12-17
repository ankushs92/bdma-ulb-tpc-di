package bdma.ulb.tpcdi.domain.enums

enum TaxStatus {

    ZERO(0),
    ONE(1),
    TWO(2)
    final int code

    TaxStatus(int code) {
        this.code = code
    }

    static TaxStatus from(Integer code) {
        if(code == null) return ZERO
        for(val in values()) {
            if(val.code == code) {
                return val
            }
        }
    }

}
