import java.util.Iterator;
import org.apache.jena.atlas.logging.LogCtl;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Derivation;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.ReasonerVocabulary;

public class RDFS {

    public static void main(String[] args) {
        LogCtl.setCmdLogging();
        Model esquema = RDFDataMgr.loadModel("esquema.ttl");
        Model datos = RDFDataMgr.loadModel("datos.ttl");
        
        Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
        reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_SIMPLE);
        reasoner.setDerivationLogging(true);
        
//        InfModel inferencia = ModelFactory.createInfModel(reasoner, esquema, datos);
        Reasoner reasonerScheme = reasoner.bindSchema(esquema);
        InfModel inferencia = ModelFactory.createInfModel(reasonerScheme, datos);

        StmtIterator iter = inferencia.listStatements();
        while(iter.hasNext()){
            Statement st = iter.nextStatement();
            System.out.println("---> " + PrintUtil.print(st));
            for (Iterator iter2 = inferencia.getDerivation(st); iter2.hasNext();) {
                Derivation deriv = (Derivation) iter2.next();
                System.out.println(deriv);
            }
        }
        
        Resource Lugo = inferencia.getResource("http://www.si2.com/si2/Lugo");
        iter = inferencia.listStatements(Lugo, RDF.type, (RDFNode)null);
        System.out.println("------------------");
        while(iter.hasNext()){
            Statement st = iter.nextStatement();
            System.out.println("---> " + PrintUtil.print(st));
        }
        System.out.println("------------------");
        
        ValidityReport validacion = inferencia.validate();
        if (validacion.isValid()) {
            System.out.println("Todo OK");
        }else{
            System.out.println("ERROR.");
            Iterator<ValidityReport.Report> reports = validacion.getReports();
            while(reports.hasNext()){
                System.out.println(reports.next());
            }
        }
    }
    
}
