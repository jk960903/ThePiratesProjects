create table BussinessTimes(ID int auto_increment PRIMARY KEY , STOREID int , DAY varchar(20) , OPEN varchar(20) , CLOSE varchar(20));
create table Store(ID int auto_increment PRIMARY KEY, NAME varchar(50) , OWNER varchar(30)
				, DESCRIPTION varchar(200) , level int , ADDRESS varchar(200) , phone varchar(20));
create table Holiday(ID int auto_increment PRIMARY KEY, STOREID int, HOLIDAYS varchar(30));

