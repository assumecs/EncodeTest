package com.n22.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class EDcodeUtil
{
  @SuppressWarnings("unused")
private static final int ITERATIONS = 1;
  private static Provider provider = new BouncyCastleProvider();

  private static final char[] HEX = { 
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

  private static final Charset CHARSET = Charset.forName("UTF-8");

  static
  {
    Security.addProvider(provider);
  }

  public static byte[] flashDecode(String data) {
    char[] s = data.toCharArray();
    int len = s.length;
    byte[] r = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      int k1 = s[i] - '0';
      k1 -= (k1 > 9 ? 7 : 0);
      int k2 = s[(i + 1)] - '0';
      k2 -= (k2 > 9 ? 7 : 0);
      r[(i / 2)] = ((byte)(k1 << 4 | k2));
    }

    return r;
  }

  public static String md5(String data) {
    return md5(data, false);
  }
  public static String md5(String data, boolean hashAsBase64) {
    return encode(data, hashAsBase64, "MD5");
  }

  public static String sha1(String data) {
    return encode(data, false, "SHA-1");
  }
  public static String sha1(String data, boolean hashAsBase64) {
    return encode(data, hashAsBase64, "SHA-1");
  }

  private static String encode(String data, boolean hashAsBase64, String algorithm) {
    MessageDigest messageDigest = getMessageDigest(algorithm);

    byte[] digest = messageDigest.digest(utf8Encode(data));

    for (int i = 1; i < 1; i++) {
      digest = messageDigest.digest(digest);
    }

    if (hashAsBase64) {
      return base64Encode(digest);
    }
    return hexEncode(digest);
  }

  private static MessageDigest getMessageDigest(String algorithm) throws IllegalArgumentException {
    try {
      return MessageDigest.getInstance(algorithm, provider); } catch (NoSuchAlgorithmException e) {
    }
    throw new IllegalArgumentException("No such algorithm [" + algorithm + "]");
  }

  public static String base64Encode(String data)
  {
    return base64Encode(utf8Encode(data));
  }
  public static String base64Encode(byte[] data) {
    return utf8Decode(Base64.encodeBase64(data));
  }
  public static String base64Decode(String data) {
    return utf8Decode(Base64.decodeBase64(utf8Encode(data)));
  }

  public static byte[] genHmacMD5Key() {
    return genHmacKey("HmacMD5");
  }
  public static byte[] genHmacSHA1Key() {
    return genHmacKey("HmacSHA1");
  }
  public static byte[] genHmacSHA256Key() {
    return genHmacKey("HmacSHA256");
  }
  public static byte[] genHmacSHA384Key() {
    return genHmacKey("HmacSHA384");
  }
  public static byte[] genHmacSHA512Key() {
    return genHmacKey("HmacSHA512");
  }
  private static byte[] genHmacKey(String algorithm) {
    try {
      KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm, provider);
      SecretKey secretKey = keyGenerator.generateKey();

      return secretKey.getEncoded(); } catch (NoSuchAlgorithmException e) {
    }
    throw new IllegalArgumentException("No such algorithm [" + algorithm + "]");
  }

  public static byte[] hmacMD5(byte[] data, byte[] key)
  {
    return hmac(data, key, "HmacMD5");
  }
  public static byte[] hmacSHA1(byte[] data, byte[] key) {
    return hmac(data, key, "HmacSHA1");
  }
  public static byte[] hmacSHA256(byte[] data, byte[] key) {
    return hmac(data, key, "HmacSHA256");
  }
  public static byte[] hmacSHA384(byte[] data, byte[] key) {
    return hmac(data, key, "HmacSHA384");
  }
  public static byte[] hmacSHA512(byte[] data, byte[] key) {
    return hmac(data, key, "HmacSHA512");
  }
  private static byte[] hmac(byte[] data, byte[] key, String algorithm) {
    try {
      SecretKey secretKey = new SecretKeySpec(key, algorithm);

      Mac mac = Mac.getInstance(secretKey.getAlgorithm(), provider);
      mac.init(secretKey);

      return mac.doFinal(data);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalArgumentException("No such algorithm [" + algorithm + "]");
    } catch (InvalidKeyException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  public static byte[] desEncode(byte[] data, byte[] key) {
    return des(data, key, 1);
  }
  public static String desEncodeAsHex(byte[] data, byte[] key) {
    return hexEncode(des(data, key, 1));
  }
  public static String desEncodeAsHex(String data, String key) {
    return hexEncode(des(utf8Encode(data), utf8Encode(key), 1));
  }
  public static String desEncodeAsBase64(String data, String key) {
    return base64Encode(des(utf8Encode(data), utf8Encode(key), 1));
  }
  public static byte[] desDecode(byte[] data, byte[] key) {
    return des(data, key, 2);
  }
  public static byte[] desDecodeForHex(String dataHex, byte[] key) {
    return des(hexDecode(dataHex), key, 2);
  }
  public static String desDecodeForHexAsString(String dataHex, String key) {
    return utf8Decode(des(hexDecode(dataHex), utf8Encode(key), 2));
  }
  public static String desDecodeForBase64AsString(String dataBase64, String key) {
    return utf8Decode(des(Base64.decodeBase64(utf8Encode(dataBase64)), utf8Encode(key), 2));
  }
  private static byte[] des(byte[] data, byte[] key, int opMode) {
    try {
      DESKeySpec desKey = new DESKeySpec(key);
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES", provider);
      SecretKey secureKey = keyFactory.generateSecret(desKey);

      Cipher cipher = Cipher.getInstance("DES", provider);

      SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
      cipher.init(opMode, secureKey, secureRandom);

      return cipher.doFinal(data);
    } catch (Exception e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  public static byte[] aesEncode(byte[] data, byte[] key) {
    return aes(data, key, 128, 1);
  }
  public static String aesEncodeAsHex(byte[] data, byte[] key) {
    return hexEncode(aes(data, key, 128, 1));
  }
  public static String aesEncodeAsHex(String data, String key) {
    if (data == null) return null;
    return hexEncode(aes(utf8Encode(data), utf8Encode(key), 128, 1));
  }
  public static String aesEncodeAsBase64(String data, String key) {
    return base64Encode(aes(utf8Encode(data), utf8Encode(key), 128, 1));
  }
  public static byte[] aesDecode(byte[] data, byte[] key) {
    return aes(data, key, 128, 2);
  }
  public static byte[] aesDecodeForHex(String dataHex, byte[] key) {
    return aes(hexDecode(dataHex), key, 128, 2);
  }
  public static String aesDecodeForHexAsString(String dataHex, String key) {
    if (dataHex == null) return null;
    return utf8Decode(aes(hexDecode(dataHex), utf8Encode(key), 128, 2));
  }
  public static String aesDecodeForBase64AsString(String dataBase64, String key) {
    if (dataBase64 == null) return null;
    return utf8Decode(aes(Base64.decodeBase64(utf8Encode(dataBase64)), utf8Encode(key), 128, 2));
  }
  private static byte[] aes(byte[] data, byte[] key, int keyLen, int opMode) {
    try {
      KeyGenerator kgen = KeyGenerator.getInstance("AES", provider);
      SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
      secureRandom.setSeed(key);
      kgen.init(keyLen, secureRandom);
      SecretKey secretKey = kgen.generateKey();
      SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

      Cipher cipher = Cipher.getInstance("AES", provider);
      cipher.init(opMode, keySpec);

      return cipher.doFinal(data);
    } catch (Exception e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  public static long ipToLong(String ipStr)
  {
    if (ipStr.equals("0:0:0:0:0:0:0:1")) {
      return 0L;
    }

    String[] ipArr = ipStr.split("\\.");
    long[] ip = new long[4];
    ip[0] = Long.parseLong(ipArr[0]);
    ip[1] = Long.parseLong(ipArr[1]);
    ip[2] = Long.parseLong(ipArr[2]);
    ip[3] = Long.parseLong(ipArr[3]);

    return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
  }

  public static String ipToString(long ipLong) {
    StringBuffer ip = new StringBuffer();
    ip.append(String.valueOf(ipLong >>> 24));
    ip.append(".");
    ip.append(String.valueOf((ipLong & 0xFFFFFF) >>> 16));
    ip.append(".");
    ip.append(String.valueOf((ipLong & 0xFFFF) >>> 8));
    ip.append(".");
    ip.append(String.valueOf(ipLong & 0xFF));

    return ip.toString();
  }

  public static String uuid() {
    return UUID.randomUUID().toString();
  }

  public static String unescape(String src) {
    StringBuffer tmp = new StringBuffer();
    tmp.ensureCapacity(src.length());
    int lastPos = 0; int pos = 0;

    while (lastPos < src.length()) {
      pos = src.indexOf("%", lastPos);
      if (pos == lastPos) {
        if (src.charAt(pos + 1) == 'u') {
          char ch = (char)Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
          tmp.append(ch);
          lastPos = pos + 6;
        } else {
          char ch = (char)Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
          tmp.append(ch);
          lastPos = pos + 3;
        }
      }
      else if (pos == -1) {
        tmp.append(src.substring(lastPos));
        lastPos = src.length();
      } else {
        tmp.append(src.substring(lastPos, pos));
        lastPos = pos;
      }

    }

    return tmp.toString();
  }

  public static String md5Encode(String data, String encoding) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(data.getBytes(encoding));
      return hexEncode(md.digest());
    } catch (Exception e) {
      throw new IllegalArgumentException("Encoding failed", e);
    }
  }

  public static String hexEncode(byte[] bytes)
  {
    int nBytes = bytes.length;
    char[] result = new char[2 * nBytes];

    int j = 0;
    for (int i = 0; i < nBytes; i++)
    {
      result[(j++)] = HEX[((0xF0 & bytes[i]) >>> 4)];

      result[(j++)] = HEX[(0xF & bytes[i])];
    }

    return new String(result);
  }
  public static byte[] hexDecode(CharSequence s) {
    int nChars = s.length();

    if (nChars % 2 != 0) {
      throw new IllegalArgumentException("Hex-encoded string must have an even number of characters");
    }

    byte[] result = new byte[nChars / 2];

    for (int i = 0; i < nChars; i += 2) {
      int msb = Character.digit(s.charAt(i), 16);
      int lsb = Character.digit(s.charAt(i + 1), 16);

      if ((msb < 0) || (lsb < 0)) {
        throw new IllegalArgumentException("Non-hex character in input: " + s);
      }
      result[(i / 2)] = ((byte)(msb << 4 | lsb));
    }
    return result;
  }

  public static byte[] utf8Encode(CharSequence string)
  {
    try {
      ByteBuffer bytes = CHARSET.newEncoder().encode(CharBuffer.wrap(string));
      byte[] bytesCopy = new byte[bytes.limit()];
      System.arraycopy(bytes.array(), 0, bytesCopy, 0, bytes.limit());

      return bytesCopy;
    } catch (CharacterCodingException e) {
      throw new IllegalArgumentException("Encoding failed", e);
    }
  }

  public static String utf8Decode(byte[] bytes) {
    try { return CHARSET.newDecoder().decode(ByteBuffer.wrap(bytes)).toString();
    } catch (CharacterCodingException e) {
      throw new IllegalArgumentException("Decoding failed", e);
    }
  }

  public static byte[] gbk2utf8(String chenese) {
    char[] c = chenese.toCharArray();
    byte[] fullByte = new byte[3 * c.length];
    for (int i = 0; i < c.length; i++) {
      int m = c[i];
      String word = Integer.toBinaryString(m);

      StringBuffer sb = new StringBuffer();
      int len = 16 - word.length();
      for (int j = 0; j < len; j++) {
        sb.append("0");
      }
      sb.append(word);
      sb.insert(0, "1110");
      sb.insert(8, "10");
      sb.insert(16, "10");

      byte[] bf = new byte[3];
      bf[0] = Integer.valueOf(sb.substring(0, 8), 2).byteValue();
      fullByte[(i * 3)] = bf[0];
      bf[1] = Integer.valueOf(sb.substring(8, 16), 2).byteValue();
      fullByte[(i * 3 + 1)] = bf[1];
      bf[2] = Integer.valueOf(sb.substring(16), 2).byteValue();
      fullByte[(i * 3 + 2)] = bf[2];
    }

    return fullByte;
  }

  @SuppressWarnings("unused")
public static void main(String[] args)throws Exception
  {
    String s = ""+
			"            <Order> "+
			"                <TBOrderId>192238340754966</TBOrderId> "+
			"                <TotalPremium>100000</TotalPremium> "+
			"                <PostFee>0</PostFee> "+
			"                <InsBeginDate xsi:nil=\"true\"/> "+
			"                <InsEndDate xsi:nil=\"true\"/> "+
			"                <InsPeriod xsi:nil=\"true\"/> "+
			"                <ApplyNum>1</ApplyNum> "+
			"                <Item> "+
			"                    <ItemId>2000040097330</ItemId> "+
			"                    <SkuRiskCode>TBLife001</SkuRiskCode> "+
			"                    <ProductCode xsi:nil=\"true\"/> "+
			"                    <ProductName>万能险</ProductName> "+
			"                    <Amount xsi:nil=\"true\"/> "+
			"                    <Premium>100000</Premium> "+
			"                    <ActualPremium>100000</ActualPremium> "+
			"                    <DiscountRate>10000</DiscountRate> "+
			"                </Item> "+
			"                <PolicyNo xsi:nil=\"true\"/> "+
			"            </Order> "+
			"<ApplyInfo> "+
            "<Holder> "+
            "    <CustomList> "+
            "        <Custom key=\"HolderName\">a利王bc</Custom> "+
            "        <Custom key=\"save-holder\">1</Custom> "+
            "        <Custom key=\"HolderEmail\">177006966@qq.com</Custom> "+
            "        <Custom key=\"HolderMobile\">18911103086</Custom> "+
            "        <Custom key=\"HolderCardType\">4</Custom> "+
            "        <Custom key=\"HolderCardNo\">120221198801010081</Custom> "+
            "        <Custom key=\"HolderBirthday\">1990-01-01</Custom> "+
            "        <Custom key=\"HolderSex\">2</Custom> "+
            "        <Custom key=\"HolderResidentProvince\">340000</Custom> "+
            "        <Custom key=\"HolderResidentCity\">340100</Custom> "+
            "        <Custom key=\"HolderAddress\">啦啦啦啦啦</Custom> "+
            "        <Custom key=\"HolderZip\">200000</Custom> "+
            "    </CustomList> "+
            "</Holder> "+
            "<InsuredInfo> "+
            "    <IsHolder>0</IsHolder> "+
            "    <InsuredList> "+
            "        <Insured> "+
            "            <CustomList> "+
            "                <Custom key=\"InsuredRelation\">1</Custom> "+
            "            </CustomList> "+
            "            <BenefitInfo> "+
            "                <IsLegal>1</IsLegal> "+
            "                <BenefitList/> "+
            "            </BenefitInfo> "+
            "        </Insured> "+
            "    </InsuredList> "+
            "</InsuredInfo> "+
            "<OtherInfo> "+
            "    <CustomList/> "+
            "</OtherInfo> "+
            "<RefundInfo> "+
            "    <CustomList/> "+
            "</RefundInfo> "+
        "</ApplyInfo>";
//    aes加解密
//    String r1 = aesEncodeAsBase64(s, "rtyuikl@#$%^&*(976fghjklfds2389");
//    System.out.println(r1);
//    System.out.println(aesDecodeForBase64AsString(r1, "rtyuikl@#$%^&*(976fghjklfds2389"));
//    
//    r1 = aesEncodeAsHex(s, "rtyuikl@#$%^&*(976fghjklfds2389");
//    System.out.println(r1);
//    System.out.println(aesDecodeForHexAsString(r1, "rtyuikl@#$%^&*(976fghjklfds2389"));
    
    String filePath = "E:\\EPad2\\Json\\encodetest.txt";
//    FileReader fileReader = new FileReader(filePath);
    
    StringBuffer sb = null;
    BufferedReader br = null;
	try {
		br = new BufferedReader(new FileReader(filePath));
		sb = new StringBuffer();
		for(String buf = br.readLine();buf != null; buf = br.readLine()){
			System.out.println(buf);
			sb.append(buf.trim());
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally{
		if(br != null){
			br.close();
		}
	}
    System.out.println(sb+"\n");
    
//    JsonUtil.
    
//    String s2 = "qfVfQJgYp4dPtp5I8gcPZmyfy+9EY9XZOFtwedWsLx4T8jt1aAB9LaCE2+4MccZC/uws8DlwWLenaE9biTe+gIbg1heFuuuz/bQVim4cCYq7YChE04+nMyG09A36xbLU/iobrqDF6O5rMXd8uiug3D5BUT99qoRqj38O8yUxoDQ8qL50GG7mRfwMNhbHTLPU";
//    System.out.println(aesDecodeForBase64AsString(sb.toString(), "rewq3421@34512fds"));
  String r1 = aesEncodeAsBase64(sb.toString(), "rewq3421@34512fds");
  System.out.println(r1);
  //1.没有trim()	mYAtdwlV9XjismYtoacvd4kFNA6O+U9sa
  //2.有了trim()	mYAtdwlV9XjismYtoacvd4kFNA6O+U9sa
//  System.out.println(aesDecodeForBase64AsString(r1, "rtyuikl@#$%^&*(976fghjklfds2389"));
    
//    System.out.println(aesDecodeForHexAsString(s2, "rtyuikl@#$%^&*(976fghjklfds2389"));
//    System.out.println(aesDecodeForBase64AsString(s2, "rtyuikl@#$%^&*(976fghjklfds2389"));
//    System.out.println(desDecodeForHexAsString(s2, "rtyuikl@#$%^&*(976fghjklfds2389"));
//    System.out.println(desDecodeForBase64AsString(s2, "rtyuikl@#$%^&*(976fghjklfds2389"));
    
//    des加解密
//    r1 = desEncodeAsHex(s, "rtyuikl@#$%^&*(976fghjklfds2389");
//    System.out.println(r1);
//    System.out.println(desDecodeForHexAsString(r1, "rtyuikl@#$%^&*(976fghjklfds2389"));
//    
//    r1 = desEncodeAsBase64(s, "rtyuikl@#$%^&*(976fghjklfds2389");
//    System.out.println(r1);
//    System.out.println(desDecodeForBase64AsString(r1, "rtyuikl@#$%^&*(976fghjklfds2389"));
    
    
  }
}