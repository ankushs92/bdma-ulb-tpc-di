package bdma.ulb.tpcdi.util

import org.apache.commons.lang3.StringUtils

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

    static String toLowerCaseIfAnyMixedCase(String str) {
        if(hasText(str)) {
            if(!StringUtils.isAllLowerCase(str)) {
                return str.toLowerCase()
            }
        }
    }

}

