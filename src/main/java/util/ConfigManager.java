package util;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton handling requests to the true configuration class
 *
 * @see Config
 */
public class ConfigManager {

    private static Config configuration;

    private ConfigManager () {

    }

    /**
     * Getter method to obtain the Singleton configuration. Note that this is a "lazy" singleton, so the real singleton
     * gets created the first time it's required, and not at the ConfigManager creation.
     * @return If it's the first call, it returns a new Config instance, otherwise an already existing Config it's
     * returned
     * @see Config
     */
    public static synchronized Config getConfig () {
        if (configuration == null) {
            configuration = new Config();
        }

        return configuration;
    }


    public static class Config {

        // Default values
        final private int MONGO_DB_STANDARD_PORT = 27017;
        final private String MONGO_DB_PROTOCOL = "mongodb://";
        final private String ROULETTE_DEFAULT_DB_NAME = "roulette_db";
        final public String ROULETTE_DEFAULT_ROUTE_COLLECTION_NAME = "route";

        // Init config keys
        final private String R_PORT = "ROULETTE_PORT";
        final private String R_API = "ROULETTE_API_CONFIG";
        final private String R_DB_IP = "ROULETTE_DATABASE_IP";
        final private String R_DB_PORT = "ROULETTE_DATABASE_PORT";
        final private String R_DB_NAME = "ROULETTE_DATABASE_NAME";
        final private String R_DB_USERNAME = "ROULETTE_DATABASE_USERNAME";
        final private String R_DB_PASSWORD = "ROULETTE_DATABASE_PASSWORD";
        // End config keys

        final private static Logger LOG = LoggerFactory.getLogger(Config.class);
        
        private int port;
        private int databasePort;
        private String APIConfigPath;
        private String databaseIP;
        private String databaseName;
        final private MongoClient MONGO_CLIENT;
        final private String DATABASE_USERNAME;
        final private String DATABASE_PASSWORD;

        private Config() {
            LOG.debug("Environment variable " + R_PORT + " set to: " + System.getenv(R_PORT));
            if (System.getenv(R_PORT) != null) {
                this.port = Integer.parseInt(System.getenv(R_PORT));
            } else {
                this.port = 80;
            }

            LOG.debug("Environment variable " + R_API + " set to: " + System.getenv(R_API));
            this.APIConfigPath = System.getenv(R_API);

            if (System.getenv(R_DB_IP) != null) {
                databaseIP = System.getenv(R_DB_IP);
            } else {
                LOG.error("You must set a valid database IP");
            }

            if (System.getenv(R_DB_PORT) != null) {
                this.databasePort = Integer.parseInt(System.getenv(R_DB_PORT));
            } else {
                this.databasePort = MONGO_DB_STANDARD_PORT;
            }

            if (System.getenv(R_DB_NAME) != null) {
                this.databaseName = System.getenv(R_DB_NAME);
            } else {
                this.databaseName = ROULETTE_DEFAULT_DB_NAME;
            }

            if (System.getenv(R_DB_USERNAME) != null && System.getenv(R_DB_PASSWORD) != null) {
                this.DATABASE_USERNAME = System.getenv(R_DB_USERNAME);
                this.DATABASE_PASSWORD = System.getenv(R_DB_PASSWORD);
            } else {
                this.DATABASE_USERNAME = "";
                this.DATABASE_PASSWORD = "";
            }

            // Create MongoDB connection
            StringBuilder connection = new StringBuilder();
            connection.append(MONGO_DB_PROTOCOL)
                    .append(DATABASE_USERNAME)
                    .append(":")
                    .append(DATABASE_PASSWORD)
                    .append("@")
                    .append(databaseIP)
                    .append(":")
                    .append(databasePort);
            MONGO_CLIENT = MongoClients.create(new ConnectionString(connection.toString()));
        }

        /**
         * Getter method to obtain the port number in with the server will listen to request
         * @return it returns the port in with Sparks Java is running
         */
        public int getPort () {
            return port;
        }

        /**
         * Get the application logger based on the class name provided
         * @param name the class description
         * @return a logger with the configured class name
         */
        public Logger getApplicationLogger (Class name) {
            return LoggerFactory.getLogger(name);
        }

        /**
         * Getter method to obtain the filepath to the JSON API config
         * @return a filepath to the API config JSON
         */
        public String getAPIConfig () {
            return APIConfigPath;
        }

        public MongoDatabase getDatabase () {

            return MONGO_CLIENT.getDatabase(databaseName);
        }

        public String getDefaultDBName () {
            return databaseName;
        }
    }
}
