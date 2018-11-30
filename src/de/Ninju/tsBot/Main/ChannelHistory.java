package de.Ninju.tsBot.Main;

public class ChannelHistory {
	
	int channels = 0;
	
	public ChannelHistory() {
		
	}
	
	public void addChannel() {
		System.out.println(channels);
		channels++;
	}
	
	public void remChannel() {
		if(channels > 0) {
		channels--;
		}
	}
	
	public int isChannelhopping() {
		if(channels == 8) {
			return 2;
		}if(channels >= 10) {
			return 1;
		}else {
			return 0;
		}
	}

}
