package cnp;

public class Bid {
	
	private String 	agent;
	private int 	bid;
	private Object	data;
	
	public Bid(String agent, int bid) {
		this(agent, bid, null);
	}
	
	public Bid(String agent, int bid, Object data) {
		this.agent 	= agent;
		this.bid	= bid;
		this.data	= data;
	}
	
	public String 	getAgent() 	{ return this.agent; }
	public int 		getBid()	{ return this.bid; 	}
	public Object	getData()	{ return this.data; }
}
