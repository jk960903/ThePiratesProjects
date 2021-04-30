package com.example.demo.Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;

import javax.sql.DataSource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import Model.Today;

// 컨트롤러
@RestController
public class StoreController {
	
	//데이터 베이스 연결 
	@Autowired
	DataSource dataSource;

	//데이터 베이스 연결이 되는지 확인하는 용도
	@RequestMapping("")
	public String Hello() {
		Connection connection = null;
		String answer ="";
		String answer2 = "";
		PreparedStatement pstmt;
		try {
			connection = dataSource.getConnection();
			String check = "show TABLES";
			pstmt = connection.prepareStatement(check);
			ResultSet rs = pstmt.executeQuery();
			answer = rs.toString();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return answer;
	}
	
	//테스트 용도로 마음껏 바꾸셔도 됩니다. 
	@RequestMapping("test")
	public String Find() {
		Connection connection = null;
		String answer ="";
		PreparedStatement pstmt;
		try {
			connection = dataSource.getConnection();
			String select = "Select * from BussinessTimes";
			pstmt = connection.prepareStatement(select);
			ResultSet rs = pstmt.executeQuery();
			answer = rs.toString();
			while(rs.next()) {
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return answer;
	}
	
	/*제휴 가게 추가 
		먼저 해당 들어가게 될 ID (STORE PRIMARYKEY )를 찾은 후에 해당 ID 값을 바탕으로 BUSSINESSTIMES DB 에 bussiness의 데이터들을 넣음
		그 후 Store DB에 저장 
	*/
	@RequestMapping(value = "/AddStore" , method = RequestMethod.POST , produces = "application/json; charset=utf8")
	public String AddStore(@RequestBody HashMap<String,Object> params) {
		String answer = "";
		 Connection connection = null;
		 PreparedStatement pstmt;
		 Object objname = params.get("name");
		 Object objowner = params.get("owner");
		 Object objdescription = params.get("description");
		 Object objlevel = params.get("level");
		 Object objaddress = params.get("address");
		 Object objphone = params.get("phone");
		 String objbussinessTimes = params.get("businessTimes").toString();
		 objbussinessTimes = objbussinessTimes.replaceAll(" ", "");
		 objbussinessTimes = objbussinessTimes.substring(1,objbussinessTimes.length()-1);
		 String[] bussinessarray = objbussinessTimes.split("}");
		 String Query ="";
		 String findIDQuery = "select id from STORE order by id desc limit 1";
		 int id=-1;
		 try {
			 connection = dataSource.getConnection();
			 pstmt = connection.prepareStatement(findIDQuery);
			 ResultSet rs = pstmt.executeQuery();
			 if(rs.next()) {
				 id = rs.getInt("ID")+1;
			 }
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
		 for(int i = 0; i < bussinessarray.length; i++) {
			 if(bussinessarray[i].charAt(0) == ',') bussinessarray[i] = bussinessarray[i].substring(2); 
			 bussinessarray[i] = bussinessarray[i].replace("{","");
			 String[] data = bussinessarray[i].split(",");
			 if(id == -1) id = 1;
			 Query += "INSERT INTO BUSSINESSTIMES(STOREID,DAY,OPEN,CLOSE) VALUES(" + Integer.toString(id) +",'";
			 for(int j = 0 ; j < data . length; j++) {
				 String[] temp = data[j].split("=");
				 Query += temp[1] + "','";
			 }
			 Query = Query.substring(0,Query.length() -2);
			 Query=Query.concat(");\n");
		 }

		 try {
			 connection = dataSource.getConnection();
			 pstmt = connection .prepareStatement(Query);
			 int result = pstmt.executeUpdate();
			 String sql = "INSERT INTO STORE(NAME,OWNER,DESCRIPTION,LEVEL,ADDRESS,PHONE) VALUES('"
					 + objname.toString() +"','" + objowner.toString() + "','" + objdescription.toString()
					 + "','" + Integer.parseInt(objlevel.toString()) + "','" + objaddress.toString() +"','"
					 + objphone.toString() +"')";
			 pstmt = connection.prepareStatement(sql);
			 result = pstmt.executeUpdate();
			 answer = Integer.toString(result);
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
		return answer;
	}
	
	/*
	 * 공휴일 및 휴일 지정 API 
	 * ID 와 holidays 를 받아와서 Holiday Table 에 저장 
	 */
	@RequestMapping(value = "/holidays" , method = RequestMethod.POST , produces = "application/json; charset=utf8")
	public String Holidays(@RequestBody HashMap<String, Object> params){
		 Object objId = new Object();
		 Object objholidays = new Object();
		 objId = params.get("id");
		 objholidays = params.get("holidays");
		 String temp = objholidays.toString();
		 temp=temp.substring(1,temp.length()-1);
		 temp=temp.replaceAll(" ", "");
		 String[] holidays = temp.split(",");
		 Connection connection = null;
		 String answer ="";
		 PreparedStatement pstmt;
		 try {
			connection = dataSource.getConnection();
			String query = "";
			for(int i = 0; i < holidays.length ; i ++) {
				query+="INSERT INTO HOLIDAY(STOREID,HOLIDAYS) VALUES(" + objId.toString()
						+",'"+holidays[i]+"');";
			}
			pstmt = connection.prepareStatement(query);
			int result = pstmt.executeUpdate();
			answer = Integer.toString(result);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return answer;
	}
	
	/*
	 * 테이블에 있는 내용 level ( 등급) 에 따라 정렬하여 점포명 요약 등급 현재영업상태 등을 보여줌
	 */
	@RequestMapping("/ShowStore")
	public String SHOWSTORELIST() {
		
		Connection connection = null;// DB Connection
		PreparedStatement pstmt;
		JSONArray jsonArray =new JSONArray();
		Today today = new Today();
		try {
			String sql = "select id , name , description , level from STORE order by level";
			connection = dataSource.getConnection();
			pstmt = connection.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				JSONObject jobject = new JSONObject();
				int id = rs.getInt("ID");
				String name = rs.getString("NAME");
				String description = rs.getString("DESCRIPTION");
				int level = rs.getInt("LEVEL");
				String sql2 = "select DAY, OPEN , CLOSE from BUSSINESSTIMES where STOREID=" + id +";";
				pstmt = connection.prepareStatement(sql2);
				ResultSet rs2 = pstmt.executeQuery();
				jobject.put("name", name);
				jobject.put("description",description);
				jobject.put("level",level);
				String sql3 = "select HOLIDAYS from HOLIDAY where STOREID=" + id + ";";
				pstmt = connection.prepareStatement(sql3);
				ResultSet rs3 = pstmt.executeQuery();
				while(rs2.next()) {
					String day = rs2.getString("DAY");
					String open = rs2.getString("OPEN");
					String close = rs2.getString("CLOSE");
					day = day.toLowerCase();
					if(day.equals(today.dayweek)) {
						if(today.changeTime(open) <= today.minute
							&& today.changeTime(close) >= today.minute) {
							jobject.put("bussinessStatus" ,"OPEN");
							System.out.println("check");
							break;
						}else if(today.changeTime(open) > today.minute
								|| today.changeTime(close) < today.minute) {
							jobject.put("bussinessStatus", "CLOSE");
							System.out.println("check");
							break;
						}
					}
				}
				while(rs3.next()) {
					String Holiday = rs3.getString("HOLIDAYS");
					System.out.println(Holiday);
					System.out.println(today.today);
					if(Holiday.equals(today.today)) jobject.put("bussinessStatus","HOLIDAY");
				}
				System.out.println(jobject.get("bussinessStatus"));
				jsonArray.add(jobject);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return jsonArray.toString();
	}
	
	/*
	 * 점포 상세 조회 id 를 통해 검색
	 * 오늘을 기준으로 3일의 데이터를 가져옴 
	 */
	@RequestMapping(value ="/DetailShow" , method = RequestMethod.POST , produces = "appication/json; charset=utf-8")
	public String DetaliShow(@RequestBody int id) {
		Connection connection =null;
		PreparedStatement pstmt;
		String query = "select * from STORE where id ="+Integer.toString(id);
		String name , description , address , phone ,bussinessday;
		Today today = new Today();
		int ID , level;
		JSONObject jsonobject = new JSONObject();
		try {
			connection = dataSource.getConnection();
			pstmt = connection.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();// 점포 테이블 조회 저장 SET
			while(rs.next()) {
				ID = rs.getInt("ID");
				name = rs.getString("NAME");
				description = rs.getString("DESCRIPTION");
				level = rs.getInt("LEVEL");
				address = rs.getString("ADDRESS");
				phone = rs.getString("PHONE");
				jsonobject.put("id",ID);
				
				jsonobject.put("name",name);
				jsonobject.put("description",description);
				jsonobject.put("level",level);
				jsonobject.put("address",address);
				jsonobject.put("phone",phone);
				
				String BussinessQuery = "select DAY , OPEN , CLOSE from BUSSINESSTIMES where STOREID=" + Integer.toString(id)+";";
				pstmt = connection.prepareStatement(BussinessQuery);
				ResultSet rs2 = pstmt.executeQuery(); // 점포의 운영 데이터 저장 SET
				
				int count = 0;
				String HolidayQuery = "select Holidays from Holiday where STOREID =" +Integer.toString(id);
				pstmt = connection .prepareStatement(HolidayQuery);
				ResultSet rs3 = pstmt.executeQuery(); // 휴무일 저장 SET
				boolean[] holi = new boolean[3];
				HashSet<String> set = new HashSet<>();
				while(rs3.next()) {
					String holiday = rs3.getString("Holidays");
					set.add(holiday);
				}
				if(set.contains(today.today)) {
					holi[0] = true;
					for(int i=1; i<=2 ; i++) {
						if(set.contains(today.getNextDate(today.today))){
							holi[i]=true;
						}
					}
				}
				today = new Today();
				JSONArray jsonArray = new JSONArray();
			
				while(rs2.next()) {
					String day = rs2.getString("DAY");
					String open = rs2.getString("OPEN");
					String close = rs2.getString("CLOSE");
					String tempday= day.toLowerCase();
					JSONObject tempJson = new JSONObject();
					if(today.dayweek.equals(tempday)) {
						tempJson.put("day",day);
						tempJson.put("open",open);
						tempJson.put("close",close);
						System.out.println(day);
						System.out.println(open);
						System.out.println(close);
						String status;
						if(today.changeTime(open) <= today.minute && today.changeTime(close) >= today.minute) { // 시간 계산 
							if(holi[0]) tempJson.put("status","HOLIDAY");
							else tempJson.put("status","OPEN");
							System.out.println(tempJson.get("status"));
							jsonArray.add(tempJson);
							count++;
							for(int i = 1; i < 3 ; i++) {
								tempJson = new JSONObject();
								if(rs2.next()) {
									day = rs2.getString("DAY");
									open = rs2.getString("OPEN");
									close = rs2.getString("CLOSE");
									tempJson.put("day" , day);
									tempJson.put("open" ,open);
									tempJson.put("close",close);
									if(today.changeTime(open) <= today.minute && today.changeTime(close) >= today.minute) {
										if(holi[count]) tempJson.put("status","HOLIDAY");
										else tempJson.put("status","OPEN");
									}else {
										if(holi[count]) tempJson.put("status","HOLIDAY");
										else tempJson.put("status","close");
									}
								}else {
									day = today.getDayWeek((today.todayweek+i)%7);
									open = "NOT OPEN TODAY";
									close = "NOT OPEN TODAY";
									tempJson.put("day","day");
									tempJson.put("open",open);
									tempJson.put("close",close);
									tempJson.put("status","CLOSE");
								}
								jsonArray.add(tempJson);
							}
							break;
						}else {
							if(holi[0]) tempJson.put("status","Holiday");
							else tempJson.put("status","CLOSE");
							count++;
							for(int i = 1; i< 3; i++) {
								if(rs2.next()) {
									tempJson = new JSONObject();
									day = rs2.getString("DAY");
									open = rs2.getString("OPEN");
									close = rs2.getString("CLOSE");
									tempJson.put("day" , day);
									tempJson.put("open" ,open);
									tempJson.put("close",close);
									if(today.changeTime(open) <= today.minute && today.changeTime(close) >= today.minute) {
										if(holi[count]) tempJson.put("status","HOLIDAY");
										else tempJson.put("status","OPEN");
									}else {
										if(holi[count]) tempJson.put("status","HOLIDAY");
										else tempJson.put("status","close");
									}
								}else {
									day = today.getDayWeek((today.todayweek+i)%7);
									open = "NOT OPEN TODAY";
									close = "NOT OPEN TODAY";
									tempJson.put("day","day");
									tempJson.put("open",open);
									tempJson.put("close",close);
									tempJson.put("status","CLOSE");
								}
								jsonArray.add(tempJson);
							}
							break;
						}
					}
				}
				jsonobject.put("bussinessdays",jsonArray);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return jsonobject.toString();
	}
	/*
	 * 제휴 만료로 인한 삭제
	 */
	@RequestMapping(value = "/DeleteStore" , method = RequestMethod.POST , produces = "application/json; charset=utf-8")
	public void DeleteStore(@RequestBody int id) {
		Connection connection =null;
		PreparedStatement pstmt;
		String query = "delete from STORE where ID ="+Integer.toString(id);
		String query1 = "delete from BUSSINESSTIMES where STOREID="+Integer.toString(id);
		String query2 = "delete from Holiday where STOREID="+Integer.toString(id);
		try {
			connection = dataSource.getConnection();
			pstmt = connection.prepareStatement(query);
			int result = pstmt.executeUpdate();
			pstmt = connection.prepareStatement(query1);
			result = pstmt.executeUpdate();
			pstmt = connection.prepareStatement(query2);
			result = pstmt.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ;
	}
	
}
