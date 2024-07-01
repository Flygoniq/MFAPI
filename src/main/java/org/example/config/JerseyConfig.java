package org.example.config;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath("/")
@OpenAPIDefinition(info = @Info(description = "API", version = "1.0.0", title = "MFAPI"),
  servers = {
    @Server(
      description = "Base URL configuration",
//      url = "http://{host}:8080/{basePath}",
      url = "http://{basePath}",
      variables = {
        @ServerVariable(name = "basePath", description = "base path", defaultValue = "localhost:8080"),
//        @ServerVariable(name = "host", description = "host", defaultValue = "localhost")
      })
  })
public class JerseyConfig extends ResourceConfig {

  public JerseyConfig() {
    packages(true, "org.example.rest");
    configureJackson();
    register(OpenApiResource.class);
    register(MultiPartFeature.class);
  }

  private void configureJackson() {
    final JacksonJsonProvider provider = new JacksonJsonProvider();
    provider.setMapper(JacksonConfig.getMapper());
    register(provider);
  }

}
