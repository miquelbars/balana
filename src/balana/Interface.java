package balana;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.balana.Indenter;
import org.wso2.balana.ctx.xacml3.Result;

/**
 *
 * @author micky
 */
public class Interface {   
    
    public static void printResult(OutputStream output, Indenter indenter, Set results) {
        PrintStream out = new PrintStream(output);
        String indent = indenter.makeString();
        out.println(indent + "<Response>");
        Iterator it = results.iterator();
        indenter.in();
        while (it.hasNext()) {
            Result result = (Result)(it.next());
            out.append(result.encode());
        }
        indenter.out();
        out.println(indent + "</Response>");
    }
    
    public static String getXMLFromFilePath(String path) throws TransformerException, Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        StringWriter writer = new StringWriter();
        
        DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = builder.newDocumentBuilder();
        Document document = docBuilder.parse(path);
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        
        return writer.getBuffer().toString().replaceAll("\n|\r", "");
    }
    
    /**
     * Creates DOM representation of the XACML request
     *
     * @param response  XACML request as a String object
     * @return XACML request as a DOM element
     */
    public static Element getXacmlResponse(String response) {
        ByteArrayInputStream inputStream;
        DocumentBuilderFactory dbf;
        Document doc;

        inputStream = new ByteArrayInputStream(response.getBytes());
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        try {
            doc = dbf.newDocumentBuilder().parse(inputStream);
        } catch (Exception e) {
            System.err.println("DOM of request element can not be created from String");
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
               System.err.println("Error in closing input stream of XACML response");
            }
        }
        return doc.getDocumentElement();
    }
    
    /**
     * Elimina todos los ficheros que hay dentro del directorio resources/output
     */
    public static void deleteFiles(){
        File directory = new File((new File(".")).getAbsolutePath() + File.separator + "src" + File.separator + "resources" + File.separator + "output");
            // Get all files in directory
            File[] files = directory.listFiles();
            for (File file : files){
                // Delete each file
                if (!file.delete()){
                // Failed to delete file
                System.out.println("Failed to delete "+file);
            }
        } 
    }
}
