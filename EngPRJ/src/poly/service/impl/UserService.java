package poly.service.impl;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import poly.dto.UserDTO;
import poly.persistance.mapper.IUserMapper;
import poly.service.IUserService;

@Service("UserService")
public class UserService implements IUserService{

	@Resource(name = "UserMapper")
	IUserMapper userMapper;
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Override
	public void updateUserLvl(String user_seq, String finalLevel) throws Exception {
		
		log.info(this.getClass().getName() + ".updateUserLvl start");
		userMapper.updateUserLvl(user_seq, finalLevel);
	}

	@Override
	public UserDTO checkLogin(String email, String pw) throws Exception {
		log.info(this.getClass().getName() + ".checkLogin start");
		return userMapper.checkLogin(email,pw);
	}
	
	

}
