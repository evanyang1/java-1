import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.XMLConstants;

// ReferenceMapping(boolean active, String consumerType, String ct_code, String value)
public class Parse {
    public static void main (String[] args) {
        // use value as key (4th param) (ironic but this is closest to one to one)
        HashMap<String, ArrayList<ReferenceMapping>> map1 = new HashMap<>();
        HashMap<String, ArrayList<ReferenceMapping>> map2 = new HashMap<>();

        try {
            File xml = new File("DataSource1.xml");
            File dsv = new File("DataSource2.dsv"); 

            // xml file
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            // optional, but recommended
            // process XML securely, avoid attacks like XML External Entities (XXE)
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File("DataSource1.xml"));
            doc.getDocumentElement().normalize();

            NodeList xmlNodeList = doc.getElementsByTagName("ref:refMapping");
            for (int i = 0; i < xmlNodeList.getLength(); i++) {
                Node node = xmlNodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                }
            }

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