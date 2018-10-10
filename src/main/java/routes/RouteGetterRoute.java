package routes;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.entrybuilders.RouteEntry;
import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

import static database.DBValues.ROUTE_COLLECTION_NAME;
import static routes.util.ParamsName.Route.SPI;


public class RouteGetterRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(RouteGetterRoute.class);


    @Override
    public Object handle(Request request, Response response) {

        LOG.debug("Get route called");
        final MongoDatabase db = ConfigManager.getConfig().getDatabase();
        final MongoCollection<Document> routes = db.getCollection(ROUTE_COLLECTION_NAME);
        final String SPId = request.params(SPI);
        ResponseCreator res;

        Document route = routes.find(new RouteEntry().addSPI(SPId).build()).first();
        if (route != null) {
            LOG.debug("Hit");

            res = new ResponseCreator(ResponseCreator.ResponseType.OK);
            res.add(ResponseCreator.Fields.CONTENT, new JSONObject(route.toJson()));
        } else {
            LOG.debug("Miss");

            res = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            res.add(ResponseCreator.Fields.REASON, "Route not found");
        }

        return res;
    }
}
