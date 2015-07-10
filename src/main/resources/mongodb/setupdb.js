"use strict";

// "db" is whatever db I connect to when I pass this script to mongo
db.nonces.createIndex({'nonce': 1}, {'unique': true});