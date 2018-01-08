/*
 * The MIT License
 *
 * Copyright 2018 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ray3k.mosquitorising.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.ray3k.mosquitorising.Entity;
import com.ray3k.mosquitorising.states.GameState;
import static com.ray3k.mosquitorising.states.GameState.entityManager;

public class SpawnControllerEntity extends Entity {
    private float difficulty;
    private float enemySpawnTimer;
    private float carrotSpawnTimer;
    private static final float DIFFICULTY_INCREASE = .1f;
    private static final float ENEMY_SPAWN_TIME = 10.0f;
    private static final float CARROT_SPAWN_TIME = 11.0f;
    private static final float SPAWN_DISTANCE = 200.0f;
    private static final Vector2 temp = new Vector2();

    @Override
    public void create() {
        difficulty = 1.0f;
        enemySpawnTimer = ENEMY_SPAWN_TIME / difficulty;
        carrotSpawnTimer = CARROT_SPAWN_TIME;
        spawnEnemy();
        spawnCarrot();
    }

    @Override
    public void act(float delta) {
        difficulty += DIFFICULTY_INCREASE * delta;
        
        enemySpawnTimer -= delta;
        if (enemySpawnTimer < 0) {
            enemySpawnTimer = ENEMY_SPAWN_TIME / difficulty;
            
            spawnEnemy();
        }
        
        carrotSpawnTimer -= delta;
        if (carrotSpawnTimer < 0) {
            carrotSpawnTimer =  CARROT_SPAWN_TIME;
            
            spawnCarrot();
        }
    }

    @Override
    public void actEnd(float delta) {
    }

    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void collision(Entity other) {
    }

    private void spawnEnemy() {
        MosquitoEntity mosquito = new MosquitoEntity();

        float x, y;
        do {
            x = MathUtils.random(GameState.GAME_WIDTH);
            y = MathUtils.random(GameState.GAME_HEIGHT);
            temp.set(x, y);
        } while (temp.dst(GameState.player.getPosition()) < SPAWN_DISTANCE);

        mosquito.setPosition(x, y);
        entityManager.addEntity(mosquito);
    }
    
    private void spawnCarrot() {
        CarrotEntity carrot = new CarrotEntity();
        
        float x, y;
        do {
            x = MathUtils.random(GameState.GAME_WIDTH);
            y = MathUtils.random(GameState.GAME_HEIGHT);
            temp.set(x, y);
        } while (temp.dst(GameState.player.getPosition()) < SPAWN_DISTANCE);

        carrot.setPosition(x, y);
        entityManager.addEntity(carrot);
    }
}
