package Model;

public class BussinessTimes {
	private String day;
	private String open;
	private String close;
	public BussinessTimes() {
		
	}
	public BussinessTimes(String day , String open , String close) {
		this.day = day;
		this.open = open;
		this.close = close;
	}
	public String getDay() {
		return this.day;
	}
	public String getOpen() {
		return this.open;
	}
	public String getClose() {
		return this.close;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public void setOpen(String open) {
		this.open = open;
	}
	public void setClose(String close) {
		this.close = close;
	}
	public int checkSame(String open , String close) {
		if(open.equals(close)) return 0;
		else return 0;
	}
}
