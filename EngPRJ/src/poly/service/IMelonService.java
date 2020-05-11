package poly.service;

import java.util.List;

import poly.dto.MelonDTO;

public interface IMelonService {

	
	public int collectMelonRank() throws Exception;
	
	public List<MelonDTO> getRank() throws Exception;
}
