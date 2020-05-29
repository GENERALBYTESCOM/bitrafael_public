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
package com.generalbytes.bitrafael.examples.bitcoin;

import com.generalbytes.bitrafael.client.api.IClient;
import com.generalbytes.bitrafael.tools.api.payment.IPaymentListener;
import com.generalbytes.bitrafael.tools.api.payment.Payment;
import com.generalbytes.bitrafael.tools.api.payment.PaymentRequest;
import com.generalbytes.bitrafael.tools.api.payment.PaymentRequestSpec;
import com.generalbytes.bitrafael.tools.payment.*;

import java.math.BigDecimal;


public class PaymentExample {
    public static void main(String[] args) {
        String cryptoCurrency = IClient.BTC;
        String myTrezorAccountXpub = "xpub6CLuyGaJwJngMH6H7v7NGV4jtjwN7JS7QNH6p9TJ2SPEVCvwSaeL9nm6y3zjvV5M4eKPJEzRHyiTLq2probsxzdyxEj2yb17HiEsBXbJXQc";
        final PaymentRequest paymentRequest = PaymentMgr.getInstance().createPaymentRequest(
                new PaymentRequestSpec(new BigDecimal("1"),new BigDecimal("0.05"), "USD", cryptoCurrency, myTrezorAccountXpub), new IPaymentListener() {
                    @Override
                    public void paymentArriving(Payment payment) {
                        System.out.println("Payment is arriving....: " + payment);
                    }

                    @Override
                    public void paymentReceived(Payment payment) {
                        System.out.println("Payment is received, you can send customer goods: " + payment);
                    }

                    @Override
                    public void paymentFailed(Payment payment) {
                        System.out.println("Payment failed due to reason: " + payment.getFailure());
                    }
                }
        );
        System.out.println("paymentRequest = " + paymentRequest);
        System.out.println("Please pay " + paymentRequest.getCryptoAmount() + " " + paymentRequest.getCryptoCurrency() + " to address " + paymentRequest.getCryptoAddress() + " and see what happens.");
        System.out.println("Encode this into QR code: " + paymentRequest.getPaymentURL("Bitrafael Rocks"));
        try {
            Thread.sleep(60*60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
