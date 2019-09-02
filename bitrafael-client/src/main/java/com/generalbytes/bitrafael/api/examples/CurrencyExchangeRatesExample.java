package com.generalbytes.bitrafael.api.examples;

import com.generalbytes.bitrafael.api.currency.CurrencyRateSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public class CurrencyExchangeRatesExample {
    public static void main(String[] args) {
        CurrencyRateSource rs = new CurrencyRateSource("https://coin.cz");
        Set<String> fiatCurrenciesFrom = rs.getFiatCurrenciesFrom();
        System.out.println("Currencies from:");
        for (String from : fiatCurrenciesFrom) {
            System.out.println(from);
        }
        Set<String> fiatCurrenciesTo = rs.getFiatCurrenciesTo();
        System.out.println("Currencies to:");
        for (String to : fiatCurrenciesTo) {
            System.out.println(to);
        }

        BigDecimal rate = rs.getRate("USD", "CZK");
        System.out.println("USD to CZK today: " + rate);
        rate = rs.getRateHistorical("USD", "CZK", LocalDate.of(2019,1,1));
        System.out.println("USD to CZK on 1.1.2019: " + rate);
    }

}
