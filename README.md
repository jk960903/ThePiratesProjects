# ThePiratesProjects

1. 설치 및 환경설정 가이드
 1.1 Tomcat 설정 
  1.1.1 Tomcat version - Apache tomcat 9 . 0 .45
  1.1.2 Tomcat Download 경로 : https://tomcat.apache.org/download-90.cgi zip 파일을 다운
  1.1.3 받은 zip 파일을 압축해제후 bin 폴더에 startup.bat 파일 실행
   1.1.3.1 구동 실패시 setclasspath.bat 파일 실행 후 재시도
  
 1.2 War 파일 설정 방법
 현재 포함된 WAR 파일은 코드 버전과 맞춰둔 상태 .WAR 파일을 Apache-tomcat-9.0.45 webapps 폴더에 위치
 Apache Tomcat 9.0.45 실행시 자동으로 war파일 압축 해제 되며 구동가능
 
 1.3 이클립스 배포 방법
  프로젝트 실행 후 프로젝트 우클릭 EXPORT 탭의 War file 선택 후 Apache-tomcat-9.0.45 webapps 폴더에 Export


1.4 구동확인 방법 
 1.4.1 localhost:8080 (tomcat 포트 변경시 포트에 맞춰서 ) /(war 파일이름)/주소 : 로 사용가능
 
 1.4.2 127.0.0.1:8080 (tomcat 포트 변경시 포트에 맞춰서 ) /(war 파일이름)/주소 : 로 사용가능




2. 테이블 생성 SQL : 
기본적으로 사용테이블을 H2 mem 모드로 사용하기 때문에 schema.sql 파일을 통해 테이블 생성은 자동으로 이루어짐
테이블 사용 sql 
create table BussinessTimes(ID int auto_increment PRIMARY KEY , 
                            STOREID int , DAY varchar(20) ,  
                            OPEN varchar(20) , CLOSE varchar(20));
create table Store(ID int auto_increment , NAME varchar(50) , OWNER varchar(30)
				, DESCRIPTION varchar(200) , level int , ADDRESS varchar(200) , phone varchar(20));
create table Holiday(ID int auto_increment , STOREID int, HOLIDAYS varchar(30));


3개의 테이블을 생성




3. API 사용가이드 ( 개발 당시 war 파일명 예시 project)
 3.1 점포추가 API  점포 등록시 id(Primary Key) 값은 자동 증가처리 
 URL : localhost:8080/project/AddStore
 Method : POST
 Parameter - JSON (HashMap 으로 매핑)
 필수 Parameter (name , owner , description level address phone bussinessTimes[])
 
 요청 파라미터 EX ) open 과 close 의 경우 포맷을 정확히 지켜야함 HH:MM 으로
 {
  "name": "점포명",
  "owner": "개발자",
  "description": "요약",
  "level": 2 (int),
  "address": "서울시 강남구",
  "phone": "010-1111-2222",
  "businessTimes": [
  {
    "day": "Monday",
    "open": "13:00",
    "close": "23:00"
   },
   {
     "day": "Tuesday",
     "open": "13:00",
     "close": "23:00"
   },
   {
     "day": "Wednesday",
     "open": "09:00",
     "close": "18:00"
   },
   {
     "day": "Thursday",
     "open": "09:00",
     "close": "23:00"
   },
   {
     "day": "Friday",
     "open": "09:00",
     "close": "23:00"
   }
   ]
   
 
 결과 코드 
 1 : 점포 추가 성공
 실패시 에러코드 리턴 
 
 
 3.2 점포 휴무일 등록 API 점포의 id(Primary Key) 값을 파라미터로 전달받아 저장
 URL : localhost:8080/project/holidays
 Method : POST
 Parameter - JSON (HashMap 으로 매핑)
 필수 Parameter ( id , holidays[])
 
 요청 파라미터 EX )
 {
   "id": 1,
   "holidays": [
     "2021-04-30",
     "2021-05-01"
    ]
 {

 
 결과코드 
 1:  휴무일 등록 성공
 실패시 에러 코드 리턴 
 
 3.3 점포 목록 조회 API 점포명 점포 설명 영엉상태 정보를 level 오름차순 조회 
 영엉중 - > OPEN  종료 -> close -> 휴무 -> HOLIDAY
  URL : localhost:8080/project/ShowStore
  Method POST
  Parameter - 없음
  
  요청 파라미터 Ex )
  
  응답 파라미터 Ex)
 [
   {
     "name": "점포명",
     "description": "요약본",
     "level": 1,
     "businessStatus": "OPEN"
    }
]

  
  성공시 점포 목록 조회를 String(Json 형태) 로리턴
  실패시 에러코드 리턴
 
 3.4 점포 상세 정보 조회 API 점포의 상세 정보(점포명, 점포 설명, 주소 ,전화번호 ,조회 일자 기준 영업시간 3일)
 
 URL : localhost:8080/project/DetailShow
 Method : POST
 parameter ResponseBody int id 
 
 성공시 점포 상제 정보를 Json 형태로 (id name desciption level address phone bussinessday[3]) 형태로 리턴
 실패시 에러코드 리턴
 
 만약 가게를 open day가 정해져 있지 않는경우 NOT OPEN TODAY 로 open close 값 초기화 후 status Close 로 초기화
 요청 파라미터 Ex ) 1 
 
 응답 파라미터 Ex
 {
   "id": 1,
   "name": "점포",
   "description": "요약",
   "level": "2",
   "address": "주소",
   "phone": "010-1111-2222",
   "businessDays": [
   {
   "day": "Wednesday",
   "open": "09:00",
   "close": "18:00",
   "status": "CLOSE"
   },
   {
   "day": "Thursday",
  "open": "09:00",
   "close": "23:00",
   "status": "HOLIDAY"
   },
   {
   "day": "Friday",
   "open": "09:00",
   "close": "23:00",
   "status": "HOLIDAY"
   }
   ]
  }

 3.5 점포 삭제 api 요청 점포 id 명 삭제
 
 URL : localhost:8080/project/DeleteStore
 Method : POST
 parameter ResponseBody int id 
 
 성공시 1 
 실패시 에러코드 리턴
 
 
 
 4. 테스트 방법 : 
 PostMan 설치후 각 url에 날려보면됩니다. 
 
 필수사항 : 꼭 Method 방식을 맞춰 주셔야 하고 
 header 탭에 KEY 값 : Content-Type  Value : application/json 꼭해주셔야합니다
 
 body에 헤더로 요청 파라미터 json 형태로 넣으시면 됩니다. 
 
 
 

 
