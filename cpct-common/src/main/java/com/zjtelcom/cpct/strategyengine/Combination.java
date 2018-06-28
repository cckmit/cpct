package com.zjtelcom.cpct.strategyengine;

import java.util.ArrayList;
import java.util.List;

public class Combination {
	
	public static String CONNECT = "有";
	
	public static List<String> combiantion(List<String> chs, List<String> keys) {
		List<String> result = new ArrayList<String>();
		if (chs == null || chs.size() == 0) {
			return result;
		}
		List<String> list = new ArrayList();
		for (int i = 1; i <= chs.size(); i++) {
			combine(chs, 0, i, list, result, keys);
		}
		return result;
	}

	// 从字符数组中第begin个字符开始挑选number个字符加入list中
	public static void combine(List<String> cs, int begin, int number, List<String> list, List<String> result, List<String> keys) {
		if (number == 0) {
			boolean flagAdd = true;
			if(keys != null && keys.size() > 0){
				for (int i = 0; i < keys.size(); i++) {
					if(!list.contains(keys.get(i))){
						flagAdd = false;
						break;
					}
				}
			}
			if(flagAdd){
				String value = list2String(list);
				result.add(value);
			}
//			result.add(value);
			return;
		}
		if (begin == cs.size()) {
			return;
		}
		list.add(cs.get(begin));
		combine(cs, begin + 1, number - 1, list, result, keys);
		list.remove(cs.get(begin));
		combine(cs, begin + 1, number, list, result, keys);
	}

	public static void main(String args[]) {
		
//		String chs[] = { "CDMA", "宽", "固", "ITV" };
		//String chs[] = { "CDMA", "宽", "固", "ITV", "东", "南", "西", "北" };
		List<String> strings = new ArrayList<String>();
		strings.add("CDMA");
		strings.add("宽");
		strings.add("固");
		strings.add("ITV");
		List<String> keys = new ArrayList<String>();
//		keys.add("CDMA");
		keys.add("宽");
		keys.add("固");
		keys.add("ITV");
		List<String> result = combiantion(strings, keys);
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
		}
		System.out.println(result.size());
	}

	public static String list2String(List<String> list) {
		StringBuffer buffer = new StringBuffer();
		if (list == null || list.size() == 0) {
			return "";
		}
		for (int i = 0; i < list.size(); i++) {
			buffer.append(CONNECT).append(list.get(i));
		}
		return buffer.substring(1);
	}
}