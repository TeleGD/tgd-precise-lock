package games.pong;

import java.util.ArrayList;
import java.util.List;

//import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.EmptyImageData;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import games.pong.bonus.Bonus;
import general.AppGame;
import general.Playable;

public class World extends BasicGameState implements Playable {

	private int ID;

	public int milieu[];
	public int taille;

	Player[] players;
	private List<Ball> balls;
	private List<Bonus> bonus;
	private int nbJoueurs;
	private Image background;

	public World (int ID) {
		this.ID = ID;
	}

	@Override
	public int getID () {
		return this.ID;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) {
		try {
			background = new Image ("images/Pong/backgroundDuck.png");
		} catch (SlickException exception) {
			background = new Image (new EmptyImageData (0, 0));
		}
		milieu = new int[2];
		milieu[0]=container.getWidth()/2;
		milieu[1]=container.getHeight()/2;
		taille = Math.min(milieu[0]*2,milieu[1]*2);
	}

	@Override
	public void enter (GameContainer container, StateBasedGame game) {
		container.getInput ().clearKeyPressedRecord ();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		Input input = container.getInput ();
		AppGame appGame = (AppGame) game;
		int gameMasterID = appGame.appPlayers.get (0).getControllerID ();
		if (input.isKeyPressed (Input.KEY_ESCAPE) || input.isButtonPressed (AppGame.BUTTON_PLUS, gameMasterID)) {
			game.enterState (general.AppGame.MENUS_GAMES_MENU, new FadeOutTransition (), new FadeInTransition ());
		} else {
			if (balls.size() == 0) {
				balls.add(new Ball(this));
			}
			for (int i=0; i < players.length ; i++) {
				players[i].update(container, game, delta);
				if (players[i].getVies()<=0 && players[i].getId()>=0) {
					players[i]=new Wall(this, i);
					nbJoueurs--;
				}
			}
			if (nbJoueurs<1) {
				System.out.println("Partie terminée");
				System.exit(0);
			}
			for (Ball b : balls) {
				b.update(container, game, delta);
				for (Bonus bo : bonus) {
					bo.update(container, game, delta ,b);
				}
			}
			for (int i=0 ; i<balls.size() ; i++) {
				int out = balls.get(i).isOut();
				if (out != -1) {
					balls.remove(i);
					if (out<4) {
						players[out].loseVie();
					}
				}
			}
			for (int i=0 ; i<bonus.size() ; i++) {
				if (bonus.get(i).isPicked()) {
					bonus.remove(i);
				}
			}
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics context) {
		context.drawImage(background,milieu[0]-taille/2,milieu[1]-taille/2,milieu[0]+taille/2,milieu[1]+taille/2,0,0,background.getWidth(),background.getHeight());
		for (Player p : players) {
			p.render(container, game, context);
		}
		for (Bonus b : bonus) {
			b.render(container, game, context);
		}
		for (Ball b : balls) {
			b.render(container, game, context);
		}
	}

	@Override
	public void initPlayers (GameContainer container, StateBasedGame game) {
		AppGame appGame = (AppGame) game;
		nbJoueurs = appGame.appPlayers.size ();
		this.players = new Player [4];
		for (int i = 0; i < 4; i++) {
			this.players [i] = i < nbJoueurs ? new Player (this, i, appGame.appPlayers.get (i)) : new Wall (this, i);
		};
		this.balls = new ArrayList <Ball> ();
		for (int i = 0; i < 1; i++) {
			balls.add (new Ball (this));
		}
		this.bonus = new ArrayList <Bonus> ();
	}

}
