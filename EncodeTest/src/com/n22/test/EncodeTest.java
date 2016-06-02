package com.n22.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.n22.finance.customer.Customer;
import com.n22.finance.customer.InfoItem;
import com.n22.finance.policy.Order;

import framework.util.JsonUtil;
import lerrain.project.insurance.plan.Time;

public class EncodeTest {
	
	public static void main(String[] args) throws IOException {
		
		System.out.println(remove0("0.0234"));
		
//		int gender = 1;
//		String date = "1980-10-23";
//		String name = "Jack";
//		Date birthday = new Date();
//		InfoItem idType = new InfoItem("1", "扫翔的");
//		InfoItem idType2 = new InfoItem("2", "扫翔的");
//		String idNo = "4783915";
//		
//		Customer c1 = new Customer(date , gender);
//		c1.setName(name);
////		c1.setBirthday(birthday);
//		c1.setIdType(idType);
//		c1.setIdNo(idNo );
//		
//		Customer c2 = new Customer(date , gender);
//		c2.setName(name);
////		c2.setBirthday(birthday);
//		c2.setIdType(idType);
//		c2.setIdNo(idNo);
//		
//		System.out.println(c1.isTheSame(c2));
//		System.out.println(Time.getDate(date).toLocaleString());
		
//		ArrayList<String> strs = new ArrayList<>();
//		strs.add("AA");
//		strs.add("BB");
//		strs.add("CC");
//		strs.add("DD");
//		System.out.println(strs.indexOf("CC"));
		
		/*File mApkTmpFile = new File("E:\\Users\\HanXiaoqiang-N22\\Downloads\\Huatai_sfss.apk");
		String md5 = EncodeTest.getFileMD5String(mApkTmpFile);
		System.out.println(md5);*/
		
//		HashMap<String, String> order = new HashMap<String, String>();
//		order.put("policyCode", "001100");//保单号
//		order.put("bankCode", "0011022");//银行编号
//		order.put("bankAccount", "622848");//银行账户
//		order.put("payMode", "实时支付");//支付方式
		//以上必传，以下选传
//		order.put("esignature", policy.getPay().getBankAccountCode());//电子签名
//		order.put("deviceNo", (String)Application.getSession().getValue("deviceId"));//客户端序列号
//		order.put("agentCode", "0021");//代理人工号
//		String str = JsonUtil.objectToJson(order);
//		System.out.println(str);
		
//		String paramString = "";
//		try {
//			paramString = FileUtil.readFile("E:\\Users\\HanXiaoqiang-N22\\Downloads\\json.txt");
////			paramString = "bqywlpFRUjN94WNk4yXbp8Y6WSfS+HKDHb/FcrjdGUl94WNk4yXbp4gMOiTTHGp9qZiH+GiUVR2zW+0HYXk1z+z1ssyA6RoyfeFjZOMl26cHL6e5t7Vd2R9zXgZx/Hlj99SLgvy3hHN94WNk4yXbp33hY2TjJdunZtsrq+b1toIPx6LMT4/cdb5W/X52h977feFjZOMl26ewwa1OsYq3q/EQkC9pvcyaOIX0nXVdlLhPjiw/Ad/Npy6uRql5WJJYfeFjZOMl26cHY9HUFW6MLn3hY2TjJdunOY/ZR05GyJE=";
//			Order response = JsonUtil.jsonToObject(paramString, Order.class);
//			System.out.println(response.order.get("policyCode"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	protected static String remove0(String str){
		if(str != null && str.length() >=2){
			if(str.charAt(0) == '0' && str.charAt(1) != '.'){
				return remove0(str.substring(1));
			}
		}
		return str;
	}
	/**
	 * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符,apache校验下载的文件的正确性用的就是默认的这个组合
	 */
	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	protected static MessageDigest messagedigest = null;
	static {
		try {
			messagedigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException nsaex) {
			System.err.println(EncodeTest.class.getName()
					+ "初始化失败，MessageDigest不支持MD5Util。");
			nsaex.printStackTrace();
		}
	}
	
	/**
	 * 生成字符串的md5校验值
	 * 
	 * @param s
	 * @return
	 */
	public static String getMD5String(String s) {
		return getMD5String(s.getBytes());
	}
	
	/**
	 * 判断字符串的md5校验码是否与一个已知的md5码相匹配
	 * 
	 * @param str 要校验的字符串
	 * @param md5Str 已知的md5校验码
	 * @return
	 */
	public static boolean checkMD5(String str, String md5Str) {
		String s = getMD5String(str);
		return s.equals(md5Str);
	}
	// 生产文件md5值
	public static String getMd5ForFile(String path) throws IOException {
		File f = new File(path);
		if (f.exists()) {
			return EncodeTest.getFileMD5String(f);
		} else {
			return "";
		}
	}
	/**
	 * 生成文件的md5校验值
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String getFileMD5String(File file) throws IOException {		
		InputStream fis;
	    fis = new FileInputStream(file);
	    byte[] buffer = new byte[1024];
	    int numRead = 0;
	    while ((numRead = fis.read(buffer)) > 0) {
	    	messagedigest.update(buffer, 0, numRead);
	    }
	    fis.close();
		return bufferToHex(messagedigest.digest());
	}


	public static String getMD5String(byte[] bytes) {
		messagedigest.update(bytes);
		return bufferToHex(messagedigest.digest());
	}

	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[(bt & 0xf0) >> 4];// 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同 
		char c1 = hexDigits[bt & 0xf];// 取字节中低 4 位的数字转换 
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}
	
}