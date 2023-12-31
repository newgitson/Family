package com.animal.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Random;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.animal.domain.MemberDTO;
import com.animal.service.MemberService;
import com.github.scribejava.core.model.OAuth2AccessToken;

@Controller
@RequestMapping("/member/*")
public class MemberController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private JavaMailSender mailSender;
	
	/* NaverLoginBO */
	private NaverLoginBO naverLoginBO;
	private String apiResult = null;
	
	@Autowired
	private void setNaverLoginBO(NaverLoginBO naverLoginBO) {
		this.naverLoginBO = naverLoginBO;
	}
	
	@PostMapping("/loginPro")
	public String loginPro(MemberDTO MemberDTO, HttpSession session,HttpServletResponse response) {
		boolean isValid = false;
		MemberDTO DTO2 = memberService.checklogin(MemberDTO);
		if (DTO2 != null && DTO2.getPass() != null) {
		    isValid = BCrypt.checkpw(MemberDTO.getPass(), DTO2.getPass());
		}
		if(isValid) {
			session.setAttribute("id", MemberDTO.getId());
			session.setAttribute("nickname", DTO2.getNickname());
			session.setAttribute("email", DTO2.getEmail());
			return "redirect:/board/home";
		}else {
			response.setContentType("text/html;charset=UTF-8");
			 PrintWriter out;
			try {
				out = response.getWriter();
		        out.println("<script>");
		        out.println("alert('계정정보가 틀렸습니다');");
		        out.println("history.back()");
		        out.println("</script>");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	@GetMapping("/join")
	public String join() {
		return "member/join";
	}
	
	@PostMapping("/joinPro")
	@ResponseBody
	public String joinPro(MemberDTO MemberDTO, HttpSession session) {
	    boolean isjoined = memberService.join(MemberDTO);
	    if (isjoined) {
	        session.setAttribute("id", MemberDTO.getId());
	        session.setAttribute("nickname", MemberDTO.getNickname());
	        return "Y";
	    } else {
	        return "N";
	    }
	}
	
	@GetMapping("/main")
	public String main() {
		return "member/main";
	}
	
	@GetMapping("/find")
	public String find() {
		return "member/find";
	}
	
	@GetMapping("/findid")
	public String findid() {
		return "member/findid";
	}
	
	@PostMapping("/showidPro")
	@ResponseBody
	public String showidPro(MemberDTO memberDTO) {
		memberDTO = memberService.checkemail(memberDTO);
	    return memberDTO.getId();
	}
	
	@PostMapping("/showidPro2")
	@ResponseBody
	public String showidPro2(MemberDTO memberDTO) {
	    memberDTO = memberService.checkmobile(memberDTO);
	    return memberDTO.getId();
	}
	
	@GetMapping("/showid")
	public String showid() {
		return "member/showid";
	}
	
	@GetMapping("/findpass")
	public String findpass() {
		return "member/findpass";
	}
	
	@PostMapping("/checkid")
	@ResponseBody
	public String checkid(MemberDTO memberDTO) {
		memberDTO = memberService.checkid(memberDTO);
	    return memberDTO.getId();
	}

	@GetMapping("/findpass2")
	public String findpass2() {
	    return "member/findpass2";
	}
	
	@PostMapping("/findpassPro")
	@ResponseBody
	public String findpassPro(MemberDTO MemberDTO, HttpSession session) {
	    boolean isjoined = memberService.findpassPro(MemberDTO);
	    if (isjoined) {
	        return "Y";
	    } else {
	        return "N";
	    }
	}
	
	//이메일 인증
	@ResponseBody
	@PostMapping("/emailAuth")
	public String emailAuth(String email, MemberDTO memberDTO) {
		MemberDTO memberDTO2 = memberService.checkemail(memberDTO);
		//DB 이메일 없으면 에러
		if (memberDTO2 == null) {
	        throw new IllegalArgumentException("memberDTO cannot be null");
	    }
		//DB 비밀번호 없으면 에러
		if (memberDTO2.getPass() == null) {
			throw new IllegalArgumentException("pass cannot be null");
		}
		Random random = new Random();
		int checkNum = random.nextInt(888888) + 111111;

		/* 이메일 보내기 */
        String setFrom = "sonminuk@naver.com";
        String toMail = email;
        String title = "회원가입 인증 이메일 입니다.";
        String content = 
                "홈페이지를 방문해주셔서 감사합니다." +
                "<br><br>" + 
                "인증 번호는 " + checkNum + "입니다." + 
                "<br>" + 
                "해당 인증번호를 인증번호 확인란에 기입하여 주세요.";
        
        try {
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content,true);
            mailSender.send(message);
            
        }catch(Exception e) {
            e.printStackTrace();
        }
        return memberDTO2.getEmail() + "," + Integer.toString(checkNum);
 
	}
	
	//이메일 인증
		@ResponseBody
		@PostMapping("/emailAuth2")
		public String emailAuth2(String email, MemberDTO memberDTO) {
			MemberDTO memberDTO2 = memberService.checkeidmail(memberDTO);
			//DB 아이디,이메일 없으면 에러
			if (memberDTO2 == null) {
		        throw new IllegalArgumentException("memberDTO cannot be null");
		    }
			Random random = new Random();
			int checkNum = random.nextInt(888888) + 111111;

			/* 이메일 보내기 */
	        String setFrom = "sonminuk@naver.com";
	        String toMail = email;
	        String title = "회원가입 인증 이메일 입니다.";
	        String content = 
	                "홈페이지를 방문해주셔서 감사합니다." +
	                "<br><br>" + 
	                "인증 번호는 " + checkNum + "입니다." + 
	                "<br>" + 
	                "해당 인증번호를 인증번호 확인란에 기입하여 주세요.";
	        
	        try {
	            
	            MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
	            helper.setFrom(setFrom);
	            helper.setTo(toMail);
	            helper.setSubject(title);
	            helper.setText(content,true);
	            mailSender.send(message);
	            
	        }catch(Exception e) {
	            e.printStackTrace();
	        }
	        return memberDTO2.getId() + "," + memberDTO2.getEmail() + "," + Integer.toString(checkNum);
	 
		}
	
	//로그인 첫 화면 요청 메소드
		@RequestMapping(value = "/login", method = { RequestMethod.GET, RequestMethod.POST })
		public String login(Model model, HttpSession session) {
			
			/* 네이버아이디로 인증 URL을 생성하기 위하여 naverLoginBO클래스의 getAuthorizationUrl메소드 호출 */
			String naverAuthUrl = naverLoginBO.getAuthorizationUrl(session);
			
			//https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=sE***************&
			//redirect_uri=http%3A%2F%2F211.63.89.90%3A8090%2Flogin_project%2Fcallback&state=e68c269c-5ba9-4c31-85da-54c16c658125
			System.out.println("네이버:" + naverAuthUrl);
			
			//네이버 
			model.addAttribute("url", naverAuthUrl);
	 
			return "member/login";
		}
	 
		//네이버 로그인 성공시 callback호출 메소드
		@RequestMapping(value = "/home", method = { RequestMethod.GET, RequestMethod.POST })
		public String callback(Model model, @RequestParam(required = false) String code, @RequestParam(required = false) String state, HttpSession session) throws IOException, ParseException {
			if (code != null && state != null) {
			OAuth2AccessToken oauthToken;
	        oauthToken = naverLoginBO.getAccessToken(session, code, state);
	 
	        //1. 로그인 사용자 정보를 읽어온다.
			apiResult = naverLoginBO.getUserProfile(oauthToken);  //String형식의 json데이터
			
			/** apiResult json 구조
			{"resultcode":"00",
			 "message":"success",
			 "response":{"id":"33666449","nickname":"shinn****","age":"20-29","gender":"M","email":"sh@naver.com","name":"\uc2e0\ubc94\ud638"}}
			**/
			
			//2. String형식인 apiResult를 json형태로 바꿈
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(apiResult);
			JSONObject jsonObj = (JSONObject) obj;
			
			//3. 데이터 파싱 
			//Top레벨 단계 _response 파싱
			JSONObject response_obj = (JSONObject)jsonObj.get("response");
			//response의 nickname값 파싱
			String nickname = (String)response_obj.get("nickname");
			
			//DB에 사용자가 이미 존재하는지 확인
			if (!memberService.isUserExist(nickname)) {
				memberService.join2(response_obj);  // 전체 데이터를 join2 메소드에 전달
		    }
			
			//4.파싱 닉네임 세션으로 저장
			String email = (String)response_obj.get("email");
			session.setAttribute("id", email);
			session.setAttribute("nickname", nickname);
			model.addAttribute("result", apiResult);
			}
		     
			return "board/home";
		}
		
		//로그아웃
		@RequestMapping(value = "/logout", method = { RequestMethod.GET, RequestMethod.POST })
		public String logout(HttpSession session)throws IOException {
				session.invalidate();
				return "redirect:/member/login";
			}
		
		@ResponseBody
		@GetMapping("/mobile")
		public String sendSMS(MemberDTO memberDTO, @RequestParam("mobile") String userPhoneNumber) { // 휴대폰 문자보내기
			MemberDTO memberDTO2 = memberService.checkmobile(memberDTO);
			//DB 휴대전화 없으면 에러, 네이버 연동은 하이폰 있어서 안보내짐
			if (memberDTO2 == null) {
		        throw new IllegalArgumentException("memberDTO cannot be null");
		    }
			
			int randomNumber = (int)((Math.random()* (9999 - 1000 + 1)) + 1000);//난수 생성

			memberService.certifiedPhoneNumber(userPhoneNumber,randomNumber);
			
			return memberDTO2.getMobile() + "," + Integer.toString(randomNumber);
		}
		
		@ResponseBody
		@PostMapping("/mobile2")
		public String sendSMS2(MemberDTO memberDTO, @RequestParam("mobile") String userPhoneNumber) { // 휴대폰 문자보내기
			System.out.println(memberDTO);
			MemberDTO memberDTO2 = memberService.checkeidmobile(memberDTO);
			//DB 아이디,휴대폰 없으면 에러
			if (memberDTO2 == null) {
		        throw new IllegalArgumentException("memberDTO cannot be null");
		    }
			
			int randomNumber = (int)((Math.random()* (9999 - 1000 + 1)) + 1000);//난수 생성

			memberService.certifiedPhoneNumber(userPhoneNumber,randomNumber);
			
			return memberDTO2.getId() + "," + memberDTO2.getMobile() + "," + Integer.toString(randomNumber);
		}
		
		//카카오 로그인
		@RequestMapping(value="/kakaoLogin", method=RequestMethod.GET)
		public String kakaoLogin(@RequestParam(value = "code", required = false) String code, HttpSession session) throws Exception {
			String access_Token = memberService.getAccessToken(code);
			HashMap<String, Object> userInfo = memberService.getUserInfo(access_Token);
			session.setAttribute("id", userInfo.get("email"));
			session.setAttribute("nickname", userInfo.get("nickname"));
			return "board/home";
	    	}

}
