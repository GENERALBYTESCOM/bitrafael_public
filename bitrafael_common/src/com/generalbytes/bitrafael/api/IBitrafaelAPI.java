package com.generalbytes.bitrafael.api;

import com.generalbytes.bitrafael.api.dto.AmountsPair;
import com.generalbytes.bitrafael.api.dto.TxTemplate;
import com.generalbytes.bitrafael.api.dto.rest.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by b00lean on 27.5.17.
 */
public interface IBitrafaelAPI {

    public static final long FEE_LOW = -1;
    public static final long FEE_MEDIUM = -2;
    public static final long FEE_HIGH = -3;

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
    @Path("/transactions/{txhash}/risk")
    public TxRiskLevelInfoResponse getTransactionRiskLevel(@PathParam("txhash") String txHash);

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
