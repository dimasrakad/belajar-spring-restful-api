package programmerzamannow.restful.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "app.photo")
@Getter
@Setter
public class PhotoStorageProperties {
    private String uploadDir;
}
