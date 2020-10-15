package poly.controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import poly.dto.WordQuizDTO;
import poly.service.IAudioService;
import poly.service.INewsWordService;
import poly.util.SessionUtil;

@Controller
public class AudioController {

	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "NewsWordService")
	INewsWordService newsWordService;
	
	@Resource(name = "AudioService")
	IAudioService audioService;
	
	@RequestMapping(value = "today/todaySentence")
	public String todaySentence(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".todaySentence start");
		ModelMap sessionModel = SessionUtil.verify(session, model);
		if(sessionModel != null) {
			model = sessionModel;
			return "/redirect";
		}
		String user_seq = (String) session.getAttribute("user_seq");
		
		
		String user_lvl = (String) session.getAttribute("user_lvl");
		log.info("user_lvl : " + user_lvl);
		if(user_lvl == null) {
			String url = "/wordTest/takeTest.do";
			String msg = "처음 가입 후 실력 측정 테스트가 필요합니다.";
			model.addAttribute("url", url);
			model.addAttribute("msg", msg);
			return "/redirect";
		}
		WordQuizDTO qDTO = newsWordService.getRandomTodayQuiz(user_seq);
		if(qDTO.getIdx()!=-1) {
			String msg = "오늘의 퀴즈를 완료하지 않았습니다";
			String url = "/today/todayQuiz.do";
			model.addAttribute("msg", msg);
			model.addAttribute("url", url);
			return "/redirect";
		}
		
		
		
		log.info(this.getClass().getName() + ".todaySentence end");
		return "/today/todaySentence";
	}
	
	@RequestMapping(value = "today/getTodaySentences", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getTodaySentences(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".getTodaySentences start");
		

		List<Map<String, Object>> sentenceList = newsWordService.getTodaySentences();
		
		Map<String, Object> rMap = new HashMap<String, Object>();
		rMap.put("res", sentenceList);
		
		log.info(this.getClass().getName() + ".getTodaySentences end");
		return rMap;
	}
	
	@RequestMapping(value = "today/analyzeAudio", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> analyzeAudio(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".analyzeAudio start");
		String data = request.getParameter("data");
		String sentenceAudioIdx = request.getParameter("sentenceAudioIdx");
		log.info("sentenceAudioIdx : " + sentenceAudioIdx);
		Map<String, Object> rMap = audioService.analyzeAudio(data, sentenceAudioIdx);
		log.info(this.getClass().getName() + ".analyzeAudio end");
		
		return rMap;
	}
	
	@RequestMapping(value = "audio/getTodaySentenceAudio", method = RequestMethod.GET, produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public byte[] getTodaySentenceAudio(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".getTodaySentenceAudio start");
		String idx = request.getParameter("idx");
		byte[] res = audioService.getTodaySentenceAudio(idx);
		log.info(this.getClass().getName() + ".getTodaySentenceAudio end");
		return res;
	}
	
	@RequestMapping(value = "audio/getAnswerAudio", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public byte[] getAnswerAudio(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".getAnswerAudio start");
		String answer_temp_file = request.getParameter("file");
		log.info(this.getClass().getName() + ".getAnswerAudio end");
		return audioService.getAnswerAudio(answer_temp_file);
		//return audioService.getAnswerAudioFromOuter(answer_temp_file);
		
	}
}
