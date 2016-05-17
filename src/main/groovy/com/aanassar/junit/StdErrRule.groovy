package com.aanassar.junit

import java.nio.charset.Charset

import org.junit.rules.ExternalResource

/** This implementation of a JUnit rule allows output to STDERR to be intercepted for the
 * duration of a test, either to suppress this output, or to make assertions about it afterward.
 * After the test, the rule will revert STDERR to its value beforehand.
 *
 * @author tnassar
 *
 */
public class StdErrRule extends ExternalResource {

    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
    private PrintStream newWriter = new PrintStream(outputStream)
    private PrintStream oldWriter

    public String getContent() {
        newWriter.flush()
        // A PrintStream is always in the system-default encoding.
        new String(outputStream.toByteArray(), Charset.defaultCharset())
    }

    @Override protected void before() {
        oldWriter = System.err
        System.setErr(newWriter)
    }

    @Override protected void after() {
        newWriter.close()
        System.setErr(oldWriter)
    }
}