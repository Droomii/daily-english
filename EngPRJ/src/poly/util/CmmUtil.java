package poly.util;

public class CmmUtil {
	public static String nvl(String str, String chg_str) {
		String res;

		if (str == null) {
			res = chg_str;
		} else if (str.equals("")) {
			res = chg_str;
		} else {
			res = str;
		}
		return revertXSS(res);
	}

	public static String nvl(String str) {
		return nvl(str, "");
	}
	
	public static String nvl(String str, boolean noBreaks) {
		str = str==null ? "" : str;
		str = str.replaceAll("& lt;br& gt;", "&lt;br&gt;");
		return nvl(str, "");
	}
	public static String nvl(String str, String chg_str, boolean noBreaks) {
		str = str==null ? "" : str;
		str = str.replaceAll("& lt;br& gt;", "&lt;br&gt;");
		return nvl(str, chg_str);
	}

	public static String checked(String str, String com_str) {
		if (str.equals(com_str)) {
			return " checked";
		} else {
			return "";
		}
	}

	public static String checked(String[] str, String com_str) {
		for (int i = 0; i < str.length; i++) {
			if (str[i].equals(com_str))
				return " checked";
		}
		return "";
	}

	public static String select(String str, String com_str) {
		if (str.equals(com_str)) {
			return " selected";
		} else {
			return "";
		}
	}

	public static String revertXSS(String value) {

		value = value.replaceAll("& lt;br& gt;", "<br>")
				.replaceAll("& lt;", "&lt;")
				.replaceAll("& gt;", "&gt;")
				.replaceAll("& #40;", "&#40;")
				.replaceAll("& #39;", "&#39;")
				.replaceAll("& #41;", "&#41;")
				.replaceAll("\"", "&#34;")
				.replaceAll("scr!pt", "script");
		return value;
	}
	
}