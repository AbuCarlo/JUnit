package com.aanassar.junit

import org.junit.rules.ExternalResource

/**
 * This rule allows a unit test to assume that a system property has a particular value,
 * and
 * @author tnassar
 *
 */
public final class SystemPropertyRule extends ExternalResource {

    private final Map<String,Object> testProperties = [:]
	// Properties is really Map<Object,Object>. Is that a problem?
    private Map<String,Object> systemProperties

    SystemPropertyRule() {
    }

    public void setProperties(Map<String,Object> testProperties) {
		// TODO Make this immutable after before() is called.
        testProperties.each { String key, Object value ->
            System.setProperty key, value.toString()
        }
        this.testProperties.putAll testProperties
    }

    @Override protected void before() {
        systemProperties = new LinkedHashMap(System.properties)
    }

    @Override protected void after() {
        testProperties.each { String key, Object value ->
            Object previousValue = systemProperties[key]
            if (previousValue == null) {
                System.clearProperty key
            } else {
                System.setProperty key, previousValue
            }
        }
    }
}
