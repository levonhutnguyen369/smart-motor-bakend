package backend.datn;

import jakarta.annotation.PostConstruct;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DatnApplication {

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(DatnApplication.class, args);
	}

	@PostConstruct
	public void init() {
		System.out.println("========================");
		System.out.println("MAIL_HOST = " + env.getProperty("MAIL_HOST"));
		System.out.println("MAIL_PORT = " + env.getProperty("MAIL_PORT"));
		System.out.println("MAIL_USERNAME = " + env.getProperty("MAIL_USERNAME"));
		System.out.println("========================");
	}

}
