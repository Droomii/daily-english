package poly.util;

import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;

public class SessionUtil {
	/**
	 * @param user_seq : 사용자 번호
	 * @param model : 메서드 안에서 사용중인 모델맵
	 * @return
	 * 세션이 만료되었을 경우 오류 메시지와 리다이렉트될 url을 담은 모델맵을 반환함. 정상일 경우 null
	 */
	public static ModelMap verify(String user_seq, ModelMap model) {
		
		if(user_seq==null) {
			model.addAttribute("msg", "세션이 만료되었습니다.");
			model.addAttribute("url", "/login.do");
			return model;
		}
		return null;
	}
	
	/**
	 * @param user_type : 사용자 종류(사용자, 판매자, 관리자 등을 의미하는 문자열)
	 * @param rightType : 알맞은 사용자 종류
	 * @param model : 메서드 안에서 사용중인 모델맵
	 * @return
	 * 알맞은 사용자 종류로 로그인한 상태가 아닌 경우 오류 메시지와 리다이렉트될 url을 담은 모델맵을 반환함. 정상일 경우 null
	 */
	public static ModelMap verify(String user_type, String rightType, ModelMap model) {
		if(!CmmUtil.nvl(user_type).matches(rightType)) {
			model.addAttribute("msg", "비정상적인 접근입니다");
			model.addAttribute("url", "/index.do");
			return model;
		}
		
		return null;
	}
	
	/**
	 * @param user_seq : 사용자 번호
	 * @param user_type : 사용자 종류(사용자, 판매자, 관리자 등을 의미하는 문자열)
	 *@param rightType : 알맞은 사용자 종류
	 * @param model : 메서드 안에서 사용중인 모델맵
	 * @return
	 * 세션이 만료되었거나 알맞은 사용자 종류가 아닌 경우 오류 메시지와 리다이렉트될 url을 담은 모델맵 반환. 정상일 경우 null
	 */
	public static ModelMap verify(String user_seq, String user_type, String rightType, ModelMap model) {
		if(SessionUtil.verify(user_seq, model)!=null) {
			model = SessionUtil.verify(user_seq, model);
			return model;
		}
		
		if(SessionUtil.verify(user_type, rightType, model)!=null) {
			model = SessionUtil.verify(user_type, rightType, model);
			return model;
		}
		
		return null;
	}
	
	/**
	 * @param session : 현재 세션
	 * @param rightType : 알맞은 사용자 타입
	 * @param model : 현재 모델맵
	 * @return
	 * 세션이 만료되었거나 알맞은 사용자 종류가 아닌 경우 오류 메시지와 리다이렉트될 url을 담은 모델맵 반환. 정상일 경우 null
	 */
	public static ModelMap verify(HttpSession session, String rightType, ModelMap model) {
		String user_seq = (String)session.getAttribute("user_seq");
		String user_type = (String)session.getAttribute("user_type");
		
		if(SessionUtil.verify(user_seq, model)!=null) {
			model = SessionUtil.verify(user_seq, model);
			return model;
		}
		
		if(SessionUtil.verify(user_type, rightType, model)!=null) {
			model = SessionUtil.verify(user_type, rightType, model);
			return model;
		}
		
		return null;
	}
	/**
	 * @param session : 현재 세션
	 * @param model : 현재 모델맵
	 * @return
	 * 세션이 만료되었을 경우 오류 메시지와 리다이렉트될 url을 담은 모델맵 반환. 정상일 경우 null
	 */
	public static ModelMap verify(HttpSession session, ModelMap model) {
		String user_seq = (String)session.getAttribute("user_seq");
		
		if(SessionUtil.verify(user_seq, model)!=null) {
			model = SessionUtil.verify(user_seq, model);
			return model;
		}

		return null;
	}
	
}