package poly.persistance.mapper;

import config.Mapper;

@Mapper("UserMapper")
public interface IUserMapper {

	void updateUserLvl(String user_seq, String finalLevel) throws Exception;

	String checkLogin(String email, String pw) throws Exception;

	
}
