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

package com.generalbytes.bitrafael.api.transaction;


import com.generalbytes.bitrafael.api.dto.InputInfo;
import com.generalbytes.bitrafael.api.dto.OutputInfo;

import java.util.List;

public interface ITransaction {
    String getTxHash();
    String getBlockHash();
    long getTimestamp();
    long getReceivedTimestamp();
    long getSize();
    List<InputInfo> getInputInfos();
    List<OutputInfo> getOutputInfos();
    long getBlockHeight();
    long getConfirmations();


    boolean isDirectionSend();
    long getAmount();
    long getFee();
    String getRelativeToAddress();
}
