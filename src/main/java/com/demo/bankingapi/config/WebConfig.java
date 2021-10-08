package com.demo.bankingapi.config;

import com.demo.bankingapi.service.conveter.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ConversionService conversionService;

    public WebConfig(@Lazy ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new AccountResourceToAccountConverter());
        registry.addConverter(new AccountToAccountResourceConverter());
        registry.addConverter(new CustomerResourceToCustomerConverter());
        registry.addConverter(new CustomerToCustomerResourceConverter(conversionService));
        registry.addConverter(new TransactionToTransactionResourceConverter());
    }
}
