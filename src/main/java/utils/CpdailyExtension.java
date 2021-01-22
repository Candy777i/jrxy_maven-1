package utils;

import net.sf.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.UUID;

/**
 *
 * CpdailyExtension 用来生成加密值的工具类
 *
 * @author kit chen
 * @github https://github.com/meethigher
 * @blog https://meethigher.top
 * @time 2021年1月6日
 */
public class CpdailyExtension {
	// 算法的模式：DES、3DES、AES、RC
	private static final String modeAlgorithm = "DES";
	// 算法的标准转化名称
	private static final String name = "DES/CBC/PKCS5Padding";
	// 编码格式
	private static final String charset = "UTF-8";
	// 明文
	private static final String text = "abcde";
	// 原始密钥，2021-1-8日ST83=@XV弃用，此处感谢大佬https://github.com/ZimoLoveShuang/auto-sign/issues/38
	private static final String key = "b3L26XNL";
	// 初始化向量，简称IV
	private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };

	/**
	 * Base64编码
	 * @param bytes
	 * @return String
	 */
	public static String Base64Encrypt(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	/**
	 * Base64解码
	 *
	 * @param bytes
	 * @return byte[]
	 */
	public static byte[] Base64Decrypt(byte[] bytes) {
		return Base64.getDecoder().decode(bytes);
	}

	/**
	 * DES加密
	 *
	 * @param text
	 * @param key
	 * @param charset
	 * @return String 经过Base64编码后的内容
	 * @throws Exception
	 */
	public static String DESEncrypt(String text, String key, String charset) throws Exception {
		// 通过给定的字节数组构建一个密钥
		SecretKeySpec sks = new SecretKeySpec(key.getBytes(charset), modeAlgorithm);
		// 使用IV构造对象
		IvParameterSpec ivPS = new IvParameterSpec(iv);
		// 1.获取加解密的算法工具类
		Cipher cipher = Cipher.getInstance(name);
		// 2.对工具类进行初始化
		cipher.init(Cipher.ENCRYPT_MODE, sks, ivPS);
		// 3.用加密工具类对象对明文进行加密
		byte[] doFinal = cipher.doFinal(text.getBytes(charset));
		// 防止出现乱码，所以采用Base64编码
		return Base64Encrypt(doFinal);

	}

	/**
	 * DES解密
	 *
	 * @param text
	 * @param key
	 * @param charset
	 * @return String
	 * @throws Exception
	 */
	public static String DESDecrypt(byte[] text, String key, String charset) throws Exception {
		// 先进行Base64解码
		text = Base64Decrypt(text);
		// 通过给定的字节数组构建一个密钥
		SecretKeySpec sks = new SecretKeySpec(key.getBytes(charset), modeAlgorithm);
		// 使用IV构造对象
		IvParameterSpec ivPS = new IvParameterSpec(iv);
		// 1.获取加解密的算法工具类
		Cipher cipher = Cipher.getInstance(name);
		// 2.对工具类进行初始化
		cipher.init(Cipher.DECRYPT_MODE, sks, ivPS);
		// 3.用加密工具类对象对明文进行解密
		return new String(cipher.doFinal(text));
	}

	/**
	 * 生成CpdailyExtension
	 *
	 * @param
	 * @return String
	 */
	public static String generateCpdailyExtension(String longitude,String latitude) {
		GetConfig config= GetConfig.getInstance();
		JSONObject object = new JSONObject();
		object.put("systemName", config.getString("systemName"));
		object.put("systemVersion", config.getString("systemVersion"));
		object.put("model", config.getString("model"));
		object.put("deviceId", UUID.randomUUID().toString());
		object.put("appVersion", config.getString("appVersion"));
		object.put("lon", longitude);
		object.put("lat", latitude);
		object.put("userId", config.getString("userId"));
		try {
			return DESEncrypt(object.toString(), config.getString("key"), charset);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("生成CpdailyExtension错误");
			return null;
		}
	}
}
