package com.in28minutes.microservices.currencyconversionservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeProxy currencyExchangeProxy;

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionValue calculateCurrencyConversion(@PathVariable String from, @PathVariable String to,
                                                               @PathVariable BigDecimal quantity){

        HashMap<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        ResponseEntity<CurrencyConversionValue> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                CurrencyConversionValue.class, uriVariables);

        CurrencyConversionValue currencyConversionValue = responseEntity.getBody();

        return new CurrencyConversionValue(
                currencyConversionValue.getId(), from, to , quantity,
                currencyConversionValue.getConversionMultiple(),
                quantity.multiply(currencyConversionValue.getConversionMultiple()),
                currencyConversionValue.getEnvironment() + "through rest template");
    }

    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionValue calculateCurrencyConversionFeign(@PathVariable String from, @PathVariable String to,
                                                               @PathVariable BigDecimal quantity){

        CurrencyConversionValue currencyConversionValue =
                currencyExchangeProxy.retrieveExchangeValue(from, to);

        return new CurrencyConversionValue(
                currencyConversionValue.getId(), from, to , quantity,
                currencyConversionValue.getConversionMultiple(),
                quantity.multiply(currencyConversionValue.getConversionMultiple()),
                currencyConversionValue.getEnvironment() + "in feign");
    }
}
