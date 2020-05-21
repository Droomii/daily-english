package poly.service;

import poly.dto.NLPDTO;

public interface INLPService {

	NLPDTO process(String inputText) throws Exception;

}
