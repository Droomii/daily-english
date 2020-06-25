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
		
		log.info(this.getClass().getName() + ".index end");
		return "/index";
	}
	
	@RequestMapping(value = "login")
	public String login(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".login start");

		log.info(this.getClass().getName() + ".login end");
		return "/login";
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
		session.setAttribute("user_seq", rDTO.getUSER_SEQ());
		session.setAttribute("user_nick", rDTO.getUSER_NICK());
		session.setAttribute("user_lvl", rDTO.getUSER_LVL());
		
		log.info(this.getClass().getName() + ".doLogin end");
		return "0";
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
	 
}
