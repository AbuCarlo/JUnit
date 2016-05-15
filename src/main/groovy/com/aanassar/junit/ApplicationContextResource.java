/*
 * COPYRIGHT, PALANTIR TECHNOLOGIES INC. 2013
 * THIS SOFTWARE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * OWNED BY PALANTIR TECHNOLOGIES INC.
 * UNAUTHORIZED DISCLOSURE TO ANY THIRD PARTY IS STRICTLY PROHIBITED
 *
 * For good and valuable consideration, the receipt and adequacy of which
 * is acknowledged by Palantir and recipient of this file ("Recipient"), the
 * parties agree as follows:
 * This file is being provided subject to the non-disclosure terms by and
 * between Palantir and the Recipient.
 * Palantir solely shall own and hereby retains all rights, title and
 * interest in and to this software (including, without limitation,
 * all patent, copyright, trademark, trade secret and other intellectual
 * property rights) and all copies, modifications and derivative works thereof.
 * Recipient shall and hereby does irrevocably transfer and assign to Palantir
 * all right, title and interest it may have in the foregoing to Palantir and
 * Palantir hereby accepts such transfer. In using this software, Recipient
 * acknowledges that no ownership rights are being conveyed to Recipient. This
 * software shall only be used in conjunction with properly licensed Palantir
 * products or services.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.aanassar.junit;

import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This rule is intended to be used at class scope. It does little more than
 * wrap an {@link ClassPathXmlApplicationContext} in order to be able to use the same
 * configuration for an entire test case, and properly close it at the end.
 * The public static field of this type should be annotated with {@link org.junit.ClassRule}.
 * Note that in order for autowiring to work at all, the Spring configuration here
 * must include {@code <context:annotation-config/>}.
 *
 * @author tnassar
 *
 */
public class ApplicationContextResource extends ExternalResource {

    private ClassPathXmlApplicationContext context;
    private final String location;
    // We really need this just so Spring will have an appender!
    static private final Logger logger = LoggerFactory.getLogger(ApplicationContextResource.class);

    public ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * @param a location for {@link ClassPathXmlApplicationContext}'s constructor
     */
    public ApplicationContextResource(String location) {
        this.location = location;
    }

    @Override
    protected void before() {
        try {
            context = new ClassPathXmlApplicationContext(location);
        } catch (BeansException ex) {
            // We also need a logger because this rule is initialized statically,
            // so exceptions caused by missing resources were being eaten.
            logger.error("Could not load " + location, ex);
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    protected void after() {
        context.close();
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
                        getApplicationContext().getAutowireCapableBeanFactory().autowireBean(bean);
                        statement.evaluate();
                    }
                };
            }
        };
    }
}
