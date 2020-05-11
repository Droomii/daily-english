package poly.persistance.mongo;

import java.util.List;

import poly.dto.MelonDTO;

public interface IMelonMapper {

	public boolean createCollection(String colNm) throws Exception;
	
	public int insertRank(List<MelonDTO> pDTO, String colNm) throws Exception;
	
	public List<MelonDTO> getRank(String colNm) throws Exception;
	
}
