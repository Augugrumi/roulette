package routes;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DBValues;
import database.entrybuilders.RouteEntry;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.ParamsName;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

public class RouteAdderRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(RouteAdderRoute.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {


        MongoDatabase db = ConfigManager.getConfig().getDatabase();
        JSONObject body;

        try {
            body = new JSONObject(request.body());
            final JSONArray addressList = body.getJSONArray(ParamsName.SI);
            final MongoCollection<Document> collection = db.getCollection(DBValues.COLLECTION_NAME);
            final String SPId = request.params(ParamsName.SPI);

            if (collection.find(new RouteEntry().addSPI(SPId).build()).first() == null) {

                LOG.info("Adding a new route to the SFC table with id" + SPId);
                collection.insertOne(
                        new RouteEntry()
                                .addSPI(request.params(ParamsName.SPI))
                                .addSI(addressList)
                                .build()
                );
            } else {
                ResponseCreator res = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
                res.add(ResponseCreator.Fields.REASON, "Route already existing");
                return res;
            }


        } catch (JSONException e) {
            ResponseCreator res = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            res.add(ResponseCreator.Fields.REASON, e.getMessage());
            return res;
        }

        return new ResponseCreator(ResponseCreator.ResponseType.OK).toString();
    }
}
