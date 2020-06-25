package poly.service;

import poly.dto.UserDTO;

public interface IUserService {

	void updateUserLvl(String user_seq, String finalLevel) throws Exception;

	UserDTO checkLogin(String email, String pw) throws Exception;

}
