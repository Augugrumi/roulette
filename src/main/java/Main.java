import database.DBInitializer;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import util.ArgParser;
import util.ConfigManager;

import java.io.IOException;

import static spark.Spark.port;

/**
 * Main program class. It parses the command line args and starts the Spark Java server, loading the desired API.
 *
 * @see ArgParser
 * @see DynamicAPILoader
 */
public class Main {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(Main.class);

    public static void main (String args[]) {

        ArgParser parser = new ArgParser(args);
        try {
            parser.parse();
        } catch (ParseException e) {
            LOG.error("The application failed to correctly parse the supplied arguments");
            e.printStackTrace();
            System.exit(1);
        }

        LOG.info("Roulette started");

        port(ConfigManager.getConfig().getPort());

        try {
            DBInitializer.init();
            LOG.info("Database successfully retrieved");

            DynamicAPILoader apiLoader = new DynamicAPILoader(ConfigManager.getConfig().getAPIConfig());
            apiLoader.load();

            LOG.info("Route successfully set");
        } catch (IOException e) {
            LOG.error("The Application could not load a valid API configuration");
            e.printStackTrace();
            System.exit(1);
        } catch (NullPointerException e) {
            LOG.error("NullPointer exception: did you forget to set some required parameters?");
            e.printStackTrace();
        } catch (Exception e) {
            LOG.error("Roulette could not be initialized");
            e.printStackTrace();
        }
    }
}
