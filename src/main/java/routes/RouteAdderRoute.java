package routes;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.entrybuilders.RouteEntry;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

import static database.DBValues.ROUTE_COLLECTION_NAME;
import static routes.util.ParamsName.Route.SI;
import static routes.util.ParamsName.Route.SPI;

public class RouteAdderRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(RouteAdderRoute.class);

    @Override
    public Object handle(Request request, Response response) {


        MongoDatabase db = ConfigManager.getConfig().getDatabase();
        JSONObject body;

        try {
            body = new JSONObject(request.body());
            final JSONArray addressList = body.getJSONArray(SI);
            final MongoCollection<Document> collection = db.getCollection(ROUTE_COLLECTION_NAME);
            final String SPId = request.params(SPI);

            if (collection.find(new RouteEntry().addSPI(SPId).build()).first() == null) {

                LOG.debug("Adding a new route to the SFC table with id" + SPId);
                collection.insertOne(
                        new RouteEntry()
                                .addSPI(request.params(SPI))
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
