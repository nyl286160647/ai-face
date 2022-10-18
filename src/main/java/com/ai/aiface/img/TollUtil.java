package com.ai.aiface.img;

public class TollUtil {
	/**
	 *
	 * base64开头部分
	 *
	 * @param suffix
	 *
	 * @return
	 *
	 */

	public static String imgsBase(String suffix) {
		StringBuilder sBuilder = new StringBuilder();

		sBuilder.append("data:image/");

		sBuilder.append(suffix);

		sBuilder.append(";base64,");

		return sBuilder.toString();

	}

}
