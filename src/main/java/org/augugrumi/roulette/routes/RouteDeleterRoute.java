package org.augugrumi.roulette.routes;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.augugrumi.roulette.database.entrybuilders.RouteEntry;
import org.augugrumi.roulette.routes.util.ResponseCreator;
import org.augugrumi.roulette.util.ConfigManager;
import org.bson.Document;
import org.slf4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;

import static org.augugrumi.roulette.database.DBValues.ROUTE_COLLECTION_NAME;
import static org.augugrumi.roulette.routes.util.ParamsName.Route.SPI;


public class RouteDeleterRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(RouteAdderRoute.class);

    @Override
    public Object handle(Request request, Response response) {
        LOG.debug("Get route called");
        final MongoDatabase db = ConfigManager.getConfig().getDatabase();
        final MongoCollection<Document> routes = db.getCollection(ROUTE_COLLECTION_NAME);
        final String SPId = request.params(SPI);
        ResponseCreator res;

        Document toRemove = routes.find(new RouteEntry().addSPI(SPId).build()).first();

        if (toRemove == null) {
            res = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            res.add(ResponseCreator.Fields.REASON, "Route does not exist");
        } else {
            LOG.debug("Deleting route " + SPId);
            routes.deleteOne(toRemove);
            res = new ResponseCreator(ResponseCreator.ResponseType.OK);
        }

        return res;
    }
}
