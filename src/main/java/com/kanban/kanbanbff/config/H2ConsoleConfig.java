package com.kanban.kanbanbff.config;

import jakarta.annotation.PreDestroy;
import org.h2.tools.Server;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * H2ConsoleAutoConfiguration a disparu de spring-boot-autoconfigure en
 * Spring Boot 4 et la servlet H2 (org.h2.server.web.WebServlet) reste sur
 * l'ancien namespace javax.servlet, incompatible avec le Tomcat 11 /
 * Jakarta EE utilise ici. On demarre donc le serveur web H2 autonome
 * (son propre port, hors du conteneur servlet Spring) pour un usage local.
 * Desactive dans les tests (port fixe, cf. src/test/resources/application.yml)
 * pour eviter un conflit si l'app tourne deja en local pendant `mvn test`.
 */
@Component
@ConditionalOnProperty(prefix = "app.h2-console", name = "enabled", havingValue = "true", matchIfMissing = true)
public class H2ConsoleConfig {

    private final Server webServer;

    public H2ConsoleConfig() throws SQLException {
        this.webServer = Server.createWebServer("-webPort", "8082").start();
    }

    @PreDestroy
    public void stop() {
        webServer.stop();
    }
}
