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

import org.joda.time.DateTimeZone;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * This rule is used to set the Joda timezone for a test, and reset it afterward. This is useful
 * if temporal data is parsed differently in different time zones.
 *
 * @author tnassar
 *
 */
public class TimeZoneTestRule implements TestRule {

    private final DateTimeZone zone;

    TimeZoneTestRule(DateTimeZone zone) {
        this.zone = zone;
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                DateTimeZone defaultTimeZone = DateTimeZone.getDefault();
                try {
                    DateTimeZone.setDefault(zone);
                    base.evaluate();
                } finally {
                    DateTimeZone.setDefault(defaultTimeZone);
                }
            }
        };
    }
}
