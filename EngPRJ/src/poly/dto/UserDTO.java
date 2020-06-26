package poly.dto;

import org.apache.ibatis.type.Alias;

@Alias("UserDTO")
public class UserDTO {

	private String USER_SEQ;
	private String USER_PW;
	private String USER_NICK;
	private String USER_EMAIL;
	private String USER_AGE;
	private String REG_DT;
	private String REG_SEQ;
	private String UPD_DT;
	private String UPD_SEQ;
	private String USER_LVL;
	public String getUSER_SEQ() {
		return USER_SEQ;
	}
	public void setUSER_SEQ(String uSER_SEQ) {
		USER_SEQ = uSER_SEQ;
	}
	public String getUSER_PW() {
		return USER_PW;
	}
	public void setUSER_PW(String uSER_PW) {
		USER_PW = uSER_PW;
	}
	public String getUSER_NICK() {
		return USER_NICK;
	}
	public void setUSER_NICK(String uSER_NICK) {
		USER_NICK = uSER_NICK;
	}
	public String getUSER_EMAIL() {
		return USER_EMAIL;
	}
	public void setUSER_EMAIL(String uSER_EMAIL) {
		USER_EMAIL = uSER_EMAIL;
	}
	public String getUSER_AGE() {
		return USER_AGE;
	}
	public void setUSER_AGE(String uSER_AGE) {
		USER_AGE = uSER_AGE;
	}
	public String getREG_DT() {
		return REG_DT;
	}
	public void setREG_DT(String rEG_DT) {
		REG_DT = rEG_DT;
	}
	public String getREG_SEQ() {
		return REG_SEQ;
	}
	public void setREG_SEQ(String rEG_SEQ) {
		REG_SEQ = rEG_SEQ;
	}
	public String getUPD_DT() {
		return UPD_DT;
	}
	public void setUPD_DT(String uPD_DT) {
		UPD_DT = uPD_DT;
	}
	public String getUPD_SEQ() {
		return UPD_SEQ;
	}
	public void setUPD_SEQ(String uPD_SEQ) {
		UPD_SEQ = uPD_SEQ;
	}
	public String getUSER_LVL() {
		return USER_LVL;
	}
	public void setUSER_LVL(String uSER_LVL) {
		USER_LVL = uSER_LVL;
	}
	
	
}
