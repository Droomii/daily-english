package poly.controller;

import javax.annotation.Resource;
import static poly.util.CmmUtil.nvl;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import poly.dto.UserDTO;
import poly.service.IUserService;

@Controller
public class MainController {
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "UserService")
	IUserService userService;
	
	@RequestMapping(value = "index")
	public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".index start");
		
		String user_seq = (String) session.getAttribute("user_seq");
		if(user_seq == null) {
			return "/login";
		}
		
		String user_lvl = (String) session.getAttribute("user_lvl");
		log.info("user_lvl : " + user_lvl);
		if(user_lvl == null) {
			String url = "/wordTest/takeTest.do";
			String msg = "처음 가입 후 실력 측정 테스트가 필요합니다.";
			model.addAttribute("url", url);
			model.addAttribute("msg", msg);
			return "/redirect";
		}
		
		log.info(this.getClass().getName() + ".index end");
		return "/index";
	}
	
	@RequestMapping(value = "login")
	public String login(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".login start");
		String user_seq = (String) session.getAttribute("user_seq");
		if(user_seq != null) {
			return "/index";
		}
		log.info(this.getClass().getName() + ".login end");
		return "/login";
	}
	
	@RequestMapping(value = "findPw")
	public String findPw(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".findPw start");

		log.info(this.getClass().getName() + ".findPw end");
		return "/findPw";
	}
	
	@RequestMapping(value = "register")
	public String register(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".register start");

		
		log.info(this.getClass().getName() + ".register end");
		return "/register";
	}
	
	@RequestMapping(value = "checkEmailDuplicate", method = RequestMethod.POST)
	@ResponseBody
	public String checkEmailDuplicate(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".checkEmailDuplicate start");
		String email = request.getParameter("email");
		String res = userService.checkEmailDuplicate(email);
		
		log.info(this.getClass().getName() + ".checkEmailDuplicate end");
		return res;
	}
	@RequestMapping(value = "doLogin", method = RequestMethod.POST)
	@ResponseBody
	public String doLogin(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".doLogin start");

		String pw = nvl(request.getParameter("pw"));
		String email = nvl(request.getParameter("email"));
		log.info("pw : " + pw);
		log.info("email : " + email);
		UserDTO rDTO = userService.checkLogin(email, pw);
		if(rDTO==null) {
			log.info("user not found");
			log.info(this.getClass().getName() + ".doLogin end");
			return "1";
		}
		log.info("user_seq : " + rDTO.getUSER_SEQ());
		log.info("rDTO.getUSER_NICK : " + rDTO.getUSER_NICK());
		session.setAttribute("user_seq", rDTO.getUSER_SEQ());
		session.setAttribute("user_nick", rDTO.getUSER_NICK());
		session.setAttribute("user_lvl", rDTO.getUSER_LVL());
		
		log.info(this.getClass().getName() + ".doLogin end");
		return "0";
	}
	
	@RequestMapping(value = "findPwOK")
	public String findPwOK(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".findPwOK start");
		
		model.addAttribute("url", "/login.do");
		model.addAttribute("msg", "가입하신 이메일로 암호 초기화 메일을 발송하였습니다.");
		
		log.info(this.getClass().getName() + ".findPwOK end");
		return "/redirect";
	}
	
	@RequestMapping(value = "logout")
	public String logout(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".logout start");

		session.invalidate();
		String url = "/login.do";
		String msg = "로그아웃 하였습니다.";
		model.addAttribute("url", url);
		model.addAttribute("msg", msg);
		log.info(this.getClass().getName() + ".logout end");
		return "/redirect";
	}
	
	@RequestMapping(value = "doRegister", method = RequestMethod.POST)
	public String doRegister(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".doRegister start");
		String user_nick = request.getParameter("nick");
		String user_email = request.getParameter("email");
		String pw = request.getParameter("pw");
		UserDTO pDTO = new UserDTO();
		pDTO.setUSER_NICK(user_nick);
		pDTO.setUSER_EMAIL(user_email);
		pDTO.setUSER_PW(pw);
		int res = userService.insertUser(pDTO);
		String url = "/login.do";
		String msg = "회원가입에 실패했습니다.";
		if(res!=0) {
			msg = "회원가입에 성공했습니다.";
		}
		
		model.addAttribute("url", url);
		model.addAttribute("msg", msg);
		
		log.info(this.getClass().getName() + ".doRegister end");
		return "/redirect";
	}
	 
}
