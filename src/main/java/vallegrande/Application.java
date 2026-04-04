package vallegrande;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import vallegrande.config.YoutubeApiProperties;
import vallegrande.config.TranslateApiProperties;

@SpringBootApplication
@EnableConfigurationProperties({YoutubeApiProperties.class, TranslateApiProperties.class})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
