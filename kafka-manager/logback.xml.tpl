<!--
  ~ Copyright (C) 2009-2015 Typesafe Inc. <http://www.typesafe.com>
  -->
<!-- The default logback configuration that Play uses if no other configuration is provided -->
<configuration>

    <conversionRule conversionWord="coloredLevel" converterClass="play.api.Logger$ColoredLevel" />

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%coloredLevel %logger{15} - %message%n%xException{10}</pattern>
        </encoder>
    </appender>

    <logger name="play" level="${LOG_LEVEL}" />
    <logger name="application" level="${LOG_LEVEL}" />
    <logger name="kafka.manager" level="${LOG_LEVEL}" />

    <!-- Off these ones as they are annoying, and anyway we manage configuration ourself -->
    <logger name="com.avaje.ebean.config.PropertyMapLoader" level="OFF" />
    <logger name="com.avaje.ebeaninternal.server.core.XmlConfigLoader" level="OFF" />
    <logger name="com.avaje.ebeaninternal.server.lib.BackgroundThread" level="OFF" />
    <logger name="com.gargoylesoftware.htmlunit.javascript" level="OFF" />
    <logger name="org.apache.zookeeper" level="INFO"/>

    <root level="WARN">
        <appender-ref ref="STDOUT" />
    </root>

</configuration>
