package bdma.ulb.tpcdi.util

class Strings {

    static final String NULL = "NULL"
    /**
     * Validate if a String has text in it. Whitespaces are not considered valid text
     * @param text The String that has to be validated
     * @return false if the String does not have text
     */
    static boolean hasText(String text){
        if(!text){
            return false
        }
        for(char ch in text.toCharArray()){
            if(!Character.isWhitespace(ch)){
                return true
            }
        }
        return false
    }


}

