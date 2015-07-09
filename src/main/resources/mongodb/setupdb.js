"use strict";

var conn = new Mongo();
var db = conn.getDB('random-rex-test');

db.nonces.createIndex({'nonce': 1}, {'unique': true})