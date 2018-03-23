Bitrafael Public Repository
===========

This repository contains public part of Bitrafael related code used in GENERAL BYTES products.
More information about the products can be found here: http://www.generalbytes.com

All source code here is released under GPL2.

Overview
========
Every commercial-grade bitcoin project needs a way how to retreive and send information from blockchain as quickly as possible.

Bitrafael is a server-side bitcoin blockchain information silo provider that provides JAVA or JSON API to communicate with blockchain.


Content
=======
* **client** - contains client code and examples that can be used to communicate with Bitrafael server.
* **common** - contains data transfer and API objects shared by server and client

Notes
==========
* Best is to read examples of use of this API. List of examples use can be found <a href="https://github.com/GENERALBYTESCOM/bitrafael_public/tree/master/bitrafael_client/src/com/generalbytes/bitrafael/api/examples">here</a>.
* Which API functions Bitrafael Server exposes can be read in <a href="https://github.com/GENERALBYTESCOM/bitrafael_public/blob/master/bitrafael_common/src/com/generalbytes/bitrafael/api/IBitrafaelBitcoinAPI.java">IBitrafaelBitcoinAPI</a> interface.
* There is also BIP32,29,44 compatible WalletTools utility class that simplifies work with deterministic wallets <a href="https://github.com/GENERALBYTESCOM/bitrafael_public/blob/master/bitrafael_client/src/com/generalbytes/bitrafael/api/wallet/WalletTools.java">WalletTools</a>.


Build information
=================
Just run the following command:
```bash
./gradlew build
```
