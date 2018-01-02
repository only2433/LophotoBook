package com.starbrunch.couple.photo.frame.main.common;

public class Feature
{
	/**
	 * 네비게이션바 ( 소프트키) 가 있는 지 의 유무
	 */
	public static boolean HAVE_NAVIGATION_BAR 		= false;
	
	/**
	 * 태블릿인지의 유무 
	 */
	public static boolean IS_TABLET					= false;
	
	/**
	 * 최저 해상도 이하인지의 여부
	 */
	public static boolean IS_MINIMUM_DISPLAY_SIZE 	= false;
	
	/**
	 * 특정 태블릿 비율이 16:9 가 아닌 정상적이지 않은 4:3 비율의 태블릿을 지원하기 위해 사용. 
	 */
	public static boolean IS_MINIMUM_SUPPORT_TABLET_RADIO_DISPLAY = false;
	
	/**
	 * 언어 설정이 영어로 되어 있는 지 사용하기 위해
	 */
	public static boolean IS_LANGUAGE_ENG = false;
	
	/**
	 * 유료사용자 인지 무료 사용자인지 구분하기 위해 사용
	 */
	public static boolean IS_FREE_USER			= true;
	
}
