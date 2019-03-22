package mm.issues.util;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

/**
 * Printer is a utility class for pretty printing json.
 * When json is in pretty print format it is much easy to read and troubleshoot.
 * Pretty print can be enabled by including the lowercase letter p as the first
 * argument on the command line.<br>
 * <br>
 *
 * For example from command line: java mm/issues/Main p tarantulatechnology/apache<br>
 * or from the provided executable jar: java -jar getIssues.jar p tarantulatechnology/apache
 *
 * <br><br>
 *
 * @author markmorris
 * @version 1.0
 *
 */
public class Printer {

	/**
	 * prettyPrint will format json with line returns so it is easy to read
	 * @param json to be formatted.
	 * @return String of json in pretty print format.
	 */
	public static String prettyPrint(JsonStructure json) {
	    return jsonFormat(json, JsonGenerator.PRETTY_PRINTING);
	}


	/**
	 * jsonFormat formats json in pretty print format.
	 * @param json to be formatted.
	 * @param options configure the formater
	 * @return String of pretty print json
	 */
	private static String jsonFormat(JsonStructure json, String... options) {
	    StringWriter stringWriter = new StringWriter();
	    Map<String, Boolean> config = buildConfig(options);
	    JsonWriterFactory writerFactory = Json.createWriterFactory(config);
	    JsonWriter jsonWriter = writerFactory.createWriter(stringWriter);

	    jsonWriter.write(json);
	    jsonWriter.close();

	    return stringWriter.toString();
	}

	/**
	 * buildConfig constructs a config Map for the formatter with options.
	 * @param options for the json formatter
	 * @return return the config Map
	 */
	private static Map<String, Boolean> buildConfig(String... options) {
	    Map<String, Boolean> config = new HashMap<String, Boolean>();

	    if (options != null) {
	        for (String option : options) {
	            config.put(option, true);
	        }
	    }

	    return config;
	}


}
