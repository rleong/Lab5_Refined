package poker.app.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import exceptions.DeckException;
import netgame.common.Hub;
import poker.app.view.PokerTableController;
import poker.app.view.RootLayoutController;
import pokerBase.Action;
import pokerBase.Card;
import pokerBase.Deck;
import pokerBase.GamePlay;
import pokerBase.GamePlayPlayerHand;
import pokerBase.Player;
import pokerBase.Rule;
import pokerBase.Table;
import pokerEnums.eAction;
import pokerEnums.eDrawCount;
import pokerEnums.eGame;
import pokerEnums.eGameState;

public class PokerHub extends Hub {

	private Table HubPokerTable = new Table();
	private GamePlay HubGamePlay;
	private int iDealNbr = 0;
	//private PokerGameState state;
	private eGameState eGameState;
	private eGame eGame;
	private RootLayoutController RootTable = new RootLayoutController();
	private PokerTableController PokerTable = new PokerTableController();
	private Rule Rules;

	public PokerHub(int port) throws IOException {
		super(port);
	}

	protected void playerConnected(int playerID) {

		if (playerID == 2) {
			shutdownServerSocket();
		}
	}

	protected void playerDisconnected(int playerID) {
		shutDownHub();
	}

	@SuppressWarnings("null")
	protected void messageReceived(int ClientID, Object message) {

		if (message instanceof Action) {
			Action act = (Action) message;
			switch (act.getAction()) {
			case GameState:
				sendToAll(HubPokerTable);
				break;
			case TableState:
				resetOutput();
				sendToAll(HubPokerTable);
				break;
			case Sit:
				resetOutput();
				HubPokerTable.AddPlayerToTable(act.getPlayer());				
				sendToAll(HubPokerTable);				
				break;
			case Leave:
				resetOutput();
				HubPokerTable.RemovePlayerFromTable(act.getPlayer());
				sendToAll(HubPokerTable);				
				break;
				
			case StartGame:
				//System.out.println("Starting Game!");
				resetOutput();
				
				//TODO - Lab #5 Do all the things you need to do to start a game!!
				
				//	Determine which game is selected (from RootTableController)
				//		1 line of code
				pokerEnums.eGame gameName;
				//int gameRules;
				
				//	Get the Rule based on the game selected
				//		1 line of code
				gameName = eGame.valueOf(RootTable.getRuleName());
				//gameName = MainApp.getGameName();
				Rules = new Rule(gameName);
				
				//	The table should eventually allow multiple instances of 'GamePlay'...
				//		Each game played is an instance of 'GamePlay'...
				//		For the first instance of GamePlay, pick a random player to be the 
				//		'Dealer'...  
				//		< 5 lines of code to pick random player
				Player p = HubPokerTable.PickRandomPlayerAtTable();
				UUID puid = p.getPlayerID();				
				
				//	Start a new instance of GamePlay, based on rule set and Dealer (Player.PlayerID)
				//		1 line of code
				HubGamePlay = new GamePlay(Rules, puid);
				
				//	There are 1+ players seated at the table... add these players to the game
				//		< 5 lines of code
				for (int x=1; x<5; x++){
					Player temp_p = HubPokerTable.getPlayerByPosition(x);
					HubGamePlay.addPlayerToGame(temp_p);
				}
				
				//	GamePlay has a deck...  create the deck based on the game's rules (the rule
				//		will have number of jokers... wild cards...
				//		1 line of code
				HubGamePlay.setGameDeck(new Deck(Rules.GetNumberOfJokers(), Rules.GetWildCards()));

				//	Determine the order of players and add each player in turn to GamePlay.lnkPlayerOrder
				//	Example... four players playing...  seated in Position 1, 2, 3, 4
				//			Dealer = Position 2
				//			Order should be 3, 4, 1, 2
				//	Example...  three players playing... seated in Position 1, 2, 4
				//			Dealer = Position 4
				//			Order should be 1, 2, 4
				//		< 10 lines of code
				int[] positions = null;
				positions[0]=p.getiPlayerPosition();
				int temp_val = p.getiPlayerPosition();
				for(int i = 1; i < HubPokerTable.getHashPlayers().size(); i++){
					
					if (temp_val >= 4){
						temp_val = 1;
					}
					positions[i] = temp_val;
					temp_val++;
				}
				HubGamePlay.setiActOrder(positions);
				
				//	Set PlayerID_NextToAct in GamePlay (next player after Dealer)
				//		1 line of code
				
				HubGamePlay.setPlayerNextToAct(p);
				
				//Here's how to get an eNum based on a given value (Merry Christmas):)
				//eGame Game = eGame.getGame(1);

				//	Send the state of the game back to the players
				sendToAll(HubGamePlay);
				break;
			case Deal:
				
				/*
				int iCardstoDraw[] = HubGamePlay.getRule().getiCardsToDraw();
				int iDrawCount = iCardstoDraw[iDealNbr];

				for (int i = 0; i<iDrawCount; i++)
				{
					try {
						Card c = HubGamePlay.getGameDeck().Draw();
					} catch (DeckException e) {
						e.printStackTrace();
					}
				}
*/
				break;
			}
		}

		//System.out.println("Message Received by Hub");
	}

}