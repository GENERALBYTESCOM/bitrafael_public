/*************************************************************************************
 * Copyright (C) 2016 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.bitrafael.server.api;

import com.generalbytes.bitrafael.server.api.dto.AmountsPair;
import com.generalbytes.bitrafael.server.api.dto.TxTemplate;
import com.generalbytes.bitrafael.server.api.dto.rest.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


public interface IBitrafaelAPI {

    long FEE_LOW = -1;
    long FEE_MEDIUM = -2;
    long FEE_HIGH = -3;

    int AUDIT_RESULT_OK                     = 0;
    int AUDIT_RESULT_FRAUD_ATTEMPT_DETECTED = 1;

    @GET
    @Path("/addresses/{address}")
    AddressBalanceResponse getAddressBalance(@PathParam("address") String address);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addresses/balances")
    AddressesBalancesResponse getAddressesBalances(List<String> addresses);

    @GET
    @Path("/account/{xpub}")
    AccountBalanceResponse getAccountBalance(@PathParam("xpub") String xpub);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/account/balances")
    AccountsBalancesResponse getAccountsBalances(List<String> xpubs);

    @GET
    @Path("/addresses/{address}/last")
    TxInfoResponse getAddressLastTransactionInfo(@PathParam("address") String address);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addresses/audit")
    AddressesAuditResponse getAddressesAudits(List<String> addresses);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addresses/last")
    TxInfosResponse getAddressesLastTransactionInfos(List<String> addresses);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addresses/{address}/info")
    AddressInfoResponse getAddressInfo(@PathParam("address") String address,
                                       @QueryParam("limit") int limit,
                                       @QueryParam("page") int page,
                                       @QueryParam("sort") int sort);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addresses/info")
    AddressesInfoResponse getAddressesInfos(List<String> addresses, @QueryParam("limit") int limit);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addresses/info/{xpub}")
    AddressesInfoResponse getAddressesInfoFromXpub(@PathParam("xpub") String xpub,
                                                   @QueryParam("limit") int limit,
                                                   @QueryParam("page") int page,
                                                   @QueryParam("sort") int sort);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/transactions/info/{xpub}")
    AddressesInfoResponse getAddressesInfoFromXpub(@PathParam("xpub") String accountPUB,
                                                   @QueryParam("lasttxtimestamp") long lastTxTimestamp,
                                                   @QueryParam("limit") int limit,
                                                   @QueryParam("sort") int sort);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/transactions/build")
    TxTemplateResponse buildTransactionTemplate(TxTemplateRequest tr);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/transactions/send")
    TxReceiptResponse sendTransaction(TxTemplate template, @HeaderParam("broadcast") boolean broadcast);

    @GET
    @Path("/transactions/{txhash}/info")
    TxInfoResponse getTransactionInfo(@PathParam("txhash") String txHash);

    @GET
    @Path("/transactions/{txhash}/risk")
    TxRiskLevelInfoResponse getTransactionRiskLevel(@PathParam("txhash") String txHash);

    @GET
    @Path("/transactions/fees")
    TxFeesInfoResponse getTransactionFeesInfo();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/transactions/fees")
    TxFeesResponse getTransactionFees(List<String> txHashes);

    @GET
    @Path("/blockchain/height")
    BlockchainHeightResponse getCurrentBlockchainHeight();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/currencies/convert")
    ConvertAmountsResponse convertAmounts(List<AmountsPair> amountsPairs);

    @GET
    @Path("/currencies/list")
    CurrenciesResponse getCurrencies();

    @GET
    @Path("/currencies/quotes?source={source}")
    QuotesResponse getQuotes(@PathParam("source") String source);

    /**
     * Returns historical data of the currency
     * @param source - i.e. USD
     * @param date - YYYY-MM-DD
     * @return
     */
    @GET
    @Path("/currencies/quotes/historical")
    QuotesResponse getQuotesHistorical(@QueryParam("source") String source, @QueryParam("date") String date);



}
