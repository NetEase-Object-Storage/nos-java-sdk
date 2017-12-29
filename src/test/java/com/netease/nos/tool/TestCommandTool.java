package com.netease.nos.tool;

import java.util.Arrays;

import org.testng.annotations.Test;

import com.netease.cloud.services.nos.tools.noscmd;

/**
 * @author WANG Binghuan 2013-1-29
 *
 */
public class TestCommandTool {
	
	@Test
	public void testParseParam() {
		String[] args = new String[] {"-create", "-key", "abc", "-id", "1234", "-seq", "xxxxxxx"};
		System.out.println(Arrays.toString(noscmd.parseCommonParams(args, new noscmd())));
	}

}
