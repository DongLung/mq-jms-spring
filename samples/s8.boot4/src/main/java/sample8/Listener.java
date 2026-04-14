/*
 * Copyright © 2017, 2026 IBM Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package sample8;

import java.util.Date;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.mq.constants.MQConstants;

import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;

@Component
public class Listener {
  static final String ID = "S8.Listener";

  static final int MAX_REDELIVERY = 3;

  static boolean errorHandlerCalled = false;
  static boolean exitWarning = false;
  long prev = new Date().getTime(); // Current epoch in milliseconds

  @JmsListener(destination = Application.qName, containerFactory = "jmsContainerFactory", id = ID)
  @Transactional(rollbackFor = {JMSException.class, RuntimeException.class})
  @Retryable(includes = {JMSException.class, RuntimeException.class},
  delay=500,
  multiplier=2,
  maxDelayString="3s",
  maxRetries = 4)
  @ConcurrencyLimit(limit=1)
  public void receiveMessage(TextMessage msg) throws JMSException, RuntimeException {

    // The retry process swallows the designated exceptions, so we only call the real error handler after the Retryable configuration
    // has expired its attempts. We can do a simple test to see if we are beimg called because of a retry or because of a backout/re-fetch
    // by flipping a flag in the error handler.
    if (!errorHandlerCalled) {
      System.out.println("Called by the retry processor");
    } else {
      System.out.println("Called again after real backout/message re-receive");
      errorHandlerCalled = false;
      // Reset the delay indicator as this is a new iteration
      prev = new Date().getTime();
    }

    if (msg != null) {

      long now = new Date().getTime();
      long delay = (now - prev) / 1000;

      int dc = msg.getIntProperty(MQConstants.MQ_JMSX_DELIVERY_COUNT);

      // Print the current time in the output, so we can see the effects of the Retryable configuration
      System.out.printf("  Delay: %02d Backout: %d Text: %s\n",delay, dc, msg.getText());

      // If we've done a rollback for a message, then its delivery count will increase and we will see it again.
      // For this example, we are therefore going to exit once it's reached a maximum. In a real MQ JMS environment,
      // you can use the queue's BOTHRESH and BOQNAME to automatically requeue the message.
      if (dc > MAX_REDELIVERY) {
        if (!exitWarning) {
          // Only print this once
          System.out.printf("Exiting because delivery count greater than %d\n", MAX_REDELIVERY);
          exitWarning = true;
        }

        // Tell the application to cleanup and exit. We still might be called several times until the main thread
        // notices and completes the shutdown steps.
        Application.ok = false;
      }

      throw new RuntimeException("Doing rollback");

    }
  }
}
