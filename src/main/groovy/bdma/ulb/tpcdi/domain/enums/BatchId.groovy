package bdma.ulb.tpcdi.domain.enums

enum BatchId {

    INITIALIZATION(0),
    HISTORICAL_LOAD(1),
    INCREMENTAL_UPDATE_1(2),
    INCREMENTAL_UPDATE_2(3)

    final int code

    BatchId(int code) {
        this.code = code
    }

    static BatchId from(int code) {
        for(val in values()) {
            if(code == val.code) {
                return val
            }
        }
    }
}
