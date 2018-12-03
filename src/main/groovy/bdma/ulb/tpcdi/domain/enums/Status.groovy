package bdma.ulb.tpcdi.domain.enums

enum Status {

    ACTIVE("active"),
    CLOSED("closed"),
    INACTIVE("inactive")

    final String code

    Status(String code) {
        this.code = code
    }

    static Status from(String code) {
        def result
        for(val in values()) {
            if(val.code.equalsIgnoreCase(code)) {
              result = val
              break
            }
        }
        result
    }
}