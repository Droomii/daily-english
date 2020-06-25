package poly.persistance.mapper;

import config.Mapper;
import poly.dto.UserDTO;

@Mapper("UserMapper")
public interface IUserMapper {

	void updateUserLvl(String user_seq, String finalLevel) throws Exception;

	UserDTO checkLogin(String email, String pw) throws Exception;

	
}
