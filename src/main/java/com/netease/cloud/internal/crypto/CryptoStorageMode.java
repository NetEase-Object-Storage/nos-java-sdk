package com.netease.cloud.internal.crypto;

import com.netease.cloud.services.nos.model.ObjectMetadata;

/**
 * Denotes the different storage modes available for storing the encryption information that
 * accompanies encrypted objects in NOS. The encryption information includes an encrypted envelope
 * symmetric key, an initialization vector, and a description of the encryption materials used
 * during encryption.
 * <p>
 * ObjectMetadata is the default storage mode. If the ObjectMetadata mode is used, then encryption
 * information will be placed in the metadata of the encrypted object stored in NOS. Note: There is
 * a 2 KB limit on the size of the metadata, so be careful that you do not run out of space if you
 * are storing a lot of your own metadata.
 * <p>
 * If the InstructionFile mode is used, then encryption information will be placed in a separate
 * instruction file that will be stored in the same bucket as the encrypted object in NOS. No
 * metadata will be used for storing encryption information.
 */
public enum CryptoStorageMode {
    //使用指令文件保存秘钥
//    InstructionFile,
    //使用元数据保存秘钥
    ObjectMetadata
}
