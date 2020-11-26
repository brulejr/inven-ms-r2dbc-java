package io.jrb.labs.invenms;

import io.jrb.labs.invenms.config.DatabaseJavaConfig;
import io.jrb.labs.invenms.config.ServiceJavaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
		DatabaseJavaConfig.class,
		ServiceJavaConfig.class
})
public class Application {

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
