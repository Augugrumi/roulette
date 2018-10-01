package routes;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DBValues;
import database.entrybuilders.RouteEntry;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.ParamsName;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

public class VnfNameGetterRoute  implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(RouteGetterRoute.class);

    @Override
    public Object handle(Request request, Response response) {

        LOG.debug("Get vnf name called");
        final MongoDatabase db = ConfigManager.getConfig().getDatabase();
        final MongoCollection<Document> routes = db.getCollection(DBValues.COLLECTION_NAME);
        final String SPId = request.params(ParamsName.SPI);
        final String serviceIndex = request.params(ParamsName.SI);
        ResponseCreator res;

        Document route = routes.find(new RouteEntry().addSPI(SPId).build()).first();
        if (route != null) {
            LOG.debug(route.getString("si"));
            JSONArray vnfNames = new JSONArray(route.getString("si"));
            JSONObject name;
            try {
                name = (JSONObject)(vnfNames.get(Integer.parseInt(serviceIndex)));
                LOG.debug("Hit");
                res = new ResponseCreator(ResponseCreator.ResponseType.OK);
                res.add(ResponseCreator.Fields.CONTENT, name);
            } catch (Exception e) {
                LOG.debug("Index miss");

                res = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
                res.add(ResponseCreator.Fields.REASON, "Index " + serviceIndex + " not found on route " + SPId);
            }

        } else {
            LOG.debug("Route miss");

            res = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            res.add(ResponseCreator.Fields.REASON, "Route " + SPId + " not found");
        }

        return res;
    }
}
