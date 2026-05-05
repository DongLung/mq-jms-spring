# Sample Programs
This directory contains programs demonstrating some features of Spring Boot JMS programs.

The programs all have a `RUNME.sh`script that uses `gradle` to compile and run the programs. They also have a resource
file with externally-configured properties that control how the connection is made to a queue manager. You may need to
modify those properties to match your environment. The `README` in the root of this repository lists the available
properties.

All samples use the JMS3/Jakarta forms of the Java classes and have been renamed to remove any "jms3" suffix.
Older versions of the samples have been removed.

Most samples are now based on Spring Boot 4. The "boot4" suffix to directory names has been removed, and the "boot3"
suffix added to the holdouts.

## Contents
* s1 - The simplest example that creates two connections to MQ: one to put a message, and the other to act as a
  JMSListener that retrieves the message.
* s2 - Demonstrates use of local transactional control to commit and rollback changes
* s2.tls - Identical to s2 but with information about using a TLS-enabled connection in a Spring Boot 3.1 environment,
  using `SSLBundles` to setup the secure connection. Configuration uses a yaml file instead of a properties file.
* s3 - A request/reply program, with both the requester and responder in the same application. The responder side shows
  how transactions can be controlled within a JMSListener.
* s4.boot3 - Shows how to connect to multiple queue managers in the same application and using an XA transaction
  coordinator to reliably transfer messages between them
* s4a.boot3 - A modified version of s4 using a JmsListener instead of a polling Receiver
* s4n - Functionally identical to s4, but using Narayana as an alternative transaction manager
* s5 - A very simple (and not very interesting) application, but this also shows how to use the Testcontainer enablement
  in the _src/test/java_ directory.
* s6 - Show how the new Spring Boot 4/Framework 7 JmsClient class can be used as an alternative to JmsTemplate
* s7 - Shows how to retrieve CCDT and JWT from an https server to connect to IBM MQ.


### Note: Atomikos samples (s4 and s4a)
The current version of Atomikos does not work with the latest Spring Boot 3, as it uses deprecated and now-removed
functions in Spring. See [this issue](https://github.com/atomikos/transactions-essentials/issues/234) in the Atomikos
repository for more details. However, that issue does contain a workround for the problem, which has now been applied to
the samples here.

Even worse, it does not work at all with Spring Boot 4, where various packages were reorganised. So for now, the samples
in this tree have been kept at Boot 3.

## Other resources
Many other sample programs for MQ can be found in [this repository](https://github.com/ibm-messaging/mq-dev-patterns).
Those examples have demonstrations of additional features and alternative ways to build and run JMS programs. For
example, using `maven` instead of `gradle`.
