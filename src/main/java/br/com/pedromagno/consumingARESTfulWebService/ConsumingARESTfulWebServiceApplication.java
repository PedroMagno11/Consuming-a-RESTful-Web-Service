package br.com.pedromagno.consumingARESTfulWebService;

import br.com.pedromagno.consumingARESTfulWebService.domain.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@SpringBootApplication
public class ConsumingARESTfulWebServiceApplication {

	private static final Logger log = LoggerFactory.getLogger(ConsumingARESTfulWebServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ConsumingARESTfulWebServiceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder){
		return builder.build();
	}

	@Bean
	@Profile("!test")
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception{
		return  args -> {
			Quote quote = restTemplate.getForObject(
					"http://localhost:8080/api/random", Quote.class
			);

            log.info(Objects.requireNonNull(quote).toString());
		};
	}
}
