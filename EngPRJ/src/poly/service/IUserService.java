package poly.service;

public interface IUserService {

	void updateUserLvl(String user_seq, String finalLevel) throws Exception;

	String checkLogin(String email, String pw) throws Exception;

}
