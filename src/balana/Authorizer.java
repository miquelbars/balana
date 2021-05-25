package balana;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Scanner;
import org.wso2.balana.Balana;
import org.wso2.balana.Indenter;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.ParsingException;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;

/**
 *
 * @author micky
 */
public class Authorizer {
    private static final String pathRequest = (new File(".")).getAbsolutePath() + File.separator + "src" + File.separator + "resources" + File.separator + "request" + File.separator + "XACMLRequest";
    private static final String pathResponse = (new File(".")).getAbsolutePath() + File.separator + "src" + File.separator + "resources" + File.separator + "output" + File.separator + "XACMLResponse";
    
    private static Balana balana;

    public static void main(String[] args) throws ParsingException, FileNotFoundException, Exception {
        Scanner reader = new Scanner(System.in);
        System.out.println("Seleccionar la XACMLRequest: 1, 2, 3, 4, 5");
        int requestNumber = reader.nextInt();
        
        Interface.deleteFiles();
        
        initBalana();
        PDPConfig pdpConfig = balana.getPdpConfig();
        PDP pdp = new PDP(new PDPConfig(pdpConfig.getAttributeFinder(), pdpConfig.getPolicyFinder(), null, true));    
        
        String requestFile = pathRequest + requestNumber + ".xml";
        String response = pdp.evaluate(Interface.getXMLFromFilePath(requestFile));
        ResponseCtx responseCtx = ResponseCtx.getInstance(Interface.getXacmlResponse(response));
        
        // Mostramos por consola el resultado
        System.out.println(responseCtx.encode());
        
        // Guardamos en XML el resultado
        String pathToSave = pathResponse + "PolicyRequest" + requestNumber;
        OutputStream outputStream = new FileOutputStream(pathToSave);
        Interface.printResult(outputStream, new Indenter(), responseCtx.getResults());
    }
    
    private static void initBalana(){
        try {
            // using file based policy repository. so set the policy location as system property
            String policyLocation = (new File(".")).getCanonicalPath() + File.separator + "src" + File.separator + "resources" + File.separator + "policy";
            System.setProperty(FileBasedPolicyFinderModule.POLICY_DIR_PROPERTY, policyLocation);
        } catch (IOException e) {
            System.err.println("Can not locate policy repository");
        }
        // create default instance of Balana
        balana = Balana.getInstance();
    }
}
