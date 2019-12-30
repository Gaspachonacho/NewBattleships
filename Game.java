package battleships;

import java.util.*;

public class Game {
	String randomCoordinate;
	int amountOfPlayers;
	int turnCounter = 1;
	Pieces miss = new Pieces('O');
	Pieces hit = new Pieces('X');
	Scanner scan = new Scanner(System.in);
	LinkedList<Player> players = new LinkedList<Player>();
	
	public void PvP() {
		System.out.println("How many would like to play? Max is 4");
		int input = scan.nextInt();
		amountOfPlayers = input;
		createPlayer(amountOfPlayers);
		playerPlaceBoats();
		while(amountOfPlayers > 1) {
			PvPTurn();
		}
		for(Player p : players) {
			System.out.println("Congratulations " + p.getNickname() + "!");
			System.out.println("You Won! \n");
		}
	}
	
	public void PvE() {
		createPlayer(1);
		System.out.println("How many bots do you want to play against? Max is 3");
		int input = scan.nextInt();
		amountOfPlayers = input + 1;
		for(int i = 2; i <= amountOfPlayers; i++) {
			String datorNamn = "Göran " + (i - 1);
			Player computerPlayer = new Player(i, 0, datorNamn, null, null,null); 
			players.add(computerPlayer);
		}
		computerPlaceBoats();
		while(amountOfPlayers > 1) {
			PvETurn();
		}
		for(Player p : players) {
			System.out.println("Congratulations " + p.getNickname() + "!");
			System.out.println("You Won! \n");
		}
	}
	
	public void createPlayer(int amountOfPlayers) {
		for(int i = 1; i <= amountOfPlayers; i++) {
			System.out.println("Vad ska spelare " + i + " heta?");
			String spelarnamn = scan.next();
			Player newPlayer = new Player(i, 0, spelarnamn, null, null,null); 
			players.add(newPlayer);
		}
	}
	
	public void playerPlaceBoats() {
		Boats.printBoatList(Boats.boats);
		for(Player p : players) {
			Board playerBoard = new Board();
			Board enemyBoard = new Board();
			System.out.println("Time for " + p.getNickname() + " to place his boats" + "\n");
			playerBoard.makeBoard();
			playerBoard.placeBoats();
			p.setBoats(playerBoard.getPlayerBoatList());
			p.setPlayerBoard(playerBoard.getPlayerHashMap());
			p.setHealth(playerBoard.getHealth());
			enemyBoard.makeBoard();
			p.setEnemyBoard(enemyBoard.getPlayerHashMap());
		}
		printPlayerList();
	}
	
	public void computerPlaceBoats() {
		Boats.printBoatList(Boats.boats);
		for(Player p : players) {
			if(p.getTurn() == 1) {
				Board playerBoard = new Board();
				Board enemyBoard = new Board();
				System.out.println("Time for " + p.getNickname() + " to place his boats" + "\n");
				enemyBoard.makeBoard();
				playerBoard.makeBoard();
				playerBoard.placeBoats();
				p.setBoats(playerBoard.getPlayerBoatList());
				p.setPlayerBoard(playerBoard.getPlayerHashMap());
				p.setHealth(playerBoard.getHealth());
				p.setEnemyBoard(enemyBoard.getPlayerHashMap());
			}
			else {
				Board computerBoard = new Board();
				Board enemyBoard = new Board();
				enemyBoard.makeBoard();
				computerBoard.makeBoard();
				computerBoard.computerPlaceBoats();
				p.setBoats(computerBoard.getPlayerBoatList());
				p.setPlayerBoard(computerBoard.getPlayerHashMap());
				p.setHealth(computerBoard.getHealth());
				p.setEnemyBoard(enemyBoard.getPlayerHashMap());
			}
		}
		printPlayerList();
	}
	
	public void PvPTurn() {
		for(Player p : players) {	
			if(p.getTurn() == turnCounter) {
				System.out.println(p.getNickname() + "'s turn! \n");
				printBoard();
				break;
			}
		}
		
		for(Player p : players) {
			if(p.getTurn() != turnCounter) {
				while (shootPvP(p) == true);
			}
		}
		
		turnCounter++;
		
		if(turnCounter > amountOfPlayers) {
			turnCounter = 1;
		}
		
	}
	
	public void PvETurn() {
		for(Player p : players) {	
			if(p.getTurn() == 1) {
				System.out.println(p.getNickname() + "'s turn! \n");
				printBoard();
				break;
			}
		}
		
		for(Player p : players) {	
			if(p.getTurn() != 1) {
				while (shootPvP(p) == true);
			}
		}
		
		turnCounter++;
		
		while(!(turnCounter > amountOfPlayers)) {
			for(Player p : players) { 	
				if(p.getTurn() != turnCounter) {
					while (shootPvE(p) == true);
				}	
			}
			turnCounter++;
		}
		
		turnCounter = 1;
		
		
	}
	
	public boolean shootPvP(Player p) {
			
			List<Coordinates> newCoordinates = new ArrayList<Coordinates>();
			List<Boats> newBoats = new ArrayList<Boats>();
			
			boolean a = false;
		
			System.out.println("Where would you like to shoot " + p.getNickname() + "?");
			String playerAim = scan.next();
			System.out.println();
			Coordinates playerShot = new Coordinates(playerAim);
			while(p.getEnemyBoard().get(playerShot).equals(miss) || p.getEnemyBoard().get(playerShot).equals(hit)) {
				System.out.println("Where would you like to shoot " + p.getNickname() + "?");
				playerAim = scan.next();
				playerShot = new Coordinates(playerAim);
			}
			for (Boats boats : p.getBoats()) {
				for(Coordinates coordinates : boats.getBoatCoordinates()) {
					if(coordinates.equals(playerShot)) {
						a = true;
						System.out.println("Hit! \n");
						p.getPlayerBoard().put(playerShot, hit);
						p.getEnemyBoard().put(playerShot, hit);
						p.setHealth(p.getHealth() - 1);
						newCoordinates.add(playerShot);
					}
				}
				for(Coordinates coord : newCoordinates) {
					boats.getBoatCoordinates().remove(coord);
					boats.setSize(boats.getSize()-1);
				}
				
				if(boats.getSize() == 0) {
					newBoats.add(boats);
					System.out.println("You sunk " + p.getNickname() + "'s " + boats.getName() + "\n");
				}
				
				if(a == true) {
					break;
				}
			}
			
			for(Boats boats : newBoats) {
				p.getBoats().remove(boats);
			}
			
			if(p.getHealth() == 0) {
				System.out.println(p.getNickname() + "'s Fleet has been destroyed! \n");
				players.remove(p);
				amountOfPlayers--;
				return false;
			}
			
			if(a == false) {
				System.out.println("Miss! \n");
				p.getPlayerBoard().put(playerShot, miss);
				p.getEnemyBoard().put(playerShot, miss);
				return false;
			}
			
			return true;
			
	}
	
	public boolean shootPvE(Player p) {
		List<Coordinates> newCoordinates = new ArrayList<Coordinates>();
		List<Boats> newBoats = new ArrayList<Boats>();
		
		boolean a = false;
		
		System.out.println("Firing at: " + p.getNickname());
		String botAim = getRandomCoordinate();
		System.out.println(botAim);
		Coordinates botShot = new Coordinates(botAim);
		while(p.getEnemyBoard().get(botShot).equals(miss) || p.getEnemyBoard().get(botShot).equals(hit)) {
			botAim = getRandomCoordinate();
			botShot = new Coordinates(botAim);
			System.out.println("Refiring at: " + p.getNickname());
			System.out.println(botAim);
		}
		for (Boats boats : p.getBoats()) {
			for(Coordinates coordinates : boats.getBoatCoordinates()) {
				if(coordinates.equals(botShot)) {
					a = true;
					System.out.println("Hit! \n");
					p.getPlayerBoard().put(botShot, hit);
					p.getEnemyBoard().put(botShot, hit);
					p.setHealth(p.getHealth() - 1);
					newCoordinates.add(botShot);
				}
			}
			for(Coordinates coord : newCoordinates) {
				boats.getBoatCoordinates().remove(coord);
				boats.setSize(boats.getSize()-1);
			}
			
			if(boats.getSize() == 0) {
				newBoats.add(boats);
				System.out.println("A bot has sunk " + p.getNickname() + "'s " + boats.getName() + "\n");
			}
			
			if(a == true) {
				break;
			}
		}
		
		for(Boats boats : newBoats) {
			p.getBoats().remove(boats);
		}
		
		if(p.getHealth() == 0) {
			System.out.println(p.getNickname() + "'s Fleet has been destroyed! \n");
			players.remove(p);
			amountOfPlayers--;
			return false;
		}
		
		if(a == false) {
			System.out.println("Miss! \n");
			p.getPlayerBoard().put(botShot, miss);
			p.getEnemyBoard().put(botShot, miss);
			return false;
		}
		
		return true;
	}

	public void printBoard() {
		for(Player p : players) {
			if(p.getTurn() == turnCounter) {
				
				System.out.println(p.getNickname() + "'s Board");
				System.out.println("=======================");
				
				System.out.println("  | 0 1 2 3 4 5 6 7 8 9");
				System.out.println("--+--------------------");
	
				char k = 'A';
				
				for(Coordinates keys : p.getPlayerBoard().keySet()) {
				
					if(keys.toString().charAt(1) == '0') {
						System.out.print(k + " | ");
						k++;
					}	
			
					System.out.print(p.getPlayerBoard().get(keys) + " ");
			
					if(keys.toString().charAt(1) == '9') {
						System.out.println("");
					}
				}

				System.out.println("\n");
			}
		}
		
		for(Player p : players) {
			if(p.getTurn() != turnCounter) {
				System.out.println(p.getNickname() + "'s lines");
				System.out.println("=======================");
				
				System.out.println("  | 0 1 2 3 4 5 6 7 8 9");
				System.out.println("--+--------------------");
	
				char k = 'A';
				
				for(Coordinates keys : p.getEnemyBoard().keySet()) {
				
					if(keys.toString().charAt(1) == '0') {
						System.out.print(k + " | ");
						k++;
					}	
			
					System.out.print(p.getEnemyBoard().get(keys) + " ");
			
					if(keys.toString().charAt(1) == '9') {
						System.out.println("");
					}
				}

				System.out.println("\n");
			}
		}
			
	}
	
	public void printPlayerList() {
		System.out.println("\n");
		
		System.out.println("Here are all captains!");
		for(Player players : players) {
			System.out.println(players);
		}
		
		System.out.println("\n"); 
	}
	
	
	
	public String getRandomCoordinate(){
		double xmin = 0;
		double xmax = 9;
		int xCoord = (int) ((Math.random()*((xmax-xmin)+1))+xmin);
		
		double ymin = 65;
		double ymax = 74;
		char yCoord = (char) ((Math.random()*((ymax-ymin)+1))+ymin);
		
		String coordinate = "" + yCoord + xCoord;
		
		return coordinate;
	}
	
	public void setRandomCoordinate(String coordinate) {
		randomCoordinate = coordinate;
	}
	
}
