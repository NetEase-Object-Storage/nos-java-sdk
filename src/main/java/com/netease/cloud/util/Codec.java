package com.netease.cloud.util;

interface Codec {
    public byte[] encode(byte[] src);

    public byte[] decode(byte[] src, final int length);
}
