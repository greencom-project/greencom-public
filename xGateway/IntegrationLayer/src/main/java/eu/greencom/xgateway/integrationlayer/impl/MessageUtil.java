package eu.greencom.xgateway.integrationlayer.impl;

import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Loads a Java properties file of JSON-RPC message templates and offers methods
 * to fill in the place holders ($\d+). Offers further utility methods for JSON
 * processing.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class MessageUtil  {

    private static final Logger LOG = LoggerFactory.getLogger(MessageUtil.class.getName());

    public static final String REPLACEMENT_PATTERN = "\\$\\d";

    private static Pattern PATTERN = Pattern.compile(REPLACEMENT_PATTERN);

    // Message increment
    private static int INCREMENT = 0;

    // Restricted to a single digit at moment
    private static final int INCREMENTLIMIT = 9;

    private Properties msgs = null;

    private ObjectMapper mapper = new ObjectMapper();

    public MessageUtil() {
        init();
    }

    protected Properties loadMessages(String n) {
        // Not null
        Properties p = new Properties();
        InputStream stream = getClass().getResourceAsStream(n);
        try {
            p.load(stream);

        } catch (Exception e) {
            LOG.error(MessageUtil.class.getName() + "Failed to load properties {}: {}" + e, n, e.getMessage());
        }

        try {
            stream.close();
        } catch (Exception e) {
            LOG.error(MessageUtil.class.getSimpleName() + ": " + e, e);
        }

        return p;
    }

    protected void init() {
        msgs = loadMessages("/template.messages");
        LOG.debug("Loaded message templates: " + msgs);
    }

    protected String getTemplate(String name) {
        return msgs.getProperty(name);
    }

    protected String fillTemplate(String name, String... args) {
        String temp = getTemplate(name);
        String res = null;
        if (!temp.isEmpty() && args != null && args.length > 0) {            
         
                StringBuffer sb = new StringBuffer(temp.length()); //NOSONAR squid : S1149 ff : appendTail only accepts buffer!
                Matcher m = PATTERN.matcher(temp);
                while (m.find()) {
                    String text = m.group();
                    // Position (1 based) after $", index is less 1
                    // ff change from valueof to parse          
                    int i = Integer.parseInt(text.substring(1)) -1;
                    
                    // Check whether replacement exists
                    if (i < args.length) {
                        LOG.debug("Replacement " + args[i]);
                        m.appendReplacement(sb, Matcher.quoteReplacement(args[i]));
                    } else {
                        throw new IllegalArgumentException("Replacement parameter at position " + i + " missing");
                    }
                }
                m.appendTail(sb);
                res = sb.toString();
        }else {
                // No arguments
                return temp;
            
        }
        // No template
        return res;
    }

    protected synchronized String generateRequestId() {
        // There is a restriction on id size to be a permanent size of 3
        if (INCREMENT < INCREMENTLIMIT) {
            ++INCREMENT;
        } else {
            INCREMENT = 0;
        }
        return "id" + INCREMENT;
    }

    protected JsonNode parseJson(String content) {
        JsonNode msg = null;
        try {
            msg = mapper.readTree(content);
        } catch (Exception e) {
            LOG.error(MessageUtil.class.getName() + "Failed to parse JSON: {}: {}" + e, content, e.getMessage());
        }
        return msg;
    }

    protected <T> T readJson(JsonNode conent, Class<T> clazz) {
        T object = null;
        try {
            object = mapper.convertValue(conent, clazz);
        } catch (Exception e) {
            // ff
            LOG.error(MessageUtil.class.getName() + ":" + e, e.getMessage());
        }
        return object;
    }

    protected String writeJson(Object o) {
        String s = null;
        try {
            // Do not serialize keys with null value
            mapper.setSerializationInclusion(Include.NON_NULL);
            s = mapper.writeValueAsString(o);
        } catch (Exception e) {
            // ff
            LOG.error(MessageUtil.class.getName() + ":" + e, e.getMessage());
        }
        return s;
    }

    /*
     * Extracts "data"member from a successful JSON-RPC response
     */
    protected String getResultData(String response) {
        String result = null;
        JsonNode json = parseJson(response);
        try {
            result = json.get("result").get("data").asText();
            LOG.debug("Response data is: {}", result);
        } catch (Exception e) {
            LOG.error(MessageUtil.class.getName() + "Failed to extract response data: {}" + e, e.getMessage());
        }
        return result;
    }

}
