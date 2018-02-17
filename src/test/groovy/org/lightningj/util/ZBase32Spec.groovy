/************************************************************************
 *                                                                       *
 *  LightningJ                                                           *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU General Public License          *
 *  License as published by the Free Software Foundation; either         *
 *  version 3 of the License, or any later version.                      *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package org.lightningj.util

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll


/**
 * Test fo ZBase32 encoding. The test data has been taken from github project tv42/zbase32
 * to ensure interoperability.
 *
 * Created by Philip Vendil on 2018-02-15.
 */
class ZBase32Spec extends Specification {

    @Shared List encodeBitsTests = [
        // Test cases from the spec
        [0, [] as byte[], ""],
        [1, [0] as byte [], "y"],
        [1, [(int) 128] as byte[], "o"],
        [2, [64] as byte[], "e"],
        [2, [192] as byte[], "a"],
        [10, [0,0] as byte[], "yy"],
        [10, [128, 128] as byte[], "on"],
        [20, [139, 136, 128] as byte[], "tqre"],
        [24, [240, 191, 199] as byte[], "6n9hq"],
        [24, [212, 122, 4] as byte[], "4t7ye"],
        // Note: this test varies from what's in the spec by one character!
        [30, [245, 87, 189, 12] as byte[], "6im54d"],

        // Edge cases we stumbled on, that are not covered above.
        [8, [0xff] as byte[], "9h"],
        [11, [0xff, 0xE0] as byte[], "99o"],
        [40, [0xff, 0xff, 0xff, 0xff, 0xff] as byte[], "99999999"],
        [48, [0xff, 0xff, 0xff, 0xff, 0xff, 0xff] as byte[], "999999999h"],
        [192, [
            0xc0, 0x73, 0x62, 0x4a, 0xaf, 0x39, 0x78, 0x51,
            0x4e, 0xf8, 0x44, 0x3b, 0xb2, 0xa8, 0x59, 0xc7,
            0x5f, 0xc3, 0xcc, 0x6a, 0xf2, 0x6d, 0x5a, 0xaa,
            ] as byte[], "ab3sr1ix8fhfnuzaeo75fkn3a7xh8udk6jsiiko"],

        // Used in the docs.
        [20, [0x10, 0x11, 0x10] as byte[], "nyet"],
        [24, [0x10, 0x11, 0x10] as byte[], "nyety"]
    ]

    @Shared List encodedBytesTest = [
        // Byte-aligned test cases from the spec
        [[240, 191, 199] as byte[], "6n9hq"],
        [[212, 122, 4] as byte[], "4t7ye"],

        // Edge cases we stumbled on, that are not covered above.
        [[0xff] as byte[], "9h"],
        [[0xb5] as byte[], "sw"],
        [[0x34, 0x5a] as byte[], "gtpy"],
        [[0xff, 0xff, 0xff, 0xff, 0xff] as byte[], "99999999"],
        [[0xff, 0xff, 0xff, 0xff, 0xff, 0xff] as byte[], "999999999h"],
        [[
            0xc0, 0x73, 0x62, 0x4a, 0xaf, 0x39, 0x78, 0x51,
            0x4e, 0xf8, 0x44, 0x3b, 0xb2, 0xa8, 0x59, 0xc7,
            0x5f, 0xc3, 0xcc, 0x6a, 0xf2, 0x6d, 0x5a, 0xaa,
        ] as byte[], "ab3sr1ix8fhfnuzaeo75fkn3a7xh8udk6jsiiko"],
    ]

    @Unroll
    def "Verify that encoding 0x#dataHex with #bits bits results in z-base32 encoding #expected"(){
        expect:
        ZBase32.encodeToString(data,bits) == expected

        where:
        dataHex << encodeBitsTests.collect{it.get(1).encodeHex().toString()}
        data << encodeBitsTests.collect{it.get(1)}
        bits << encodeBitsTests.collect{it.get(0)}
        expected << encodeBitsTests.collect{it.get(2)}
    }

    @Unroll
    def "Verify that encode of #dataHex without specifying bits encodes into #expected"(){
        expect:
        ZBase32.encodeToString(data) == expected
        where:
        dataHex << encodedBytesTest.collect{it.get(0).encodeHex().toString()}
        data << encodedBytesTest.collect{it.get(0)}
        expected << encodedBytesTest.collect{it.get(1)}

    }

    @Unroll
    def "Verify that decoding z-base32 encoded #zbase32String to data 0x#dataHex with #bits bits."(){
        expect:
        ZBase32.decode(zbase32String,bits) == data

        where:
        dataHex << encodeBitsTests.collect{it.get(1).encodeHex().toString()}
        data << encodeBitsTests.collect{it.get(1)}
        bits << encodeBitsTests.collect{it.get(0)}
        zbase32String << encodeBitsTests.collect{it.get(2)}
    }

    @Unroll
    def "Verify that decoding z-base32 encoded #zbase32String to data 0x#dataHex without specifying bits."(){
        expect:
        ZBase32.decode(zbase32String) == data

        where:
        dataHex << encodedBytesTest.collect{it.get(0).encodeHex().toString()}
        data << encodedBytesTest.collect{it.get(0)}
        zbase32String << encodedBytesTest.collect{it.get(1)}
    }

    def "Double check with other zbase32 examples from other implementations."(){
        expect:
        ZBase32.encodeToString("Just an arbitrary sentence.".getBytes()) == "jj4zg7bycfznyam1cjwzehubqjh1yh5fp34gk5udcwzy"
        new String(ZBase32.decode( "jj4zg7bycfznyam1cjwzehubqjh1yh5fp34gk5udcwzy")) == "Just an arbitrary sentence."
    }

}
