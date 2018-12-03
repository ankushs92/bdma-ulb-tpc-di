package bdma.ulb.tpcdi.util

class FileParser {

    static List<String[]> parse(String location, String delim) {
        List<String[]> records = []
        new File(location).eachLine { line ->
            // The second param was added because the default split() function in Groovy was ignoring
            // the trailing empty strings in the end
            // For example, it was converting "3552,571,McNally,Minnie,E,314,TIjsqqkuCYQPiDTOKxRIFg,," to [[3552, 571, McNally, Minnie, E, 314, TIjsqqkuCYQPiDTOKxRIFg]
            records << line.split(delim, Integer.MAX_VALUE)
        }
        records
    }

    public static void main(String[] args) {
        def s = "3552,571,McNally,Minnie,E,314,TIjsqqkuCYQPiDTOKxRIFg,,"
        println s.split(",", 222)
    }
}
