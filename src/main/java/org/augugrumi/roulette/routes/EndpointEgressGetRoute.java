package org.augugrumi.roulette.routes;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.augugrumi.roulette.database.entrybuilders.EndpointEntry;
import org.augugrumi.roulette.routes.util.ParamsName;
import org.augugrumi.roulette.routes.util.ResponseCreator;
import org.augugrumi.roulette.util.ConfigManager;
import org.bson.Document;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import static org.augugrumi.roulette.database.DBValues.ENDPOINT_COLLECTION_NAME;
import static org.augugrumi.roulette.routes.util.ParamsName.Endpoint.*;

public class EndpointEgressGetRoute implements Route {

    @Override
    public Object handle(Request request, Response response) {

        final MongoDatabase db = ConfigManager.getConfig().getDatabase();
        final MongoCollection<Document> endpoints = db.getCollection(ENDPOINT_COLLECTION_NAME);
        final EndpointEntry query = (EndpointEntry)new EndpointEntry().setSrcIP(request.params(ParamsName.Endpoint.SRC_IP))
                .setDestIP(request.params(DEST_IP))
                .setSrcPort(Integer.parseInt(request.params(SRC_PORT)))
                .setDestPort(Integer.parseInt(request.params(DEST_PORT)))
                .setSfcId(request.params(SFC_ID));

        final Document queryRes = endpoints.find(query.build()).first();

        if (queryRes != null) {
            ResponseCreator res = new ResponseCreator(ResponseCreator.ResponseType.OK);
            JSONObject queryResObj = new JSONObject(queryRes.toJson());
            JSONObject ingressJson = new JSONObject();
            if (queryResObj.has(EGRESS_IP)) {
                ingressJson.put(EGRESS_IP, queryResObj.get(EGRESS_IP));
                ingressJson.put(SOCKET_ID_EGRESS, queryResObj.get(SOCKET_ID_EGRESS));
            } else {
                ingressJson.put(EGRESS_IP, ConfigManager.getConfig().getDefaultEgress());
                ingressJson.put(SOCKET_ID_EGRESS, "-1");
            }

            res.add(ResponseCreator.Fields.CONTENT, ingressJson);

            return res;
        } else {
            return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                    .add(ResponseCreator.Fields.REASON, "Egress not set properly");
        }
    }
}