package programmerzamannow.restful;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BelajarSpringResTfulApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BelajarSpringResTfulApiApplication.class, args);

		System.out.println("\nApp is running\n");
	}

}
