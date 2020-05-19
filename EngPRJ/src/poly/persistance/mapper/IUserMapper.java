package poly.persistance.mapper;

import config.Mapper;
import poly.dto.TestWordDTO;

@Mapper("UserMapper")
public interface IUserMapper {

	void updateLevel(TestWordDTO rDTO) throws Exception;

	
}
