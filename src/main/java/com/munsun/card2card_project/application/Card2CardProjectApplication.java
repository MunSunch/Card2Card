package com.munsun.card2card_project.application;

import com.munsun.card2card_project.application.config.ApplicationContextProvider;
import com.munsun.card2card_project.application.dto.in.CardBalanceDtoIn;
import com.munsun.card2card_project.application.dto.in.CardDtoIn;
import com.munsun.card2card_project.application.service.impl.CardServiceImpl;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@OpenAPIDefinition
public class Card2CardProjectApplication implements CommandLineRunner {
	@Override
	public void run(String... args) throws Exception {
//		var cardService = ApplicationContextProvider.getApplicationContext().getBean(CardServiceImpl.class);
//		var c1 = cardService.add(new CardDtoIn("RUR"));
//		c1 = cardService.upBalance(new CardBalanceDtoIn(c1.cardNumber(), 1000L));
//		var c2 = cardService.add(new CardDtoIn("RUR"));
//		c2 = cardService.upBalance(new CardBalanceDtoIn(c2.cardNumber(), 1000L));
//		log.info("Card: number={}, validTill={}, cvv={}, value={}, currency={}",
//				c1.cardNumber(), c1.cardValidTill(), c1.cardCVV(), c1.value(),
//				c1.currency());
//		log.info("Card: number={}, validTill={}, cvv={}, value={}, currency={}",
//				c2.cardNumber(), c2.cardValidTill(), c2.cardCVV(), c2.value(),
//				c2.currency());
	}

	public static void main(String[] args) {
		SpringApplication.run(Card2CardProjectApplication.class, args);
	}
}
