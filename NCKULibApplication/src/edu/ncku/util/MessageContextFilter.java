package edu.ncku.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Pattern;

import android.util.Log;

public class MessageContextFilter {

	/**
	 * 删除Html標籤
	 * 
	 * @param inputString
	 * @return
	 */
	public static String removeHtmlTag(String inputString) {
		if (inputString == null)
			return null;
		String htmlStr = inputString; // 含html標籤的字串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;
		java.util.regex.Pattern p_special;
		java.util.regex.Matcher m_special;

		try {

			// 定義script的正則表達式{或<script[^>]*?>[\\s\\S]*?<\\/script>

			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";

			// 定義style的正則表達式{或<style[^>]*?>[\\s\\S]*?<\\/style>

			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";

			// 定義HTML標籤的正則表達式

			String regEx_html = "<[^>]+>";

			// 定義一些特殊字符的正則表達式 如：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

			String regEx_special = "\\&[a-zA-Z]{1,10};";

			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 過濾script標籤
			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 過濾style標籤
			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 過濾html標籤
			p_special = Pattern
					.compile(regEx_special, Pattern.CASE_INSENSITIVE);
			m_special = p_special.matcher(htmlStr);
			htmlStr = m_special.replaceAll(""); // 過濾特殊標籤
			textStr = htmlStr;
		}  catch (Exception e) {
			PrintWriter pw = new PrintWriter(new StringWriter());
			e.printStackTrace(pw);
			Log.e("MessageContextFilter", pw.toString());
		}
		return textStr;// 返回字串
	}

}
