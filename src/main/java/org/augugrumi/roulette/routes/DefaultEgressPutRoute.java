package org.augugrumi.roulette.routes;

import org.augugrumi.roulette.routes.util.ResponseCreator;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;
import org.slf4j.Logger;
import org.augugrumi.roulette.util.ConfigManager;

import static org.augugrumi.roulette.routes.util.ParamsName.Configuration.DEFAULT_EGRESS;

public class DefaultEgressPutRoute implements Route {

    final public static Logger LOG = ConfigManager.getConfig().getApplicationLogger(DefaultEgressPutRoute.class);



    @Override
    public Object handle(Request request, Response response) {
        LOG.debug("Data received:\n" + request.body());
        final JSONObject body = new JSONObject(request.body());
        ResponseCreator res;
        try {
            String newDefaultEgress = body.getString(DEFAULT_EGRESS);
            ConfigManager.getConfig().setDefaultEgress(newDefaultEgress);
            res = new ResponseCreator(ResponseCreator.ResponseType.OK);

        } catch (Exception e) {
            res = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            res.add(ResponseCreator.Fields.REASON, e.getMessage());
        }

        return res;
    }
}