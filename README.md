BitRafael Public Repository
===========

This repository contains public part of BitRafael related code used in GENERAL BYTES products.
More information about the products can be found here: http://www.generalbytes.com

All source code is released under GPL2.

Overview
========
Every commercial-grade bitcoin project needs a way how to retreive and send information from and to blockchain as quickly as possible.

Bitrafael is a server-side bitcoin blockchain information silo provider that provides JAVA or JSON API to communicate with blockchain.


Content
=======
* **client** - contains client code that can be used to communicate with BitRafael server.
* **common** - contains data transfer and API objects shared by server and client

More Notes
==========
Which API functions Bitrafael Server exposes can be read in <a href="https://github.com/GENERALBYTESCOM/bitrafael_public/blob/master/common/src/com/generalbytes/bitrafael/api/IBitRafaelBitcoinAPI.java">IBitRafaelBitcoinAPI</a> interface.

The easiest way how to interface with API is to use <a href="https://github.com/GENERALBYTESCOM/bitrafael_public/blob/master/client/src/com/generalbytes/bitrafael/api/client/BitRafaelBTCClient.java">BitRafaelBTCClient</a>.

Example use of <a href="https://github.com/GENERALBYTESCOM/bitrafael_public/blob/master/client/src/com/generalbytes/bitrafael/api/client/BitRafaelBTCClient.java">BitRafaelBTCClient</a> is in <a href="https://github.com/GENERALBYTESCOM/bitrafael_public/blob/master/client/src/com/generalbytes/bitrafael/api/client/ClientExample.java">ClientExample.java</a>.

Android
=======
If you want to use this project on Android OS mare sure you:
1. Use in your Android project <a href="https://github.com/GENERALBYTESCOM/bitrafael_public/tree/master/client/libs_android">different</a> bitcoinj libraries.
2. Add <a href="https://github.com/GENERALBYTESCOM/bitrafael_public/tree/master/common/src_android">following</a> sources to your Android project.

Build information
=================
Just run the following command:
```bash
ant
```
