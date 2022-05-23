package Bohnanza;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GUI implements ActionListener {

	private final int LENGTH = 1680;
	private final int WIDTH = 1050;

	private JFrame frame = new JFrame();
	private JPanel panel = new JPanel();
	private JButton helpButton = new JButton();
	private JLabel backgroundLabel = new JLabel();
	//The sound files used in this class
	private File backgroundMusic = new File("sounds/BackgroundMusic.wav");
	private File plantingSound = new File ("sounds/Plant Growing Sound Effect.wav");

	//Game timer
	private Timer gameTimer = new Timer(1000, this); // timer for the gametime
	private static int timeElapsed = 20;
	private JLabel timerLabel = new JLabel();

	//Display labels
	private JLabel[] coinDisplay = new JLabel[4];
	private JLabel pileDisplayCards = new JLabel();
	private JLabel currentPlayerLabel = new JLabel();

	//Buttons for each player's hand
	private ArrayList<ArrayList<JButton>> handButtons = new ArrayList<ArrayList<JButton>>();

	//Field displays
	private JButton stopPlantingButton = new JButton("Stop Planting");
	private JLabel[][] fieldDisplay = new JLabel[5][3];
	private ArrayList<ArrayList<JLabel>> fieldButtons = new ArrayList<ArrayList<JLabel>>();
	private JButton[] fieldDecideButtons = new JButton[2];

	//Draw pile
	private JButton drawPileButton = new JButton();

	//Trade displays
	private JLabel[] tradingAreaLabel = new JLabel[2];
	private JLabel[] tradeRequestLabel = new JLabel[4];
	private JButton[] tradeRequestButtonAccept = new JButton[4];
	private Cartas[][] playerTradeCards = new Cartas[4][2];

	private static int humanPlantTimes = 0;		// To keep track of how many times the human player planted (maximum 2)
	private static int humanTurnStage = 1;	//To keep track of what step of the turn the human is on
	private static int cardToTrade;		//To indicate which field number is to be traded
	private static int currentPlayer;	//To keep track of whether or not it's the human's turn

	private int handCardNum;	//Holds index of card in hand
	private Cartas currentCard;	//Holds the current card

	//Creating objects
	private Baraja baraja = new Baraja();
	private Jugador[] jugadores = new Jugador[4];
	private AI1 ai1 = new AI1(1,baraja);
	private AI2 ai2 = new AI2(2,baraja);
	private AI3 ai3 = new AI3(3,baraja);

	public GUI() throws InterruptedException {
		//Running gui setup methods
		tradeAreaSetup();
		setUpFieldButtons();
		setUpDrawButtons();
		playerSetUp();
		mainSetUp();
	
		//Plays the background music
		playBackgroundMusic(backgroundMusic);

		//Starting the game, running all turns until refillCount is 3
		do {
			//Runs human turn
			currentPlayerLabel.setText("Human Player's Turn");
			JOptionPane.showMessageDialog(frame, "Human Player's Turn!");
			humanTurn();
			if (baraja.getRondas() == 3)
				break;
			currentPlayerLabel.setText("Jason AI's Turn");
			JOptionPane.showMessageDialog(frame, "Jason AI's Turn!");
			//Runs AI Turns
			aiTurn(jugadores[1]); // Jason's AI
			if (baraja.getRondas() == 3)
				break;
			currentPlayerLabel.setText("Parth AI's Turn");
			JOptionPane.showMessageDialog(frame, "Parth AI's Turn!");
			aiTurn(jugadores[2]); // Parth's AI
			if (baraja.getRondas() == 3)
				break;
			currentPlayerLabel.setText("Tony AI's Turn");
			JOptionPane.showMessageDialog(frame, "Tony AI's Turn!");
			aiTurn(jugadores[3]); // Tony's AI
		} while (baraja.getRondas() < 3);
		
		for(int p = 0; p < 4; p++)
			for(int f = 0; f < 2; f++)
				if(!jugadores[p].getCampos()[f].isEmpty())
					harvest(jugadores[p], jugadores[p].getCampos()[f]);
		
		//Displays final standings at the end of game
		JOptionPane.showMessageDialog(frame, "Final Treasury Standings\nHuman Player: " + jugadores[0].getBanco()
				+ "\nJason's AI: " + jugadores[1].getBanco()
				+ "\nParth's AI: " + jugadores[2].getBanco()
				+ "\nTony's AI: " + jugadores[3].getBanco());
	}

	private void humanTurn() throws InterruptedException {

		// start game timer
		gameTimer.start();
		timerLabel.setVisible(true);
		// set current player to human player
		humanTurnStage = 1;
		// add action listener to the first card
		handButtons.get(0).get(0).addActionListener(this);
		// human player has 20 seconds to make a move
		TimeUnit.SECONDS.sleep(20);
		handButtons.get(0).get(0).removeActionListener(this);
		TimeUnit.SECONDS.sleep(1);
		// drawing 3 cards for human player
		for (int x = 0; x < 3; x++) {
			addCardToHand(jugadores[0], baraja.reparte());
			updatePiles();
			TimeUnit.SECONDS.sleep(1);
		}
		// trade request from AI
		//repeating trading process for every card in trading area
		for (int i = 0; i < jugadores[0].getTradingArea().length; i ++) {
			if (!jugadores[0].getTradingArea()[i].isEmpty()) {
				// create a border around the first trading area
				tradingAreaLabel[i].setBorder(BorderFactory.createLineBorder(Color.BLUE, 10));

				for (int n = 1; n < 4; n++) {
					// if the AI would like to send a trade request
					if (jugadores[n].sendTradeRequest(jugadores[0].getTradingArea()[i].getCartas(), jugadores[0]) == true) {
						cardToTrade = i;
						tradeRequestLabel[n].setIcon(new ImageIcon("images/" + jugadores[n].pickCardToTrade(jugadores[0]).getNombre() + ".png"));
						playerTradeCards[n][i] = jugadores[n].pickCardToTrade(jugadores[0]);
						tradeRequestButtonAccept[n].setEnabled(true);
					} else {
						tradeRequestLabel[n].setText("No Offer");
					}
				}
				// human player have 20 seconds to accept trade if there is a trade
				if (tradeRequestLabel[1].getIcon() != null || tradeRequestLabel[2].getIcon() != null || tradeRequestLabel[3].getIcon() != null) {
					gameTimer.start();
					timerLabel.setVisible(true);
					TimeUnit.SECONDS.sleep(20);

					if (!jugadores[0].getTradingArea()[i].isEmpty()) {
						//Human player given one last chance to plant the first TA
						int reply1 = JOptionPane.showConfirmDialog(null,
								"No Trades Accepted. Plant in (y: field 1, n: field 2)", "TradeResults",
								JOptionPane.YES_NO_OPTION);
						if (reply1 == JOptionPane.YES_OPTION) {
							//Trader plants card in field 1
							plant(jugadores[0], jugadores[0].getCampos()[0], jugadores[0].getTradingArea()[i].getCartas());
							jugadores[0].getTradingArea()[i].removeCard();

						} else if (reply1 == JOptionPane.NO_OPTION) {
							//Trader plants card in field 1
							plant(jugadores[0], jugadores[0].getCampos()[1], jugadores[0].getTradingArea()[i].getCartas());
							jugadores[0].getTradingArea()[i].removeCard();
						}
					}
					
					//Clears trading offers from ais
					for (int n = 1; n < 4; n++) {
						tradeRequestLabel[n].setIcon(null);
						tradeRequestButtonAccept[n].setEnabled(false);
						tradeRequestLabel[n].setText("No Offer");
					}
					tradingAreaLabel[i].setIcon(null);
				} else {
					//Human player given one last chance to plant the second TA card
					int reply2 = JOptionPane.showConfirmDialog(null,
							"No Trades From AI. Plant card in y: field 1, n: field 2", "TradeResults",
							JOptionPane.YES_NO_OPTION);
					if (reply2 == JOptionPane.YES_OPTION) {
						//Trader plants card in field 2
						plant(jugadores[0], jugadores[0].getCampos()[0], jugadores[0].getTradingArea()[i].getCartas());
						jugadores[0].getTradingArea()[i].removeCard();
						tradingAreaLabel[i].setIcon(null);
					} else if (reply2 == JOptionPane.NO_OPTION) {
						// trader plants card in field 2
						plant(jugadores[0], jugadores[0].getCampos()[1], jugadores[0].getTradingArea()[i].getCartas());
						jugadores[0].getTradingArea()[i].removeCard();
						tradingAreaLabel[i].setIcon(null);
					}
				}
				
				tradingAreaLabel[i].setBorder(null);
				
				//Clear playerTradeCards
				for (int j = 1; j < playerTradeCards.length; j++){
					Arrays.fill(playerTradeCards[j], null);
					tradeRequestLabel[j].setIcon(null);
				}
					
			}
		}
	}

	public void aiTurn(Jugador currentAI) throws InterruptedException {
		// AI's turn
		currentPlayer = currentAI.getNumJ();
		TimeUnit.SECONDS.sleep(1);

		//Plant the first card
		plant(currentAI, currentAI.pickFieldToPlant(currentAI.getMano().getCarta(0)),currentAI.getMano().getCarta(0));
		updateFieldNum();
		removeCardFromHand(0, currentAI);
		TimeUnit.SECONDS.sleep(1);

		//Checking if AI should plant the second card
		if (currentAI.plantDecide(currentAI.getMano().getCarta(0))) {
			plant(currentAI, currentAI.pickFieldToPlant(currentAI.getMano().getCarta(0)), currentAI.getMano().getCarta(0));
			updateFieldNum();
			removeCardFromHand(0, currentAI);
			TimeUnit.SECONDS.sleep(1);
		}
		//Adding cards from deck to TA
		for (int g = 0; g < 2; g++) {
			addCardToTradingArea(currentAI.getTradingArea()[g], baraja.reparte());
			updatePiles();
			TimeUnit.SECONDS.sleep(1);
		}
		//Decide whether to plant the cards or trade the cards
		//Repeated twice for each trading area card
		for (int t = 0; t < currentAI.getTradingArea().length; t++) {
			if (currentAI.plantDecideTA(currentAI.getTradingArea()[t].getCartas())) {
				//If AI has decided to plant the TA card
				JOptionPane.showMessageDialog(frame, "AI decided not to trade.");
				plant(currentAI, currentAI.pickFieldToPlant(currentAI.getTradingArea()[t].getCartas()),currentAI.getTradingArea()[t].getCartas());
				removeCardFromTradingArea(currentAI.getTradingArea()[t]);
				tradingAreaLabel[t].setIcon(null);
				updateFieldNum();
			} else {
				//If AI has decided to trade the TA card
				tradingAreaLabel[t].setBorder(BorderFactory.createLineBorder(Color.BLUE, 10));

				if (jugadores[0].getCampos()[0].getTipo() == currentAI.getTradingArea()[t].getCartas() || jugadores[0].getCampos()[1].getTipo() == currentAI.getTradingArea()[t].getCartas()){
					//Human player can offer trade
					cardToTrade = t;
					humanTurnStage = 3;

					//Adding clickable buttons for the human player
					for (int i = 0; i < jugadores[0].getMano().getCartaMano().size(); i ++) {
						handButtons.get(0).get(i).addActionListener(this);
					}
					JOptionPane.showMessageDialog(frame, "You may pick a card to trade with the highlighted blue card.");
				
					//Human given 20 seconds to select a trading card
					gameTimer.start();
					timerLabel.setVisible(true);
					TimeUnit.SECONDS.sleep(20);

					//Removing clickable hand buttons for human player
					for (int i = 0; i < jugadores[0].getMano().getCartaMano().size(); i ++) {
						handButtons.get(0).get(i).removeActionListener(this);
					}
					
				} else {
					JOptionPane.showMessageDialog(frame, "You do not have the cards in your field so your trade is skipped.");
				}

				//AI send trade to another AI
				for (int p = 1; p < 4; p++){		//Looping through all 4 AIs
					
					//AI sending trade request given the TA card
					if(jugadores[p].sendTradeRequest(currentAI.getTradingArea()[t].getCartas(), currentAI)){
						playerTradeCards[p][0] = jugadores[p].pickCardToTrade(jugadores[currentPlayer]);
						playerTradeCards[p][1] = currentAI.getTradingArea()[t].getCartas();
					}
				}

				int tradingPartnerNum = currentAI.acceptTradeRequest(playerTradeCards);

				//If human player has accepted the trade
				if (tradingPartnerNum == 0){
					trade(jugadores[currentPlayer], jugadores[currentPlayer].getTradingArea()[cardToTrade].getCartas(), jugadores[0], jugadores[0].getMano().getCarta(handCardNum));
					jugadores[currentPlayer].getTradingArea()[cardToTrade].removeCard();
					tradingAreaLabel[cardToTrade].setIcon(null);
					removeCardFromHand(handCardNum, jugadores[0]);
					
				//If AI has accepted the trade
				} else if(tradingPartnerNum > 0){
					JOptionPane.showMessageDialog(frame, "player " + currentAI.getNumJ() + " traded " + playerTradeCards[tradingPartnerNum][1].getNombre() + " with " + "player " + jugadores[tradingPartnerNum].getNumJ() + "'s " + playerTradeCards[tradingPartnerNum][0].getNombre());
					trade(currentAI, playerTradeCards[tradingPartnerNum][1], jugadores[tradingPartnerNum], playerTradeCards[tradingPartnerNum][0]);
					removeCardFromTradingArea(currentAI.getTradingArea()[t]);
					tradingAreaLabel[t].setIcon(null);
					removeCardFromHand(jugadores[tradingPartnerNum].getMano().getCartaMano().indexOf(playerTradeCards[tradingPartnerNum][0]), jugadores[tradingPartnerNum]);

				} else {
					JOptionPane.showMessageDialog(frame, "No trades were accepted");
				}

				//Clearing the cards from the trading array
				for(int p = 0; p < 4; p++)
					for(int c = 0; c < 2; c++)
						playerTradeCards[p][c] = null;

				tradingAreaLabel[t].setBorder(null);

				//If the AI has denied the trade
				if(!currentAI.getTradingArea()[t].isEmpty()){
					//Plant the TA card in the AI's field
					plant(currentAI, currentAI.pickFieldToPlant(currentAI.getTradingArea()[t].getCartas()),currentAI.getTradingArea()[t].getCartas());
					removeCardFromTradingArea(currentAI.getTradingArea()[t]);
					tradingAreaLabel[t].setIcon(null);
				}
			}
		}
		//Adding cards to AI's hand
		for (int x = 0; x < 3; x++) {
			TimeUnit.SECONDS.sleep(1);
			addCardToHand(currentAI, baraja.reparte());
			updatePiles();
		}
	}

	public void mainSetUp() {

		//Setting up label for the timer
		timerLabel.setLayout(null);
		timerLabel.setText("Timer: " + timeElapsed);
		timerLabel.setForeground(Color.WHITE);
		timerLabel.setBounds(70, 50, 300, 50);
		timerLabel.setFont(new Font("San Serif", Font.PLAIN, 40));
		panel.add(timerLabel);
		timerLabel.setVisible(true);
		
		panel.setBounds(0, 0, LENGTH, WIDTH);
		panel.setLayout(null);
		frame.add(panel);
		panel.setBackground(Color.WHITE);
		panel.setVisible(true);
		
		//Setting up help button
		helpButton.setBounds(1580, 10, 65, 65);
		helpButton.setText("HELP");
		helpButton.setVisible(true);
		helpButton.addActionListener(this);
		helpButton.setBackground(Color.YELLOW);
		helpButton.setForeground(Color.ORANGE);
		helpButton.setBorder(null);
		helpButton.setFocusPainted(false);
		panel.add(helpButton);
		
		//Setting up stop planting button (if the user only wants to plant one card and not 2)
		stopPlantingButton.setBounds(1100, 700, 200, 100);
		stopPlantingButton.setVisible(true);
		stopPlantingButton.addActionListener(this);
		stopPlantingButton.setEnabled(false);
		panel.add(stopPlantingButton);

		//Sets up field labels to display cards in each player's fields
		for (int i = 1; i < 5; i++) {
			for (int x = 1; x < 3; x++) {
				fieldDisplay[i][x] = new JLabel();
				fieldDisplay[i][x].setText("0");
				fieldDisplay[i][x].setFont(new Font("Sans Serif", Font.PLAIN, 25));
				panel.add(fieldDisplay[i][x]);
			}
		}

		fieldDisplay[1][1].setBounds(835, 585, 100, 100);
		fieldDisplay[1][2].setBounds(935, 585, 100, 100);

		fieldDisplay[2][1].setBounds(520, 405, 100, 100);
		fieldDisplay[2][2].setBounds(620, 405, 100, 100);

		fieldDisplay[3][1].setBounds(835, 415, 100, 100);
		fieldDisplay[3][2].setBounds(935, 415, 100, 100);

		fieldDisplay[4][1].setBounds(1150, 405, 100, 100);
		fieldDisplay[4][2].setBounds(1250, 405, 100, 100);

		pileDisplayCards.setBounds(740, 590, 100, 100);
		pileDisplayCards.setText("" + baraja.getBaraja().size());
		pileDisplayCards.setFont(new Font("Sans Serif", Font.BOLD, 25));
		panel.add(pileDisplayCards);

		//Setting up treasury displays
		for (int x = 0; x < 4; x++) {
			coinDisplay[x] = new JLabel();
			panel.add(coinDisplay[x]);
			coinDisplay[x].setFont(new Font("Sans Serif", Font.BOLD, 25));
			coinDisplay[x].setText("Treasury: 0");
		}
		coinDisplay[0].setBounds(850, 800, 300, 50);
		coinDisplay[1].setBounds(330, 395, 300, 50);
		coinDisplay[2].setBounds(840, 250, 300, 50);
		coinDisplay[3].setBounds(1400, 395, 300, 50);
		
		//Setting up the label that displays the current player
		currentPlayerLabel.setVisible(true);
		currentPlayerLabel.setBounds(70, 95, 400, 100);
		currentPlayerLabel.setFont(new Font("Sans Serif", Font.BOLD, 35));
		currentPlayerLabel.setForeground(Color.WHITE);
		currentPlayerLabel.setText("Human Player's Turn");
		panel.add(currentPlayerLabel);

		//Setting up background for the game
		backgroundLabel.setIcon(new ImageIcon("images/background.png"));
		backgroundLabel.setBounds(0,0,1680,1080);
		backgroundLabel.setVisible(true);
		
		
		frame.setSize(LENGTH, WIDTH);
		frame.setTitle("Bohnanza");
		frame.setLayout(null);
		// frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		// frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		panel.add(backgroundLabel);
	}

	private void updateFieldNum() {
		for (int x = 1; x < 5; x++) {
			for (int y = 1; y < 3; y++) {
				fieldDisplay[x][y].setText("" + jugadores[x - 1].getCampos()[y - 1].getNumCartas());
			}
		}
	}

	private void updatePiles() {
		pileDisplayCards.setText("" + baraja.getBaraja().size());
	}

	private void playerSetUp() {
		
		//Adding buttons for each player's hand
		for (int i = 0; i < jugadores.length; i ++) {
			handButtons.add(new ArrayList<>());
		}
		
		jugadores[0] = new Jugador(0, baraja);
		jugadores[1] = ai1;
		jugadores[2] = ai2;
		jugadores[3] = ai3;
		for (int i = 0; i < 4; i++) {
			// draw 5 cards and put the cards into the player's hand
			for (int n = 0; n < 5; n++)
				addCardToHand(jugadores[i], baraja.reparte());
		}

	}

	//Sets up everything related to tradingareas
	private void tradeAreaSetup() {

		//Sets up tradingarea label to hold the 2 cards
		for (int i = 0; i < tradingAreaLabel.length; i++) {
			tradingAreaLabel[i] = new JLabel();
			tradingAreaLabel[i].setBounds(400 + (i * 100), 800, 100, 152);
			tradingAreaLabel[i].setVisible(true);
			panel.add(tradingAreaLabel[i]);
		}

		for (int n = 1; n < 4; n++) {
			//Setting up trade labels for ai offers
			tradeRequestLabel[n] = new JLabel("EMPTY");
			tradeRequestLabel[n].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			tradeRequestLabel[n].setBounds(1000 + n * 150, 50, 100, 150);

			panel.add(tradeRequestLabel[n]);
			tradeRequestLabel[n].setVisible(true);

			//Setting up accept buttons for ai trades (for the human player to click)
			tradeRequestButtonAccept[n] = new JButton("ACCEPT");
			tradeRequestButtonAccept[n].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			tradeRequestButtonAccept[n].addActionListener(this);
			tradeRequestButtonAccept[n].setEnabled(false);
			tradeRequestButtonAccept[n].setBounds(1000 + n * 150, 200, 100, 50);

			panel.add(tradeRequestButtonAccept[n]);
			tradeRequestButtonAccept[n].setVisible(true);
		}
	}

	private void setUpFieldButtons() {

		for (int p = 0; p < 4; p++) {

			fieldButtons.add(new ArrayList<JLabel>());

			for (int n = 0; n < 2; n++) {
				fieldButtons.get(p).add(new JLabel());
				// fieldButtons.get(p).get(n).addActionListener(this);
				panel.add(fieldButtons.get(p).get(n));
				fieldButtons.get(p).get(n).setVisible(true);
			}
		}

		// player 0 fieldButtons
		fieldButtons.get(0).get(0).setBounds(LENGTH / 2 - 50, WIDTH - 400, 100, 152);
		fieldButtons.get(0).get(1).setBounds(LENGTH / 2 + 50, WIDTH - 400, 100, 152);

		// player 1 fieldButtons
		fieldButtons.get(1).get(0).setBounds(LENGTH - 1205, WIDTH / 2 - 50, 100, 152);
		fieldButtons.get(1).get(1).setBounds(LENGTH - 1105, WIDTH / 2 - 50, 100, 152);

		// player 2 fieldButtons
		fieldButtons.get(2).get(0).setBounds(LENGTH / 2 - 50, 300, 100, 152);
		fieldButtons.get(2).get(1).setBounds(LENGTH / 2 + 50, 300, 100, 152);

		// player 3 fieldButtons
		fieldButtons.get(3).get(0).setBounds(LENGTH - 575, WIDTH / 2 - 50, 100, 152);
		fieldButtons.get(3).get(1).setBounds(LENGTH - 475, WIDTH / 2 - 50, 100, 152);

		for (int i = 0; i < 4; i ++) {
			for(int f = 0; f < 2; f++){
				fieldButtons.get(i).get(f).setBorder(BorderFactory.createLineBorder(Color.BLACK));
				fieldButtons.get(i).get(f).setIcon(new ImageIcon("images/field.png"));
			}
			
		}

		//setting up buttons for human to decide which field to plant to
		for (int i = 0; i < fieldDecideButtons.length; i++) {
			fieldDecideButtons[i] = new JButton("Plant to field " + (i + 1));
			fieldDecideButtons[i].setBounds(1300 + (i * 150), 950, 150, 50);
			fieldDecideButtons[i].addActionListener(this);
			panel.add(fieldDecideButtons[i]);
			fieldDecideButtons[i].setVisible(false);
		}
	}

	//Setting up the button for the draw pile
	public void setUpDrawButtons() {

		drawPileButton.setBounds(LENGTH - 975, WIDTH / 2 - 50, 100, 152);
		drawPileButton.setVisible(true);
		drawPileButton.addActionListener(this);
		drawPileButton.setIcon(new ImageIcon("images/Back.png"));
		drawPileButton.setEnabled(false);
		panel.add(drawPileButton);
	}

	//Updating treasury labels
	public void updateCoinLabel() {

		for (int x = 0; x < 4; x++) {
			coinDisplay[x].setText("Treasury: " + jugadores[x].getBanco());
		}
	}

	//Adds a card to the player's hand
	public void addCardToHand(Jugador player, Cartas card) {

		player.getMano().addCard(card);

		//Looping through the player's hand
		for (int i = 0; i < 4; i ++) {
			if (player == jugadores[i]) {
				handButtons.get(i).add(new JButton());
				handButtons.get(i).get(handButtons.get(i).size() - 1).setVisible(true);
				panel.add(handButtons.get(i).get(handButtons.get(i).size() - 1));

				//Relocating hand buttons to adjust
				for (int j = 0; j < handButtons.get(i).size(); j++) {
					if (i == 0)
						handButtons.get(i).get(j).setBounds(775 + j * 200 / handButtons.get(i).size(), 850, 100, 152);
					else if (i == 1)
						handButtons.get(i).get(j).setBounds(175 + j* 200 / handButtons.get(i).size(), 475, 100, 152);
					else if (i == 2)
						handButtons.get(i).get(j).setBounds(775 + j * 200 / handButtons.get(i).size(), 50, 100, 152);
					else if (i == 3)
						handButtons.get(i).get(j).setBounds(1350 + j * 200 / handButtons.get(i).size(), 475, 100, 152);
				}
				handButtons.get(i).get(handButtons.get(i).size() - 1).setIcon(new ImageIcon("images/" + card.getNombre() + ".png"));
			}
		}
	}

	//Removes a card from hand
	public void removeCardFromHand(int index, Jugador player) {
		player.getMano().removeCard(index);

		//looping through player's hand
		for (int i = 0; i < 4; i ++) {
			if (player == jugadores[i]) {
				handButtons.get(i).get(index).setVisible(false);
				handButtons.get(i).remove(index);
				if (handButtons.get(i).size() > 0) {
					//Relocating the button locations
					for (int j = 0; j < handButtons.get(i).size(); j++) {
						if (i == 0)
							handButtons.get(i).get(j).setBounds(775 + j * 200 / handButtons.get(i).size(), 850, 100, 152);
						else if (i == 1)
							handButtons.get(i).get(j).setBounds(175 + j * 200 / handButtons.get(i).size(), 475, 100, 152);
						else if (i == 2)
							handButtons.get(i).get(j).setBounds(775 + j * 200 / handButtons.get(i).size(), 50, 100, 152);
						else if (i == 3)
							handButtons.get(i).get(j).setBounds(1350 + j * 200 / handButtons.get(i).size(), 475, 100, 152);
					}
				}
			}
		}
	}

	//Adds the specified card to the tradingarea
	public void addCardToTradingArea(Intercambios tradingArea, Cartas card) {
		tradingArea.addCard(card);
		tradingAreaLabel[tradingArea.getNumInt()].setIcon(new ImageIcon("images/" + card.getNombre() + ".png"));
	}

	//Removes a card from the specified tradingarea
	public void removeCardFromTradingArea(Intercambios tradingArea) {
		tradingAreaLabel[tradingArea.getNumInt()].setBorder(null);
		tradingArea.removeCard();
		tradingArea = null;
	}

	// card1 - card that was in TA, card2 - card that was in offerer's hand
	private void trade(Jugador trader, Cartas card1, Jugador offerer, Cartas card2) {
		// Once a trade is accepted, disable trade buttons
		for (int n = 1; n < 4; n++)
			tradeRequestButtonAccept[n].setEnabled(false);
		JOptionPane.showMessageDialog(frame, "Trade Successful, Planted in field. Please wait until the timer runs out.");
		// If not human player, automatically plant
		if (offerer != jugadores[0])
			// the offerer plants the traded card directly into the field
			plant(offerer, offerer.pickFieldToPlant(card1), card1);
		else {
			//Otherwise check if fields and cards are identical and plant into that field
			if(card1 == jugadores[0].getCampos()[0].getTipo())
				plant(offerer, jugadores[0].getCampos()[0], card1);
			else
				//If none of the above, plant into the second field
				plant(offerer, jugadores[0].getCampos()[1], card1);
		}

		//If the trader is not the human player
		if (trader != jugadores[0]){
			//use the ai to decide which field to plant to
			plant(trader, trader.pickFieldToPlant(card2), card2);
		}else{
			//otherwise ask the user which field to plant to
			int reply = JOptionPane.showConfirmDialog(null, "Plant in field... y: field 1, n: field 2", "TradeResults",
					JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION) {
				// trader plants card in field 0
				plant(trader, trader.getCampos()[0], card2);
			} else {
				// trader plants card in field 1
				plant(trader, trader.getCampos()[1], card2);
			}
		}
	}

	//Plants the specified card to the specified player's field
	public void plant(Jugador player, Campo field, Cartas card) {

		//Checks if the field is empty to set cardtype
		if (field.isEmpty()) {
			field.setTipo(card);
			field.setNumCartas(1);
			fieldButtons.get(player.getNumJ()).get(field.getNumeroCampo()).setIcon(new ImageIcon("images/" + card.getNombre() + ".png"));
		//If the same card already exists, increment
		} else {
			if (field.getTipo() == card) {
				field.increaseNumCards(1);
				//Automatically harvest when reached max coin value
				//Special cases for garden bean (has no 4 coin value)
				if (field.getTipo().getNombre().equals("Garden Bean")) {
					if (field.coinFromHarvest() == 3) {
						harvest(player, field);
						field.setTipo(null);
						field.setNumCartas(0);
						fieldButtons.get(player.getNumJ()).get(field.getNumeroCampo()).setIcon(null);
					}
				//All other bean types have 4 coin values
				} else {
					if (field.coinFromHarvest() == 4) {
						harvest(player, field);
						field.setTipo(null);
						field.setNumCartas(0);
						fieldButtons.get(player.getNumJ()).get(field.getNumeroCampo()).setIcon(null);
					}
				}
			//Otherwise just harvest field
			} else {
				harvest(player, field);
				field.setTipo(card);
				field.setNumCartas(1);
				fieldButtons.get(player.getNumJ()).get(field.getNumeroCampo()).setIcon(new ImageIcon("images/" + card.getNombre() + ".png"));
			}
		}
		//Update field number labels
		updateFieldNum();
		
		//updating AllFields[][] for the ai to reference
		for (int i = 0; i < 4; i ++)
			for (int h = 0; h < 4; h ++)
				for (int j = 0; j < 2; j ++)
					jugadores[i].campos[h][j] = jugadores[h].getCampos()[j];

		playPlantingSound(plantingSound);
	}

	public void harvest(Jugador player, Campo field) {
		player.addBanco(field.coinFromHarvest());

		// Adds harvested card to the discard pile
		for (int h = 0; h < field.getNumCartas(); h++)
			baraja.aDescartar(field.getTipo());

		field.setNumCartas(0);
		field.setTipo(null);
		fieldButtons.get(player.getNumJ()).get(field.getNumeroCampo()).setIcon(null);
		updateCoinLabel();
	}

	//Sets the deciding which to plant to buttons for human player invisible
	private void decideButtonsVisibility(Boolean field1, Boolean field2, Boolean tradingArea) {
		fieldDecideButtons[0].setVisible(field1);
		fieldDecideButtons[1].setVisible(field2);
	}

	public void actionPerformed(ActionEvent event) {
		//Human offering card to ai trade
		if (humanTurnStage == 3) {
			for (int x = 0; x < jugadores[0].getMano().getCartaMano().size(); x ++) {
				if (event.getSource() == handButtons.get(0).get(x)) {
					handCardNum = x;
					playerTradeCards[0][0] = jugadores[0].getMano().getCarta(x);
					JOptionPane.showMessageDialog(frame, playerTradeCards[0][0].getNombre() + " has been selected to trade. Click on another to change.");
					break;
				}
			}
		}

		if (event.getSource() == helpButton) {
			//Opens a web link
			try {         
			     java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://www.google.com"));
			   }
			   catch (java.io.IOException e) {
			       System.out.println(e.getMessage());
			   }

			// if the first card in the hand pile is clicked
		}else if (event.getSource() == stopPlantingButton) {
			//Disables buttons and allows human to press the draw pile
			drawPileButton.setEnabled(true);
			humanPlantTimes = 0;
			handButtons.get(0).get(0).removeActionListener(this);
			stopPlantingButton.setEnabled(false);
			fieldDecideButtons[0].setVisible(false);
			fieldDecideButtons[1].setVisible(false);

			// if the first card in the hand pile is clicked
		} else if (event.getSource() == handButtons.get(0).get(0)) {
			if (humanTurnStage != 3) {
				if (currentCard == null) {
					decideButtonsVisibility(true, true, false);
					currentCard = jugadores[0].getMano().getCarta(0);
				}
			}
		} else if (event.getSource() == drawPileButton) {
			//adds 2 cards to trading area and set drawpile invisible
			addCardToTradingArea(jugadores[0].getTradingArea()[0], baraja.reparte());
			addCardToTradingArea(jugadores[0].getTradingArea()[1], baraja.reparte());
			updatePiles();
			humanTurnStage = 2;
			drawPileButton.setEnabled(false);
			JOptionPane.showMessageDialog(frame, "Trading begins shortly.");
		} else if (event.getSource() == gameTimer) {
			if (timeElapsed == 0) {
				timeElapsed = 20;
				timerLabel.setVisible(false);
				gameTimer.stop();
			} else {
				timeElapsed--;
				timerLabel.setText("Timer: " + timeElapsed);
			}
		}

		//Loops through all buttons for deciding which field to plant to
		for (int i = 0; i < fieldDecideButtons.length; i ++) {
			if (event.getSource() == fieldDecideButtons[i]) {
				//plant the card into the selected field and update hand
				plant(jugadores[0], jugadores[0].getCampos()[i], currentCard);
				currentCard = null;
				decideButtonsVisibility(false, false, false);
				removeCardFromHand(0, jugadores[0]);

				humanPlantTimes++;
				// Only allows the human player to plant twice
				if (humanPlantTimes < 2) {
					handButtons.get(0).get(0).addActionListener(this);
					stopPlantingButton.setEnabled(true);
				} else {
					// Start step 2 (drawing from the deck and trading)
					drawPileButton.setEnabled(true);
					stopPlantingButton.setEnabled(false);
					humanPlantTimes = 0;
				}
				updateFieldNum();
			}
		}

		for (int i = 0; i < tradingAreaLabel.length; i ++) {
			if (event.getSource() == tradingAreaLabel[i]) {
				currentCard = jugadores[0].getTradingArea()[i].getCartas();
				decideButtonsVisibility(true, true, false);
				removeCardFromTradingArea(jugadores[0].getTradingArea()[i]);
			}
		}

		//If the human player accepted a trade
		for (int i = 1; i < tradeRequestButtonAccept.length; i ++) {
			if (event.getSource() == tradeRequestButtonAccept[i]) {
				//Determine whether it's the first or second trading area card
				if (cardToTrade == 0) {
					//Perform trade action and set remove tradingarea card icon
					trade(jugadores[0], jugadores[0].getTradingArea()[0].getCartas(), jugadores[i], playerTradeCards[i][0]);
					tradingAreaLabel[0].setIcon(null);
					removeCardFromTradingArea(jugadores[0].getTradingArea()[0]);
					removeCardFromHand(jugadores[i].getMano().getCartaMano().indexOf(jugadores[i].pickCardToTrade(jugadores[0])),jugadores[i]);

				} else if (cardToTrade == 1) {
					//Perform trade action and set remove tradingarea card icon
					trade(jugadores[0], jugadores[0].getTradingArea()[1].getCartas(), jugadores[i], playerTradeCards[i][1]);
					tradingAreaLabel[1].setIcon(null);
					removeCardFromTradingArea(jugadores[0].getTradingArea()[1]);
					removeCardFromHand(jugadores[i].getMano().getCartaMano().indexOf(jugadores[i].pickCardToTrade(jugadores[0])),jugadores[i]);
				}
				tradeRequestLabel[i].setIcon(null);
			}
		}
	}

	// method that plays the planting sound when called
	private static void playPlantingSound(File Sound) {
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(Sound));
			clip.start();
			// if audio file not found, shows that it is an error
		} catch (Exception e) {
		}
	}

	// plays music from a sound file (background music)
	private static void playBackgroundMusic(File Sound) {
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(Sound));
			clip.start();
			clip.loop(1000000000);
			// reduce the volume of the audio clip
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-10.0f); // Reduce volume by 20 decibels.
			// if audio file not found, shows that it is an error
		} catch (Exception e) {
		}
	}
}