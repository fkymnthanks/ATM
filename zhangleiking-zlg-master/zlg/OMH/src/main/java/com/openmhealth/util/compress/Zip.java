package com.openmhealth.util.compress;

/**
 * 压缩功能接口
 */
public interface Zip {

    byte[] zip(byte[] data);

    byte[] unzip(byte[] data);
}
