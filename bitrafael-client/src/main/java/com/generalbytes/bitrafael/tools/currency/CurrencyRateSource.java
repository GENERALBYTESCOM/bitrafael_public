/*************************************************************************************
 * Copyright (C) 2017 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.bitrafael.tools.currency;

import com.generalbytes.bitrafael.server.api.IBitrafaelAPI;
import com.generalbytes.bitrafael.server.api.IBitrafaelBitcoinAPI;
import com.generalbytes.bitrafael.server.api.dto.rest.CurrenciesResponse;
import com.generalbytes.bitrafael.server.api.dto.rest.QuotesResponse;
import com.generalbytes.bitrafael.tools.api.currency.IRatesProvider;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;


public class CurrencyRateSource implements IRatesProvider {
    private String server;
    private IBitrafaelAPI api;
    private Set<String> fiatCurrenciesSupported;
    private static final String LOCK = Class.class.getName().intern();
    private Map<String,QuotesResponse> cache = new ConcurrentHashMap<String,QuotesResponse>();
    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public CurrencyRateSource() {
        this("https://coin.cz");
    }

    public CurrencyRateSource(String server) {
        this.server = server;
        api = RestProxyFactory.createProxy(IBitrafaelBitcoinAPI.class, server + "/api");
    }

    private void initializeIfNeeded() {
        synchronized (LOCK) {
            if (fiatCurrenciesSupported == null) {
                final CurrenciesResponse currencies = api.getCurrencies();
                if (currencies != null && currencies.isSuccess()) {
                    fiatCurrenciesSupported = new TreeSet<String>(currencies.getCurrencies().keySet());
                }
            }
        }
    }

    @Override
    public Set<String> getFiatCurrenciesFrom() {
        initializeIfNeeded();
        return fiatCurrenciesSupported;
    }


    @Override
    public Set<String> getFiatCurrenciesTo() {
        initializeIfNeeded();
        return fiatCurrenciesSupported;
    }


    @Override
    public BigDecimal getRate(String fromCurrency, String toCurrency) {
        initializeIfNeeded();
        if (!getFiatCurrenciesFrom().contains(fromCurrency)) {
            return BigDecimal.ZERO;
        }
        if (!getFiatCurrenciesTo().contains(toCurrency)) {
            return BigDecimal.ZERO;
        }

        String key = fromCurrency;
        final QuotesResponse resp = cache.get(key);
        if (resp != null) {
            if (System.currentTimeMillis() < (resp.getTimestamp() + 60 * 60 * 1000)) {
                return resp.getQuotes().get(fromCurrency + toCurrency);
            }
        }


        final QuotesResponse quotes = api.getQuotes(fromCurrency);
        if (quotes != null && quotes.isSuccess()) {
            quotes.setTimestamp(System.currentTimeMillis());
            cache.put(key, quotes);
            return quotes.getQuotes().get(fromCurrency+toCurrency);
        }
        return null;
    }

    @Override
    public BigDecimal getRateHistorical(String fromCurrency, String toCurrency, LocalDate date) {
        initializeIfNeeded();
        if (!getFiatCurrenciesFrom().contains(fromCurrency)) {
            return BigDecimal.ZERO;
        }
        if (!getFiatCurrenciesTo().contains(toCurrency)) {
            return BigDecimal.ZERO;
        }

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String key = fromCurrency + "_" + date.format(df);

        final QuotesResponse resp = cache.get(key);
        if (resp != null) {
            if (System.currentTimeMillis() < (resp.getTimestamp() + 60 * 60 * 1000)) {
                return resp.getQuotes().get(fromCurrency + toCurrency);
            }
        }


        final QuotesResponse quotes = api.getQuotesHistorical(fromCurrency, date.format(df));
        if (quotes != null && quotes.isSuccess()) {
            quotes.setTimestamp(System.currentTimeMillis());
            cache.put(key, quotes);
            return quotes.getQuotes().get(fromCurrency+toCurrency);
        }
        return null;
    }

    //    public static void main(String[] args) {
//        System.setProperty("org.slf4j.simpleLogger.log.si.mazi.rescu","trace");
//
//        CurrencyRateSource r = new CurrencyRateSource();
//        final Set<String> fiatCurrenciesFrom = r.getFiatCurrenciesFrom();
//        for (String s : fiatCurrenciesFrom) {
//            System.out.println("s = " + s);
//        }
//
//        BigDecimal rate = r.getRate("USD","CZK");
//        try {
//            Thread.sleep(60 * 1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("rate = " + rate);
//
//        rate = r.getRate("USD","EUR");
//        System.out.println("rate = " + rate);
//    }
}
