import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//import ReferenceMapping.SortReferenceMapping;

import java.io.PrintWriter; // write to txt file

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.XMLConstants;
//import ReferenceMapping;

// ReferenceMapping(boolean active, String consumerType, String ct_code, String value)
public class Parse {
    public static void main (String[] args) {
        
        HashMap<String, ArrayList<ReferenceMapping>> map1 = new HashMap<>();
        HashMap<String, ArrayList<ReferenceMapping>> map2 = new HashMap<>();

        try {
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

                    Boolean active = stringYesOrNoToBoolean(element.getElementsByTagName("ref:activeId").item(0).getTextContent());
                    String consumerType = element.getElementsByTagName("ref:consumerType").item(0).getTextContent();
                    String ct_code = element.getElementsByTagName("ref:code").item(0).getTextContent();
                    String value = element.getElementsByTagName("ref:value").item(0).getTextContent();
                    
                    ReferenceMapping rm = new ReferenceMapping(
                        active,
                        consumerType,
                        ct_code,
                        value
                    );
                    ArrayList<ReferenceMapping> arr;
                    String key = ct_code; // ct_code as key
                    if (map1.containsKey(key)) {
                        //append
                        arr = map1.get(key);
                    } else arr = new ArrayList<>();
                    arr.add(rm);
                    map1.put(key, arr);

                }
            }

            // dsv file
            File dsv = new File("DataSource2.dsv"); 
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
                String key = parts[1];
                ArrayList<ReferenceMapping> arr;
                if (map2.containsKey(key)) {
                    //append
                    arr = map2.get(key);
                } else arr = new ArrayList<>();
                arr.add(rmapping);
                map2.put(key, arr);


            }
            br.close();   
        } catch(Exception e) {
            e.printStackTrace();
        }

        try {
            PrintWriter out = new PrintWriter("output.txt");
            out.println("======================================");
            HashSet<ReferenceMapping> setDuplicates1 = new HashSet<>();
            int numDuplicates1 = 0;
            int numElements1 = 0;
            for (HashMap.Entry<String, ArrayList<ReferenceMapping>> entry : map1.entrySet()) {
                numElements1 += entry.getValue().size();
                if (entry.getValue().size() > 1) {
                    // check for duplicates
                    int curSizeHashSet = setDuplicates1.size();
                    ArrayList<ReferenceMapping> copy = (ArrayList<ReferenceMapping>) entry.getValue();
                    //Collections.sort(copy, null);
                    for (int i = 0; i < copy.size() - 1; i++) {
                        for (int j = i + 1; j < copy.size(); j++) {
                            if (copy.get(i).equals( copy.get(j) )) {
                                setDuplicates1.add(copy.get(i));
                            }
                        }
                    }
                    numDuplicates1 += (setDuplicates1.size() - curSizeHashSet);
                }
            }
            out.println("------ Data Source 1 Statistics ------");
            out.println("Total number of ref mapping items: " + numElements1);
            out.println("Total duplicate items: " + numDuplicates1);


            out.println("======================================");
            out.println("------ Data Source 2 Statistics ------");
            for (HashMap.Entry<String, ArrayList<ReferenceMapping>> entry : map2.entrySet()) {
                System.out.println(entry.getKey());
                System.out.println(entry.getValue());

            }

            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }


    public static Boolean stringNumberToBoolean(String str) {
        // 0 -> false, true otherwise
        int n = Integer.parseInt(str.strip());
        return (n == 0) ? false : true;
    }
    public static Boolean stringYesOrNoToBoolean(String str) {
        return (str.equals("Y")) ? true : false;
    }
}