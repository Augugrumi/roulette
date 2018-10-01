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

public class RouteDeleterRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(RouteAdderRoute.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {
        LOG.debug("Get route called");
        final MongoDatabase db = ConfigManager.getConfig().getDatabase();
        final MongoCollection<Document> routes = db.getCollection(DBValues.COLLECTION_NAME);
        final String SPId = request.params(ParamsName.SPI);
        ResponseCreator res;

        Document toRemove = routes.find(new RouteEntry().addSPI(SPId).build()).first();

        if (toRemove == null) {
            res = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            res.add(ResponseCreator.Fields.REASON, "Route does not existing");
        } else {
            LOG.debug("Deleting route " + SPId);
            routes.deleteOne(toRemove);
            res = new ResponseCreator(ResponseCreator.ResponseType.OK);
        }

        return res;
    }
}
