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

package com.generalbytes.bitrafael.api;

import com.generalbytes.bitrafael.api.dto.AmountsPair;
import com.generalbytes.bitrafael.api.dto.TxTemplate;
import com.generalbytes.bitrafael.api.dto.rest.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/bitcoin/v1")
@Produces(MediaType.APPLICATION_JSON)
public interface IBitrafaelBitcoinAPI {

    @GET
    @Path("/addresses/{address}")
    public AddressBalanceResponse getAddressBalance(@PathParam("address") String address);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addresses/balances")
    public AddressesBalancesResponse getAddressesBalances(List<String> addresses);

    @GET
    @Path("/account/{xpub}")
    public AccountBalanceResponse getAccountBalance(@PathParam("xpub") String xpub);


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/account/balances")
    public AccountsBalancesResponse getAccountsBalances(List<String> xpubs);


    @GET
    @Path("/addresses/{address}/last")
    public TxInfoResponse getAddressLastTransactionInfo(@PathParam("address") String address);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addresses/last")
    public TxInfosResponse getAddressesLastTransactionInfos(List<String> addresses);


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addresses/{address}/info")
    AddressInfoResponse getAddressInfo(@PathParam("address") String address, @QueryParam("limit") int limit);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addresses/info")
    AddressesInfoResponse getAddressesInfos(List<String> addresses, @QueryParam("limit") int limit);


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/transactions/build")
    public TxTemplateResponse buildTransactionTemplate(TxTemplateRequest tr);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/transactions/send")
    public TxReceiptResponse sendTransaction(TxTemplate template, @HeaderParam("broadcast") boolean broadcast);

    @GET
    @Path("/transactions/{txhash}/info")
    public TxInfoResponse getTransactionInfo(@PathParam("txhash") String txHash);

    @GET
    @Path("/transactions/fees")
    public TxFeesInfoResponse getTransactionFeesInfo();

    @GET
    @Path("/blockchain/height")
    public BlockchainHeightResponse getCurrentBlockchainHeight();


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/currencies/convert")
    public ConvertAmountsResponse convertAmounts(List<AmountsPair> amountsPairs);

}
