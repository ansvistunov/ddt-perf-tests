package com.asw.billing.config;

import com.asw.billing.common.Parameters;
import com.asw.billing.front.rest.BillingRestController;
import com.asw.billing.front.rest.ParametersController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class BillingConfiguration {
    @Bean
    public RouterFunction<ServerResponse> route(BillingRestController restController, ParametersController parametersController) {
        return RouterFunctions
                .route()
                .POST("/cards", restController::createCard)
                //.POST("/cardoperation", restController::processOperation)
                .POST("/cardoperations", restController::processOperations)
                .GET("/cards/{id}", restController::getCard)
                .GET("/parameters", parametersController::setParameter)
                .GET("/storage", parametersController::printStorage)
                .build();


    }


}
