package Model;

import org.apache.jena.atlas.logging.LogCtl;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.ReasonerVocabulary;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;

public class Grafo {

    private Reasoner reasonerScheme;

    public static final int TURTTLE = 0;
    public static final int XML = 1;
    public static final int NTRIPLE = 2;

    public static final int RDFSREASONER = 0;
    public static final int SIMPLEREASONER = 1;

    static final String si2 = "http://www.si2.com/si2/";
    private static final String aemet = "http://aemet.linkeddata.es/ontology/";
    private static final String geo = "http://www.w3.org/2003/01/geo/wgs84_pos#";

    private static final String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
    private static final String xsd = "http://www.w3.org/2001/XMLSchema#";


    private Model modelo;
    private Toponimos toponimos = Toponimos.getInstance();
    private final Property indicativo;
    private final Property indsinop;
    private final Property province;
    private final Property latitud;
    private final Property altitud;
    private final Property longitud;
    private final Property nombre;

    public Grafo() {
        LogCtl.setCmdLogging();
        modelo = ModelFactory.createDefaultModel();
        modelo.setNsPrefix("si2", si2);
        modelo.setNsPrefix("aemet", aemet);
        modelo.setNsPrefix("geo", geo);
        nombre = ResourceFactory.createProperty(aemet, "stationName");
        indicativo = ResourceFactory.createProperty(aemet, "indicativo");
        indsinop = ResourceFactory.createProperty(aemet, "indsinop");
        province = ResourceFactory.createProperty(aemet, "locatedInProvince");
        latitud = ResourceFactory.createProperty(geo, "lat");
        altitud = ResourceFactory.createProperty(geo, "alt");
        longitud = ResourceFactory.createProperty(geo, "long");
    }

    public Grafo(String datos, String esquema, int type) {
        this();
        modelo.setNsPrefix("rdf", rdf);
        modelo.setNsPrefix("rdfs", rdfs);
        modelo.setNsPrefix("xsd", xsd);

        Model modelEsq = RDFDataMgr.loadModel(esquema);
        Model modelDatos = RDFDataMgr.loadModel(datos);

        Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
        switch (type) {
            case RDFSREASONER:
                reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_FULL);
                break;
            case SIMPLEREASONER:
                reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_SIMPLE);
                break;
        }
        reasonerScheme = reasoner.bindSchema(modelEsq);
        modelo = ModelFactory.createInfModel(reasonerScheme, modelDatos);
    }

    public void addStation(JSONObject node) throws JSONException {
        Resource resource = modelo.createResource(si2 +"Station" + node.getString("indicativo"));
        resource.addLiteral(nombre, node.getString("nombre"));
        resource.addLiteral(indicativo, node.getString("indicativo"));
        resource.addLiteral(indsinop, node.getString("indsinop"));
        resource.addProperty(province, getProvinceNode(node));
        resource.addLiteral(latitud, node.getString("latitud"));
        resource.addLiteral(altitud, node.getString("altitud"));
        resource.addLiteral(longitud, node.getString("longitud"));
    }

    private Resource getProvinceNode(JSONObject node) throws JSONException {
        String provincia = toponimos.getCanonical(node.getString("provincia"));
        if (provincia == null) System.out.println(node.getString("provincia"));
        return modelo.getResource(si2 + provincia);
    }

    public void addStation(JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            addStation(array.getJSONObject(i));
        }
    }

    public void print(OutputStream stream, int type) {
        switch (type) {
            case TURTTLE:
                RDFDataMgr.write(stream, modelo, RDFFormat.TURTLE_PRETTY);
                break;
            case XML:
                RDFDataMgr.write(stream, modelo, RDFFormat.RDFXML_PRETTY);
                break;
            case NTRIPLE:
                RDFDataMgr.write(stream, modelo, RDFFormat.NTRIPLES);
                break;
        }
    }

    public Model inferir(Grafo grafo) {
        if (grafo.modelo instanceof InfModel) {
            InfModel infModel = ModelFactory.createInfModel(reasonerScheme, grafo.modelo);
            this.modelo = modelo.union(infModel);
            System.out.println("aaaaaaaaa");
        }else {
            this.modelo =modelo.union(grafo.modelo);
        }
        return this.modelo;
    }

}
