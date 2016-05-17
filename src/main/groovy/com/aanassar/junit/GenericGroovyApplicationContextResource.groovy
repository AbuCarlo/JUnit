package com.aanassar.junit

import groovy.util.logging.Slf4j

import org.junit.rules.ExternalResource
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.beans.BeansException
import org.springframework.context.support.GenericGroovyApplicationContext

import com.google.common.base.Throwables

@Slf4j('logger')
public final class GenericGroovyApplicationContextResource extends ExternalResource {

    List<String> locations = []
    final GenericGroovyApplicationContext context = new GenericGroovyApplicationContext()
    Closure closure = {}

    /**
     * Override to set up your specific external resource.
     *
     * @throws Throwable if setup fails (which will disable {@code after}
     */
    @Override
    protected final void before() throws Throwable {
        try {
            context.load(locations as String[]);
            context.with closure
            context.refresh()
        } catch (BeansException ex) {
            // We also need a logger because this rule is initialized statically,
            // so exceptions caused by missing resources were being eaten.
            logger.error("Could not load {}", locations, ex);
            Throwables.propagate(ex)
        }
    }

    /**
     * Closes the application context.
     */
    @Override
    protected final void after() {
        context.close()
    }

    /**
     * The public field declaring the rule for the test case must be annotated with {@link org.junit.Rule}.
     *
     * @param bean the test case class itself, with fields annotated for auto-wiring.
     * @return an instance of {@link TestRule} wrapping the test class instance with auto-wiring
     */
    public TestRule autowire(final Object bean) {

        return new TestRule() {

            @Override
            public Statement apply(final Statement statement, Description description) {
                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        context.autowireCapableBeanFactory.autowireBean(bean);
                        statement.evaluate();
                    }
                };
            }
        };
    }

}
