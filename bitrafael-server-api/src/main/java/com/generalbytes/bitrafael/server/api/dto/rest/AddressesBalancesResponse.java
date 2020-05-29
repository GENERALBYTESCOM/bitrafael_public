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

package com.generalbytes.bitrafael.server.api.dto.rest;

import com.generalbytes.bitrafael.server.api.dto.AddressBalance;

import java.util.List;

public class AddressesBalancesResponse extends APIResponse {
    public AddressesBalancesResponse() {
    }

    public AddressesBalancesResponse(List<AddressBalance> balance) {
        super(true, balance);
    }

    public AddressesBalancesResponse(String message) {
        super(false, message);
    }

    public List<AddressBalance> getData() {
        return (List<AddressBalance>) data;
    }

    public void setData(List<AddressBalance> data) {
        super.data = data;
    }
}
