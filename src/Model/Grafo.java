package Model;

import org.apache.jena.atlas.logging.LogCtl;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
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
import java.util.ArrayList;

public class Grafo {

    private enum Provincia{

        ACORUÑA(new String[]{"A CORUÑA", "A CORUNYA", "ACORUÑA"});


        private ArrayList toponimos = new ArrayList<String>();

        Provincia(String[] nombres) {
            for (String nombre : nombres) {
                this.toponimos.add(nombre);
            }
        }
    }

    public static final int TURTTLE = 0;
    public static final int XML = 1;
    public static final int NTRIPLE = 2;

    public static final int RDFSREASONER = 0;
    public static final int SIMPLEREASONER = 1;

    static final String si2 = "http://www.si2.com/";
    private static final String aemet = "http://aemet.linkeddata.es/ontology/";
    private static final String geo = "http://www.w3.org/2003/01/geo/wgs84_pos#";

    private static final String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
    private static final String xsd = "http://www.w3.org/2001/XMLSchema#";


    private Model modelo;
    private final Property indsinop;
    private final Property province;
    private final Property latitud;
    private final Property altitud;
    private final Property longitud;
    private final Property provincia;
    private final Property nombre;

    public Grafo() {
        LogCtl.setCmdLogging();
        modelo = ModelFactory.createDefaultModel();
        modelo.setNsPrefix("si2", si2);
        modelo.setNsPrefix("aemet", aemet);
        modelo.setNsPrefix("geo", geo);
        nombre = ResourceFactory.createProperty(aemet, "stationName");
        indsinop = ResourceFactory.createProperty(aemet, "indsinop");
        province = ResourceFactory.createProperty(aemet, "Province");
        provincia = ResourceFactory.createProperty(si2, "esta");
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
        switch(type){
            case RDFSREASONER:
                reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_FULL);
                break;
            case SIMPLEREASONER:
                reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_SIMPLE);
                break;
        }
        Reasoner reasonerScheme = reasoner.bindSchema(modelEsq);
        modelo = ModelFactory.createInfModel(reasonerScheme, modelDatos);
    }

    public void addStation(JSONObject node) throws JSONException {
        Resource resource = modelo.createResource(si2 + node.getString("indicativo"));
        resource.addLiteral(nombre, node.getString("nombre"));
        resource.addLiteral(indsinop, node.getString("indsinop"));
        resource.addLiteral(province, node.getString("provincia"));
        resource.addProperty(provincia, modelo.createResource(si2 + node.getString("provincia")));
        resource.addLiteral(latitud, node.getString("latitud"));
        resource.addLiteral(altitud, node.getString("altitud"));
        resource.addLiteral(longitud, node.getString("longitud"));
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

    public Model union(Grafo grafo) {
        this.modelo = modelo.union(grafo.modelo);
        return this.modelo;
    }

}
