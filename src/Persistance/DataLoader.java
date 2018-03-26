package Persistance;

import Model.Grafo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.jena.atlas.logging.LogCtl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DataLoader {

    private static OkHttpClient client = new OkHttpClient();
    private static final String clave = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuZnJhcGNAZ21haWwuY29tIiwianRpIjoiMzg1ZTA5YjctYjYzZi00YWRiLTgxZDMtYmEwOWM4NTk1MWQxIiwiaXNzIjoiQUVNRVQiLCJpYXQiOjE1MjE0NTY1MDMsInVzZXJJZCI6IjM4NWUwOWI3LWI2M2YtNGFkYi04MWQzLWJhMDljODU5NTFkMSIsInJvbGUiOiIifQ.L9mPIfI4xTvizQm3m27Gqbg_zSIVYEuui9ZVY749kGk";

    public static Grafo cargaGrafo(){
        LogCtl.setCmdLogging();
        Grafo grafo = new Grafo();

        JSONObject json = DataLoader.getJsonObject("https://opendata.aemet.es/opendata/api/valores/climatologicos/inventarioestaciones/todasestaciones/?api_key=" + clave);
        try {
            JSONArray jsonArray = DataLoader.getJsonArray(json.get("datos").toString());
            grafo.addStation(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return grafo;
    }

    private static JSONObject getJsonObject(String url) {

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JSONArray getJsonArray(String url) {

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            return new JSONArray(response.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
