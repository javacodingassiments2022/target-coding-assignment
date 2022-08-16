package com.target.transit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.target.transit.dto.TransitInfo;
import com.target.transit.service.TransitService;
import com.target.transit.validator.TransitValidator;


@SpringBootApplication
public class TransitApplication implements CommandLineRunner{

	private final static Logger logger = LoggerFactory.getLogger(TransitApplication.class);
	
	@Autowired
	private TransitService transitService;
	
	@Autowired
	private TransitValidator transitValidator;
	
	public static void main(String[] args) {
		SpringApplication.run(TransitApplication.class, args);
	}
	
	
	@Override
	public void run(String... args)  {
		if(transitValidator.isInputValid(args)) {
			TransitInfo transitInfo = new TransitInfo(args[0], args[1], args[2]);
			transitService.showNextBusAvailibility(transitInfo);
		}else {
			logger.info("Please retry with valid paramters which are Route name, RouteDirection-(East, West, North, South) and Stop name");
			System.exit(0);
		}
		
	}


	

	
	
}
