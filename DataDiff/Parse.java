import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

public class Parse {
    public static void main (String[] args) {

        try {
            File xml = new File("DataSource1.xml");
            File dsv = new File("DataSource2.dsv");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xml);
            doc.getDocumentElement().normalize();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}