package com.example.filetrans;

public interface IResponse {
	
	/**
	 * process http request response.
	 * @param respStr response content.
	 */
	void onResponse(Object respContent);

}
