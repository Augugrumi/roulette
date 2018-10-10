package database.entrybuilders;

import org.json.JSONArray;

import static routes.util.ParamsName.Route.SI;
import static routes.util.ParamsName.Route.SPI;

public class RouteEntry extends AbsEntryBuilder {

    public RouteEntry addSPI(String id) {
        entry.append(SPI, id);
        return this;
    }

    public RouteEntry addSI(JSONArray ids) {
        entry.append(SI, ids.toList());
        return this;
    }
}
