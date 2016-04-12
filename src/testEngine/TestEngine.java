package testEngine;

import com.plancrawler.gui.GUI;

public class TestEngine {

	public static void main(String[] args) {

//		JFrame frame = new JFrame("testcode");
//		frame.setSize(1400, 900);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//		ChalkBoardPanel chalkBoard = new ChalkBoardPanel(400, 600);
//		frame.add(chalkBoard, BorderLayout.WEST);
//
//		frame.setVisible(true);

		GUI gui = new GUI();
		gui.init();

//		System.out.println("calibrated to "+ CalibrationDialog.calibrate(null, 300d));
		
//		Measure measure = new Measure(3);
//		MyPoint p1 = new MyPoint(0,0);
//		MyPoint p2 = new MyPoint(100,100);
//		measure.calibrate(p1, p2, 1);
//		System.out.println("after calibration, the measurement equals: "+ measure.measure(p1, p2, 1));

		boolean running = true;
		while (running) {
			gui.updateComponents();
		}
	}

}
