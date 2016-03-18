// Same as implementation classes - access to protected and package methods
package eu.greencom.xgateway.integrationlayer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import eu.greencom.xgateway.integrationlayer.impl.MessageUtil;

public class TestMessageUtil {

    // State-less, no set-up needed
    MessageUtil util = new MessageUtil();

    @Test
    public void testGetTemplate() {
        assertNotNull(util.getTemplate("test.template.empty"));
    }

    @Test
    public void testEmptyTemplate() {
        // Superfluous parameters (all) are ignored
        util.fillTemplate("test.template.empty", "no_arg", null);
    }

    @Test
    public void testSingleParamTemplate() {
        // Superfluous parameters (2nd etc.) are ignored
        assertEquals("{\"param\":\"value\"}", util.fillTemplate("test.template.singleparam", "value", "value1", null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultiParamTemplate() {
        // Indicate missing second parameter
        util.fillTemplate("test.template.multiparam", "value");
    }

    @Test(expected = NullPointerException.class)
    public void testMultiParamNullTemplate() {
        // Can't replace with null - indicate invalid value
        util.fillTemplate("test.template.multiparam", "value", null);
    }

}