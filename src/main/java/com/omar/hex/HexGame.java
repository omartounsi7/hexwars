package com.omar.hex;

import com.omar.model.*;
import com.omar.gui.MainPanel;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.event.*;

import static com.omar.hex.HexConst.*;

public class HexGame {
	private final World world;
	private MainPanel mainPanel;
	public HexGame() {
		world = new World();
		createAndShowGUI();
	}
	private void createAndShowGUI() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("HexWars");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(430, 700);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.setVisible(true);
		DrawingPanel panel = new DrawingPanel();
		frame.add(panel, BorderLayout.CENTER);
		mainPanel = new MainPanel();
		mainPanel.updateLabel();
		frame.add(mainPanel, BorderLayout.NORTH);
	}
	public class DrawingPanel extends JPanel {
		public DrawingPanel() {
			setBackground(BGCOLOR);
			LineBorder lineBorder = new LineBorder(Color.ORANGE, 2);
			this.setBorder(lineBorder);
			MyMouseListener ml = new MyMouseListener();
			addMouseListener(ml);
		}
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
			super.paintComponent(g2);
			mainPanel.updateLabel();
			for (int i = 0; i< MAPSIZE; i++) {
				for (int j = 0; j< MAPSIZE; j++) {
					HexMech.drawHex(i,j,g2);
				}
			}
			for (int i = 0; i< MAPSIZE; i++) {
				for (int j = 0; j< MAPSIZE; j++) {
					Tile currTile = world.getTile(i, j);
					Army occArmy = currTile.getOccupyingArmy();
					String city = currTile.getCity();
					String str1 = "";
					String str2 = "";
					if(city != null){
						str1 += city;
					}
					if(occArmy != null){
						str2 += String.valueOf(occArmy.getFirepower());
					}
					HexMech.fillHex(i, j, currTile.getTileStatus(), g2, str1, str2, currTile == world.selectedTile, currTile.isAdjacent());
					repaint();
				}
			}
		}
		public class MyMouseListener extends MouseAdapter {	//inner class inside DrawingPanel
			public void mouseClicked(MouseEvent e) {
				Point p = new Point( HexMech.pxtoHex(e.getX(),e.getY()) );
				if (p.x < 0 || p.y < 0 || p.x >= MAPSIZE || p.y >= MAPSIZE){
					return;
				}
				if (World.gameStatus == GameStatus.P1WINS || World.gameStatus == GameStatus.P2WINS){
					return;
				}
				Tile clickedTile = world.getTile(p.x, p.y);
				System.out.println("You have clicked " + clickedTile);
				if(world.selectedTile == null){ // we have yet to select an army
					System.out.println("Selection phase.");
					world.selectArmy(clickedTile);
				} else { // an army has already been selected and is about to be moved
					System.out.println("Movement phase.");
					world.executeMove(p.x, p.y);
				}
			}
		}
	}
}


