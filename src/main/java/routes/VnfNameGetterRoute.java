package routes;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DBValues;
import database.entrybuilders.RouteEntry;
import org.bson.BsonArray;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

public class VnfNameGetterRoute  implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(RouteGetterRoute.class);
    final private static String SPI_PARAM_NAME = "spi";
    final private static String SI_PARAM_NAME = "si";

    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.info("Get vnf name called");
        final MongoDatabase db = ConfigManager.getConfig().getDatabase();
        final MongoCollection<Document> routes = db.getCollection(DBValues.COLLECTION_NAME);
        final String SPId = request.params(SPI_PARAM_NAME);
        final String serviceIndex = request.params(SI_PARAM_NAME);
        ResponseCreator res;

        Document route = routes.find(new RouteEntry().addSPI(SPId).build()).first();
        if (route != null) {
            LOG.info((String)route.get("si"));
            JSONArray vnfNames = new JSONArray((String)route.get("si"));
            String name;
            try {
                name = vnfNames.getString(Integer.parseInt(serviceIndex));
                LOG.info("Hit");
                res = new ResponseCreator(ResponseCreator.ResponseType.OK);
                res.add(ResponseCreator.Fields.CONTENT, name);
            } catch (Exception e) {
                LOG.info("Index miss");

                res = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
                res.add(ResponseCreator.Fields.REASON, "Index " + serviceIndex + " not found on route " + SPId);
            }

        } else {
            LOG.info("Route miss");

            res = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            res.add(ResponseCreator.Fields.REASON, "Route " + SPId + " not found");
        }

        return res;
    }
}
