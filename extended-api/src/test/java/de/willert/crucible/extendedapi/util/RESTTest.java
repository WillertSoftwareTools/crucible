package de.willert.crucible.reportplugin.extendedapi.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by czoeller on 25.10.16.
 */
public class RESTTest {
    protected void assertHtmlResponse(String response) {
        assertNotNull("No text returned!", response);

        assertResponseContains(response, "<html>");
        assertResponseContains(response, "</html>");
    }

    protected void assertResponseContains(String response, String text) {
        assertTrue("Response should contain " + text + " but was: " + response, response.contains(text));
    }
}
