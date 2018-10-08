package routes;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.entrybuilders.EndpointEntry;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Route;
import util.ConfigManager;

import static database.DBValues.ENDPOINT_COLLECTION_NAME;
import static routes.util.ParamsName.Endpoint.EGRESS_IP;
import static routes.util.ParamsName.Endpoint.SOCKET_ID_EGRESS;

public abstract class EndpointAbsUpdateRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(EndpointAbsUpdateRoute.class);

    final protected MongoDatabase db = ConfigManager.getConfig().getDatabase();
    final protected MongoCollection<Document> endpoints = db.getCollection(ENDPOINT_COLLECTION_NAME);

    protected interface UpdateAction {
        void update(EndpointEntry toUpdate);
    }

    protected ResponseCreator voidCheck(JSONObject body) {

        final String ip = body.optString(EGRESS_IP, "");
        final String socketId = body.optString(SOCKET_ID_EGRESS, "");

        if (ip.equals("") || socketId.equals("")) {
            return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                    .add(ResponseCreator.Fields.REASON, "You must set socket id and ip" +
                            " for Ingress or Egress");
        }

        return null;
    }


    protected class UpdateResult {
        final private ResponseCreator res;
        final private boolean err;

        private UpdateResult (ResponseCreator res, boolean err) {
            this.res = res;
            this.err = err;
        }

        public ResponseCreator getRes() {
            return res;
        }

        public boolean isErr() {
            return err;
        }
    }

    protected UpdateResult checkAndUpdate(
            Document query,
            EndpointEntry newData,
            JSONObject body,
            UpdateAction updateAction) {

        if (query != null) {
            try {
                    updateAction.update(newData);
            } catch (JSONException e) {
                return new UpdateResult(
                        new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                                .add(ResponseCreator.Fields.REASON, e.getMessage()),
                        true
                );
            }

            LOG.info("Data to be replaced:\n" + newData.toString());
            endpoints.replaceOne(query, newData.build());
            return new UpdateResult(
                    new ResponseCreator(ResponseCreator.ResponseType.OK),
                    false
            );
        } else {
            return new UpdateResult(
                    new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                            .add(ResponseCreator.Fields.REASON, "Cannot found document to update"),
                    false

            );
        }
    }

}
