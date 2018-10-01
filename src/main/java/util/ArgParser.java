package util;

import org.apache.commons.cli.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * It parses arguments given by the CLI
 */
public class ArgParser {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(ArgParser.class);

    // Short options
    final private static String API_CONF_PATH_OPTION_SHORT = "f";
    final private static String PORT_OPTION_SHORT = "p";
    final private static String DATABASE_JSON_OPTION_SHORT = "d";

    // Long options
    //final private static String API_CONF_PATH_OPTION_LONG = "file";
    //final private static String PORT_OPTION_LONG = "port";

    final private String[] ARGS;
    final private Options ARGS_TO_PARSE;

    /**
     * Prepare the parser but it doesn't execute it
     *
     * @param args the arguments to parse
     */
    public ArgParser(String[] args) {

        this.ARGS = args;

        ARGS_TO_PARSE = new Options();

        ARGS_TO_PARSE.addOption(API_CONF_PATH_OPTION_SHORT, true, "Path to API JSON file");
        //ARGS_TO_PARSE.addOption(API_CONF_PATH_OPTION_LONG, true, "Path to API JSON file");
        ARGS_TO_PARSE.addOption(PORT_OPTION_SHORT, true, "Port where Harbor should run");
        //ARGS_TO_PARSE.addOption(PORT_OPTION_LONG, true, "Port where Harbor should run");
        ARGS_TO_PARSE.addOption(DATABASE_JSON_OPTION_SHORT, true, "Path to the database JSON config");
    }

    /**
     * Execute the parsing procedure
     * @throws ParseException this exception is thrown if the parsing fails
     */
    public void parse() throws ParseException {

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(ARGS_TO_PARSE, ARGS);

        if (cmd.hasOption(API_CONF_PATH_OPTION_SHORT) && cmd.getOptionValue(API_CONF_PATH_OPTION_SHORT) != null) {
            ConfigManager.getConfig().setAPIConfig(cmd.getOptionValue(API_CONF_PATH_OPTION_SHORT));
            LOG.debug(API_CONF_PATH_OPTION_SHORT + " passed as argument. Value: " + cmd.getOptionValue(API_CONF_PATH_OPTION_SHORT));
        }
        if (cmd.hasOption(PORT_OPTION_SHORT) && cmd.getOptionValue(PORT_OPTION_SHORT) != null) {
            ConfigManager.getConfig().setPort(Integer.parseInt(cmd.getOptionValue(PORT_OPTION_SHORT)));
            LOG.debug(PORT_OPTION_SHORT + " passed as argument. Value: " + cmd.getOptionValue(PORT_OPTION_SHORT));
        }
        if (cmd.hasOption(DATABASE_JSON_OPTION_SHORT) && cmd.getOptionValue(DATABASE_JSON_OPTION_SHORT) != null) {
            try (final FileInputStream fis = new FileInputStream(cmd.getOptionValue(DATABASE_JSON_OPTION_SHORT))) {

                JSONObject databaseJson = new JSONObject(readFile(fis));

                if (databaseJson.getInt(DBJSONDefinitions.PORT_FIELD) != 0) {
                    ConfigManager.getConfig().setDBPort(databaseJson.getInt(DBJSONDefinitions.PORT_FIELD));
                }
                if (databaseJson.getString(DBJSONDefinitions.ADDRESS_FIELD) != null) {
                    ConfigManager.getConfig().setDBIP(databaseJson.getString(DBJSONDefinitions.ADDRESS_FIELD));
                }
                if (databaseJson.getString(DBJSONDefinitions.USERNAME_FIELD) != null) {
                    ConfigManager.getConfig().setDBUsername(databaseJson.getString(DBJSONDefinitions.USERNAME_FIELD));
                }
                if (databaseJson.getString(DBJSONDefinitions.PASSWORD_FIELD) != null) {
                    ConfigManager.getConfig().setDBPassword(databaseJson.getString(DBJSONDefinitions.PASSWORD_FIELD));
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new ParseException("Impossible to find a suitable JSON config file");
            } catch (IOException e) {
                e.printStackTrace();
                throw new ParseException("Error while reading the database JSON config file");
            } catch (JSONException e) {
                e.printStackTrace();
                throw new ParseException("Failure parsing the database JSON config file: not a valid JSON");
            }
        }
    }

    private String readFile(FileInputStream fis) throws IOException {
        StringBuilder res = new StringBuilder();

        int i;
        while ((i = fis.read()) != -1) {
            res.append((char)i);
        }

        LOG.debug("Read:\n" + res.toString());
        return res.toString();
    }
}
