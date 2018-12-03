package bdma.ulb.tpcdi.util

class FileParser {

    static List<String[]> parse(String location, String delim) {
        List<String[]> records = []
        new File(location).eachLine { line ->
            records << line.split(delim)
        }
        records
    }

}
