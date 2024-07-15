# Consuming-a-RESTful-Web-Service

An application that consumes a RESTful web service.

## What you will build

You will build an application that uses Spring's `RestTemplate`
to retrieve a random Spring Boot quotation at `http://localhost:8080/api/random`.

## What you need
- A favorite text editor or IDE
- Java 17 or later
- Gradle 7.5+ or Maven 3.5+
- You can also import the code straight into your IDE:
  - Spring Tool Suite (STS)
  - Intellij IDEA
  - VSCode

## Starting with Spring Initializr

1 - Navigate to https://start.spring.io. This service pulls in all the dependencies you need for an application and does most of the setup for you.

2 - Choose either Gradle or Maven and the language you want to use. This guide assumes that you chose Java.

3 - Click Dependencies and select Spring Web.

4 - Click Generate.

5 - Download the resulting ZIP file, which is an archive of a web application that is configured with your choices.

```
If your IDE has the Spring Initializr integration, you can complete this process from your IDE.
```

## Fetching a REST Resource

With project setup complete, you can create a simple application that consumes a RESTful service.

Before you can do so, 
you need a source of REST 
resources. We have provided 
an example of such a 
service at [https://github.com/spring-guides/quoters](https://github.com/spring-guides/quoters). You can run that application in a separate 
terminal and access the result 
at [http://localhost:8080/api/random](http://localhost:8080/api/random). 
That address randomly fetches a quotation about 
Spring Boot and returns it as a JSON document. 
Other valid addresses include [http://localhost:8080/api/](http://localhost:8080/api/) (for all the quotations) and 
[http://localhost:8080/api/1]
(http://localhost:8080/api/1) 
(for the first quotation), 
[http://localhost:8080/api/2]
(http://localhost:8080/api/2) 
(for the second quotation), 
and so on (up to 10 at 
present).

If you request that URL through a web browser or curl, you receive a JSON 
document that looks something like this:

````json
{
  "type": "success",
  "value": {
    "id": 10,
    "quote": "Really loving Spring Boot, makes stand alone Spring apps easy."
  }
}
````

That is easy enough but not terribly useful when fetched through a browser or through curl.

A more useful way to consume a 
REST web service is 
programmatically. 
To help you with that 
task, Spring provides a 
convenient template 
class called RestTemplate. 
RestTemplate makes 
interacting with most 
RESTful services a one-line 
incantation. 
And it can even bind that 
data to custom domain types.

First, you need to create 
a domain class to contain 
the data that you need. 
The following listing shows 
the Quote record class, 
which you can use as 
your domain class:

`src/main/java/br/com/pedromagno/consumingARESTfulWebService/domain/Quote.java`

````java
package br.com.pedromagno.consumingARESTfulWebService.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Quote(String type, Value value ) { }
````

This simple Java record 
class is annotated 
with `@JsonIgnoreProperties` 
from the Jackson JSON 
processing library to 
indicate that any 
properties not bound in 
this type should be ignored.

To directly bind your data 
to your custom types, 
you need to specify the 
variable name to be 
exactly the same as the 
key in the JSON document 
returned from the API. In 
case your variable name 
and key in JSON doc do not 
match, you can use 
`@JsonProperty` annotation 
to specify the exact key 
of the JSON document. 
(This example matches each 
variable name to a JSON key
, so you do not need that 
annotation here.)

You also need an 
additional class, to 
embed the inner 
quotation itself. 
The Value record class 
fills that need and is 
shown in the following 
listing (at `src/main/java/br/com/pedromagno/consumingARESTfulWebService/domain/Value.java`):

````java
package br.com.pedromagno.consumingARESTfulWebService.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Value(Long id, String quote) {
}
````

This uses the same annotations but maps onto other data fields.

## Finishing the Application

The Initializr creates a class with a main() method. 
The following listing 
shows the class the 
Initializr creates (at 
`src/main/java/br/com/pedromagno/consumingARESTfulWebService/ConsumingARESTfulWebServiceApplication.java`):

````java
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
    
	public static void main(String[] args) {
		SpringApplication.run(ConsumingARESTfulWebServiceApplication.class, args);
	}
}
````

Now you need to add a few other things to the ConsumingARESTfulWebServiceApplication class to get it to show quotations from our RESTful source. You need to add:
- A logger, to send output to the log (the console, in this example).
- A `RestTemplate`, which uses the Jackson JSON processing library to process the incoming data.
- A `CommandLineRunner` that runs the `RestTemplate` (and, consequently, fetches our quotation) on startup.

The following listing shows the finished `ConsumingARESTfulWebServiceApplication` class
(at `src/main/java/br/com/pedromagno/consumingARESTfulWebService/ConsumingARESTfulWebApplication.java`):

````java
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
````
Finally, you need to set the server port. The quoters application uses the default server port, 8080, so this application cannot also use the same port. You can set the server port to 8081 by adding the following line to application properties (which the Initializr created for you):

`server.port=8081`

## Running the Application

You should see output similar to the following but with a random quotation:

````java
2024-07-15t14:04:40:40.350-03:00  INFO 19376 --- [consumingARESTfulWebService] [        main] c.ConsumingARESTfulWebServiceApplication : Quote[type='success', value=Value[id=1, quote='Working with Spring Boot is like pair-programming with the Spring developers.']]
````