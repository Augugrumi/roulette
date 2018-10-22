package org.augugrumi.roulette.routes;

import org.augugrumi.roulette.routes.util.ResponseCreator;
import org.augugrumi.roulette.util.ConfigManager;
import org.json.JSONObject;
import org.slf4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;

import static org.augugrumi.roulette.routes.util.ParamsName.Configuration.DEFAULT_INGRESS;

public class DefaultIngressPutRoute implements Route {

    final public static Logger LOG = ConfigManager.getConfig().getApplicationLogger(DefaultIngressPutRoute.class);

    @Override
    public Object handle(Request request, Response response) {
        LOG.debug("Data received:\n" + request.body());
        final JSONObject body = new JSONObject(request.body());
        ResponseCreator res;
        try {
            String newDefaultIngress = body.getString(DEFAULT_INGRESS);
            ConfigManager.getConfig().setDefaultIngress(newDefaultIngress);
            res = new ResponseCreator(ResponseCreator.ResponseType.OK);
        } catch (Exception e) {
            res = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            res.add(ResponseCreator.Fields.REASON, e.getMessage());
        }

        return res;
    }
}