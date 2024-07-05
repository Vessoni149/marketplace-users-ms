package com.microservice.userssecurity;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.function.Function;


@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class UsersSecurityApplication {

	public static void main(String[] args) {
		// Verificar si estamos en Render
		boolean isRender = System.getenv("RENDER") != null;

		Function<String, String> getEnv;
		if (!isRender) {
			// Si no estamos en Render, cargamos el .env como antes
			Dotenv dotenv = Dotenv.load();
			getEnv = dotenv::get;
			System.out.println("Running in development mode");
		} else {
			// Si estamos en Render, usamos las variables de entorno del sistema
			getEnv = System::getenv;
			System.out.println("Running in Render (production mode)");
		}

		setEnvironmentVariables(getEnv);

		// Opcionalmente, puedes imprimir algunas variables para verificar
		System.out.println("DB URL: " + System.getProperty("spring.datasource.url"));
		System.out.println("Eureka URL: " + System.getProperty("eureka.client.serviceUrl.defaultZone"));

		SpringApplication.run(UsersSecurityApplication.class, args);
	}

	private static void setEnvironmentVariables(Function<String, String> getEnv) {
		// Configuración de la base de datos
		System.setProperty("spring.datasource.url", getEnv.apply("DB_URL"));
		System.setProperty("spring.datasource.username", getEnv.apply("DB_USERNAME"));
		System.setProperty("spring.datasource.password", getEnv.apply("DB_PASSWORD"));

		// Configuración de Eureka y servicios
		System.setProperty("eureka.client.serviceUrl.defaultZone", getEnv.apply("EUREKA_URL"));
		System.setProperty("feign.carritoApi.url", getEnv.apply("CARRITO_API_URL"));

		// Configuración de Cloudinary
		System.setProperty("cloudinary.cloud_name", getEnv.apply("CLOUDINARY_CLOUD_NAME"));
		System.setProperty("cloudinary.api_key", getEnv.apply("CLOUDINARY_API_KEY"));
		System.setProperty("cloudinary.api_secret", getEnv.apply("CLOUDINARY_API_SECRET"));

		// Configuración de Spring Security
		System.setProperty("spring.security.user.name", getEnv.apply("SPRING_SECURITY_USER_NAME"));
		System.setProperty("spring.security.user.password", getEnv.apply("SPRING_SECURITY_USER_PASSWORD"));
		System.setProperty("spring.security.user.roles", getEnv.apply("SPRING_SECURITY_USER_ROLES"));

		// Configuración de JWT
		System.setProperty("jwt.secret.key", getEnv.apply("JWT_SECRET_KEY"));
		System.setProperty("jwt.time.expiration", getEnv.apply("JWT_TIME_EXPIRATION"));

		// Configuración de Stripe
		System.setProperty("stripe.secret.key", getEnv.apply("STRIPE_SECRET_KEY"));
		System.setProperty("stripe.public.key", getEnv.apply("STRIPE_PUBLIC_KEY"));
	}
}
