package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.Random;

import javax.lang.model.type.IntersectionType;

public class MarioApp extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manState = 0;
	int pause = 0;
	float gravity = 0.2f;
	float velocity = 0;
	int score = 0;
	int manY = 0;
	Random random1, random2;

	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
	Rectangle manRectangles;
	BitmapFont font;
	Texture bomb;
	Texture dizzy;
	int bombCount;
	Texture coin;
	int coinCount, gameState=0;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		dizzy = new Texture("dizzy-1.png");
		manY = Gdx.graphics.getHeight()/2;
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		random1 = new Random();
		random2 = new Random();
	}

	public void makeCoin() {
		float height = random1.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb(){
		float height = random2.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 1){
			if(coinCount < 100){
				coinCount++;
			} else {
				coinCount = 0;
				makeCoin();
			}

			coinRectangles.clear();
			for(int i=0; i<coinXs.size(); i++){
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i,coinXs.get(i)-4);
				coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}

			if(bombCount < 250){
				bombCount++;
			} else {
				bombCount = 0;
				makeBomb();
			}
			bombRectangles.clear();
			for(int i=0; i<bombXs.size();i++){
				batch.draw(bomb, bombXs.get(i), bombYs.get(i));
				bombXs.set(i, bombXs.get(i)-8);
				bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
			}

			if(Gdx.input.justTouched()){
				velocity = -10;
			}
			if(pause < 3){
				pause++;
			} else {
				pause = 0;
				if(manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}

			velocity += gravity;
			manY -= velocity;

			if(manY <= 0){
				manY = 0;
			}


		} else if(gameState == 0){

			if(Gdx.input.justTouched()){
				gameState = 1;
			}

		} else if(gameState == 2) {
			if(Gdx.input.justTouched()){
				gameState = 1;
				manY = Gdx.graphics.getHeight()/2;
				score = 0;
				velocity = 0;
				coinYs.clear();
				coinXs.clear();
				coinRectangles.clear();
				coinCount=0;
				bombRectangles.clear();
				bombXs.clear();
				bombYs.clear();
				bombCount = 0;
			}

		}

		if(gameState == 2){
			batch.draw(dizzy,Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2 , manY);
		} else {
			batch.draw(man[manState],Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2 , manY);
		}

		manRectangles = new Rectangle(Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2, manY, man[manState].getWidth(), man[manState].getHeight());


		for(int i=0; i< coinRectangles.size(); i++){
			if(Intersector.overlaps(manRectangles, coinRectangles.get(i))){
				score++;

				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}

		for(int i=0; i< bombRectangles.size(); i++){
			if(Intersector.overlaps(manRectangles, bombRectangles.get(i))){
				gameState = 2;
			}
		}

		font.draw(batch, String.valueOf(score), 100, 200);

		batch.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
