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
        // data extraction
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

        // output 
        try {
            PrintWriter out = new PrintWriter("output.txt");
            out.println("======================================");
            HashSet<ReferenceMapping> setDuplicates1 = new HashSet<>();
            int numDuplicates1 = 0, numUniqueCodes1 = 0;
            int numElements1 = 0;
            for (HashMap.Entry<String, ArrayList<ReferenceMapping>> entry : map1.entrySet()) {
                numElements1 += entry.getValue().size();
                if (entry.getValue().size() > 1) {
                    // check for duplicates
                    int curSizeHashSet = setDuplicates1.size();
                    ArrayList<ReferenceMapping> copy = (ArrayList<ReferenceMapping>) entry.getValue();
                    Collections.sort(copy, (ReferenceMapping r1, ReferenceMapping r2) -> r1.hashCode() - r2.hashCode());
                    for (int i = 0; i < copy.size() - 1; i++) {
                        // for (int j = i + 1; j < copy.size(); j++) {
                        //     if (copy.get(i).equals( copy.get(j) )) {
                        //         setDuplicates1.add(copy.get(i));
                        //     }
                        // }
                        if (copy.get(i).equals(copy.get(i + 1))) {
                            setDuplicates1.add(copy.get(i));
                        }
                    }
                    numDuplicates1 += (setDuplicates1.size() - curSizeHashSet);
                }
                // check elements not in data source 2
                if ( !map2.containsKey(entry.getKey()) ){
                    numUniqueCodes1++;
                } 
            }
            out.println("------ Data Source 1 Statistics ------");
            out.println("Total number of ref mapping items: " + numElements1);
            out.println("Total duplicate items: " + numDuplicates1);
            out.println("Number of codes only in Data Source 1: " + numUniqueCodes1);


            out.println("======================================");
            out.println("------ Data Source 2 Statistics ------");
            HashSet<ReferenceMapping> setDuplicates2 = new HashSet<>();
            int numDuplicates2 = 0, numUniqueCodes2 = 0;
            int numElements2 = 0;
            for (HashMap.Entry<String, ArrayList<ReferenceMapping>> entry : map2.entrySet()) {
                numElements2 += entry.getValue().size();
                if (entry.getValue().size() > 1) {
                    // check for duplicates
                    int curSizeHashSet = setDuplicates2.size();
                    ArrayList<ReferenceMapping> copy = (ArrayList<ReferenceMapping>) entry.getValue();
                    Collections.sort(copy, (ReferenceMapping r1, ReferenceMapping r2) -> r1.hashCode() - r2.hashCode());
                    for (int i = 0; i < copy.size() - 1; i++) {
                        if (copy.get(i).equals(copy.get(i + 1))) {
                            setDuplicates2.add(copy.get(i));
                        }
                    }
                    numDuplicates2 += (setDuplicates2.size() - curSizeHashSet);
                }
                // check elements not in data source 1
                if ( !map1.containsKey(entry.getKey()) ){
                    numUniqueCodes2++; 
                } 
            }
            out.println("Total number of ref mapping items: " + numElements2);
            out.println("Total duplicate items: " + numDuplicates2);
            out.println("Number of codes only in Data Source 1: " + numUniqueCodes2);

            out.println("\n**************************************");
            out.println("Detailed Report: \n");
            out.println("======================================");
            // print out same ct_code but different ActiveID AND value, sorted by CT number (1 or 2)
            ArrayList<ReferenceMapping> sameCodeDifferentActiveIDAndValue1 = new ArrayList<ReferenceMapping>();
            ArrayList<ReferenceMapping> sameCodeDifferentActiveIDAndValue2 = new ArrayList<ReferenceMapping>();
            for (HashMap.Entry<String, ArrayList<ReferenceMapping>> entry : map1.entrySet()) {
                if ( map2.containsKey(entry.getKey())) {
                    ArrayList<ReferenceMapping> ar1 = entry.getValue();
                    ArrayList<ReferenceMapping> ar2 = map2.get(entry.getKey());
                    ReferenceMapping rm1 = ar1.get(0); // if more than 1 element, it's a duplicate anyways
                    ReferenceMapping rm2 = ar2.get(0);
                    if ( !rm1.getValue().equals(rm2.getValue()) && rm1.getActive() != rm2.getActive()) {
                        if (rm1.getConsumerType().equals("CT1")) {
                            sameCodeDifferentActiveIDAndValue1.add(0, rm1);
                            sameCodeDifferentActiveIDAndValue2.add(0, rm2);
                        } else {
                            sameCodeDifferentActiveIDAndValue1.add(rm1);
                            sameCodeDifferentActiveIDAndValue2.add(rm2);
                        }
                        
                    }
                }
            }
            out.println("------ " + sameCodeDifferentActiveIDAndValue1.size() + " number of codes with both different value and activeId, list them grouped by consumerType");
            out.println("ConsumerType: CT1");
            int iter = 0;
            String tab = "    ";
            while (sameCodeDifferentActiveIDAndValue1.get(iter).getConsumerType().equals("CT1")) {
                out.println(tab + "Code: " +
                    sameCodeDifferentActiveIDAndValue1.get(iter).get_ct_code()
                );
                out.println(tab + tab + "DataSource1:" + tab + "activeId=" + 
                    (sameCodeDifferentActiveIDAndValue1.get(iter).getActive() ? "Y" : "N")
                );
                out.println(tab + tab + "DataSource2:" + tab + "activeId=" + 
                    (sameCodeDifferentActiveIDAndValue2.get(iter).getActive() ? "Y" : "N")
                );
                out.println(tab + tab + "DataSource1:" + tab + "value=" + 
                    sameCodeDifferentActiveIDAndValue1.get(iter).getValue()
                );
                out.println(tab + tab + "DataSource2:" + tab + "value=" + 
                    sameCodeDifferentActiveIDAndValue2.get(iter).getValue()
                );
                iter++;
            }
            out.println("ConsumerType: CT2");
            for (; iter < sameCodeDifferentActiveIDAndValue1.size(); iter++) {
                out.println(tab + "Code: " +
                    sameCodeDifferentActiveIDAndValue1.get(iter).get_ct_code()
                );
                out.println(tab + tab + "DataSource1:" + tab + "activeId=" + 
                    (sameCodeDifferentActiveIDAndValue1.get(iter).getActive() ? "Y" : "N")
                );
                out.println(tab + tab + "DataSource2:" + tab + "activeId=" + 
                    (sameCodeDifferentActiveIDAndValue2.get(iter).getActive() ? "Y" : "N")
                );
                out.println(tab + tab + "DataSource1:" + tab + "value=" + 
                    sameCodeDifferentActiveIDAndValue1.get(iter).getValue()
                );
                out.println(tab + tab + "DataSource2:" + tab + "value=" + 
                    sameCodeDifferentActiveIDAndValue2.get(iter).getValue()
                );
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