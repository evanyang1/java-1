import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// ReferenceMapping(boolean active, String consumerType, String ct_code, String value)
public class Parse {
    public static void main (String[] args) {
        // use value as key (4th param) (ironic but this is closest to one to one)
        HashMap<String, ArrayList<ReferenceMapping>> map1 = new HashMap<>();
        HashMap<String, ArrayList<ReferenceMapping>> map2 = new HashMap<>();

        try {
            File xml = new File("DataSource1.xml");
            File dsv = new File("DataSource2.dsv"); 

            // dsv file
            BufferedReader br = new BufferedReader(new FileReader(dsv));
            String s;
            while ((s = br.readLine()) != null) {
                String[] parts = s.split("~");
                // ordering is based on constructor definition and dsv formatting
                ReferenceMapping rmapping = new ReferenceMapping(
                    stringNumberToBoolean(parts[3]),
                    parts[0],
                    parts[1],
                    parts[2]
                );
                // add to map
                String key = parts[2];
                if (map2.containsKey(key)) {
                    //append
                    ArrayList<ReferenceMapping> arr = map2.get(key);
                    arr.add(rmapping);
                    map2.put(key, arr);
                }

            }
            br.close();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xml);
            doc.getDocumentElement().normalize();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
    public static Boolean stringNumberToBoolean(String str) {
        // 0 -> false, true otherwise
        int n = Integer.parseInt(str);
        return (n == 0) ? false : true;
    }
}