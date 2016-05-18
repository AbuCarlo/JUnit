package com.aanassar.junit;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import junit.framework.AssertionFailedError;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

public abstract class XmlSchemaValidatingMatcher extends TypeSafeDiagnosingMatcher<Object> {

	private final Schema schema;

	public XmlSchemaValidatingMatcher(Source xsdSource) throws SAXException {
		Source[] schemaSources = { xsdSource };
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		schema = schemaFactory.newSchema(schemaSources);
	}
	
	public XmlSchemaValidatingMatcher(Resource xsdResource) throws SAXException, IOException {
		this(new StreamSource(xsdResource.getInputStream()));
	}

	public void describeTo(Description description) {
		description.appendText(" valid according to the XSD ");
	}

	protected void validate(Source source) throws SAXException, IOException {
		schema.newValidator().validate(source);
	}

	protected void validate(Object source) throws SAXException, IOException {
		if (source instanceof Source) {
			validate((Source) source);
		} else if (source instanceof String) {
			Reader reader = new StringReader((String) source);
			validate(new StreamSource(reader));
		} else
			throw new AssertionFailedError("Unexpected type of XML source: " + source.getClass());
	}

	@Override
	protected boolean matchesSafely(Object source, Description description) {
		try {
			validate(source);
		} catch (SAXException e) {
			description.appendText(" fails validation with " + e.getMessage());
			return false;
		} catch (IOException e) {
			throw new Error(e);
		}

		return true;
	}
}
