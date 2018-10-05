package database.entrybuilders;

import org.json.JSONArray;

import static routes.util.ParamsName.Route.SI;
import static routes.util.ParamsName.Route.SPI;

public class RouteEntry extends AbsEntryBuilder {

    /*final static private String SPI = "spi";
    final static private  String SI = "si";*/


    public RouteEntry addSPI(String id) {
        entry.append(SPI, id);
        return this;
    }

    public RouteEntry addSI(JSONArray ids) {
        entry.append(SI, ids.toString());
        return this;
    }
}
