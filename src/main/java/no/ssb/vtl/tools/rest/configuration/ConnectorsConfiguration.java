package no.ssb.vtl.tools.rest.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "connectors")
public class ConnectorsConfiguration {

    private Boolean caching = false;

    public Boolean getCaching() {
        return caching;
    }

    public void setCaching(Boolean caching) {
        this.caching = caching;
    }
}
