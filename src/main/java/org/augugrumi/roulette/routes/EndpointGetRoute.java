package org.augugrumi.roulette.routes;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.augugrumi.roulette.database.entrybuilders.EndpointEntry;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.augugrumi.roulette.routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import org.augugrumi.roulette.util.ConfigManager;

import static org.augugrumi.roulette.database.DBValues.ENDPOINT_COLLECTION_NAME;
import static org.augugrumi.roulette.routes.util.ParamsName.MONGO_ID;

public class EndpointGetRoute implements Route {
    @Override
    public Object handle(Request request, Response response) {

        final MongoDatabase db = ConfigManager.getConfig().getDatabase();
        final MongoCollection<Document> endpoints = db.getCollection(ENDPOINT_COLLECTION_NAME);
        final EndpointEntry query = (EndpointEntry)new EndpointEntry().setId(new ObjectId(request.params(MONGO_ID)));
        final Document queryRes = endpoints.find(query.build()).first();

        if (queryRes != null) {
            ResponseCreator res = new ResponseCreator(ResponseCreator.ResponseType.OK);
            res.add(ResponseCreator.Fields.CONTENT, new JSONObject(queryRes.toJson()));

            return res;
        } else {
            return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                    .add(ResponseCreator.Fields.REASON, "Endpoint not found");
        }
    }
}
