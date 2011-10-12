import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import UsageStatisticClient.UsageStatistic;
import UsageStatisticClient.UsageStatisticTest;

public class Main {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		JFrame f = new JFrame();
		f.setBounds(0,0,600,600);
		f.setVisible(true);
		UsageStatistic usage = UsageStatistic.getInstance("moja aplikacja", null);
	
		for (int i=0;i<100;i++)
		{
			usage.used("aaa", "bbb");
			Thread.sleep(30);
		}
		
		
		
		
		
		
	}

}
