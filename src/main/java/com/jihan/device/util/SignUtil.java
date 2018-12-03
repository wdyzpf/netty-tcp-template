package com.jihan.device.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

import com.jihan.device.core.Constants;

/**
 * 
 * @Author jihan
 * @Time 2018年8月22日 下午3:31:14
 * @Version 1.0
 * Description: 签名工具
 *
 */
public class SignUtil {

    /**
     * 计算签名
     * @param secret APP密钥
     * @return 签名后的字符串
     */
    public static String sign(String secret, String json) {
        try {
            Mac hmacSha256 = Mac.getInstance(Constants.HMAC_SHA256);
            byte[] keyBytes = secret.getBytes(Constants.ENCODING);
            hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, Constants.HMAC_SHA256));
            return new String(Base64.encodeBase64(hmacSha256.doFinal(json.getBytes(Constants.ENCODING))), Constants.ENCODING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
