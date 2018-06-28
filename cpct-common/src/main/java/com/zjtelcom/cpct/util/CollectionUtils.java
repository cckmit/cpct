package com.zjtelcom.cpct.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class CollectionUtils {

	
	public static boolean notEmpty(Collection con){
		return !(con == null || con.isEmpty());
	}
	
	public static boolean notEmpty(Map<?, ?> map){
		return !(map == null || map.isEmpty());
	}
	
	public static boolean isEmpty(Collection con){
		return (con == null || con.isEmpty());
	}
	
	public static boolean isEmpty(Map<?, ?> map){
		return (map == null || map.isEmpty());
	}
	
	public static String list2String(Object list){
		StringBuffer result = new StringBuffer("");
		if(list!=null){
			try {
				List ls = (List)list;
				if(notEmpty(ls)){
					for(int i=0;i<ls.size();i++){
						result.append(ls.get(i).toString()).append(",");
					}
				}
				if(!result.toString().trim().equals("")){
					result = result.deleteCharAt(result.length() -1);
				}
			} catch (Exception e) {
				System.out.println("utils.CollectionUtils：数据类型不一致");
			}
		}
		return result.toString();
	}
	
	
}
