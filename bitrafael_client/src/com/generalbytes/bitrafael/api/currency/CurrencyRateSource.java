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
package com.generalbytes.bitrafael.api.currency;

import com.generalbytes.bitrafael.api.IBitrafaelAPI;
import com.generalbytes.bitrafael.api.IBitrafaelBitcoinAPI;
import com.generalbytes.bitrafael.api.dto.rest.CurrenciesResponse;
import com.generalbytes.bitrafael.api.dto.rest.QuotesResponse;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;


public class CurrencyRateSource implements IRatesProvider{
    private String server;
    private IBitrafaelAPI api;
    private Set<String> fiatCurrenciesSupported;
    private static final String LOCK = Class.class.getName().intern();
    private Map<String,QuotesResponse> cache = new ConcurrentHashMap<String,QuotesResponse>();


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

        final QuotesResponse resp = cache.get(fromCurrency);
        if (resp != null) {
            if (System.currentTimeMillis()/1000 < (resp.getTimestamp() + 30 * 60)) {
                return resp.getQuotes().get(fromCurrency + toCurrency);
            }
        }


        final QuotesResponse quotes = api.getQuotes(fromCurrency);
        if (quotes != null && quotes.isSuccess()) {
            cache.put(fromCurrency,quotes);
            return quotes.getQuotes().get(fromCurrency+toCurrency);
        }
        return null;
    }

    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.log.si.mazi.rescu","trace");

        CurrencyRateSource r = new CurrencyRateSource();
        final Set<String> fiatCurrenciesFrom = r.getFiatCurrenciesFrom();
        for (String s : fiatCurrenciesFrom) {
            System.out.println("s = " + s);
        }

        BigDecimal rate = r.getRate("USD","CZK");
        System.out.println("rate = " + rate);

        rate = r.getRate("USD","CZK");
        System.out.println("rate = " + rate);
    }
}
