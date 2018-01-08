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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Event;
import com.ray3k.mosquitorising.Core;
import com.ray3k.mosquitorising.Entity;
import com.ray3k.mosquitorising.SpineEntity;
import com.ray3k.mosquitorising.states.GameState;

public class PlayerEntity extends SpineEntity {
    private final static float MOVE_SPEED = 300.0f;
    private float scoreTimer;
    private final static float SCORE_TIME = 1.5f;
    private boolean dead;
    
    public PlayerEntity() {
        super(Core.DATA_PATH + "/spine/pig.json", "stand");
        getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void event(AnimationState.TrackEntry entry, Event event) {
                if (event.getData().getName().equals("oink")) {
                    GameState.inst().playSound("oink", 1.0f, MathUtils.random(.5f, 1.5f));
                }
            }
            
        });
    }
    
    @Override
    public void actSub(float delta) {
        if (!dead) {
            float speed = 0.0f;
            float direction = 0.0f;

            if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
                getSkeleton().setFlipX(true);
                speed = MOVE_SPEED;
                direction = 0.0f;
            } else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
                getSkeleton().setFlipX(false);
                speed = MOVE_SPEED;
                direction = 180.0f;
            }

            if (Gdx.input.isKeyPressed(Keys.UP)) {
                speed = MOVE_SPEED;
                direction = 90.0f;

                if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
                    direction -= 45.0f;
                } else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
                    direction += 45.0f;
                }
            } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
                speed = MOVE_SPEED;
                direction = 270.0f;

                if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
                    direction += 45.0f;
                } else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
                    direction -= 45.0f;
                }
            }

            if (MathUtils.isZero(speed)) {
                setMotion(0.0f, 0.0f);
                if (!getAnimationState().getCurrent(1).getAnimation().getName().equals("stand")) {
                    getAnimationState().setAnimation(1, "stand", true);
                }
            } else {
                setMotion(speed, direction);
                if (!getAnimationState().getCurrent(1).getAnimation().getName().equals("walk")) {
                    getAnimationState().setAnimation(1, "walk", true);
                }
            }

            float originalX = getX();
            float originalY = getY();

            setX(originalX + getXspeed() * delta);
            setY(originalY + getYspeed() * delta);

            getSkeleton().setPosition(getX(), getY());
            getSkeleton().updateWorldTransform();
            getSkeletonBounds().update(getSkeleton(), true);

            setPosition(originalX, originalY);

            if (getSkeletonBounds().getMinX() < 0.0f) {
                setXspeed(0.0f);
                addX(-getSkeletonBounds().getMinX());
            } else if (getSkeletonBounds().getMaxX() > GameState.GAME_WIDTH) {
                setXspeed(0.0f);
                addX(GameState.GAME_WIDTH - getSkeletonBounds().getMaxX());
            }

            if (getSkeletonBounds().getMinY() < 0.0f) {
                setYspeed(0.0f);
                addY(-getSkeletonBounds().getMinY());
            } else if (getSkeletonBounds().getMaxY() > GameState.GAME_HEIGHT) {
                setYspeed(0.0f);
                addY(GameState.GAME_HEIGHT - getSkeletonBounds().getMaxY());
            }

            getSkeleton().setPosition(getX(), getY());
            getSkeleton().updateWorldTransform();
            getSkeletonBounds().update(getSkeleton(), true);

            for (Entity entity : GameState.entityManager.getEntities()) {
                if (entity instanceof MosquitoEntity) {
                    MosquitoEntity mosquito = (MosquitoEntity) entity;
                    if (mosquito.getSkeletonBounds().aabbIntersectsSkeleton(getSkeletonBounds())) {
                        mosquito.dispose();
                        GameState.inst().playSound("hurt", 1.0f);
                        getAnimationState().setAnimation(0, "surprise", false);
                        getAnimationState().addAnimation(0, "blink", true, 0.0f);
                        GameState.inst().loseLife();

                        if (GameState.inst().getLives() <= 0) {
                            getAnimationState().setAnimation(0, "die", false);
                            getAnimationState().setEmptyAnimation(1, 0.0f);
                            dead = true;
                            setMotion(0.0f, 0.0f);
                            GameState.entityManager.addEntity(new GameOverTimerEntity(3.0f));
                        }
                    }
                } else if (entity instanceof CarrotEntity) {
                    CarrotEntity carrot = (CarrotEntity) entity;
                    if (carrot.getSkeletonBounds().aabbIntersectsSkeleton(getSkeletonBounds())) {
                        carrot.dispose();
                        GameState.inst().playSound("chew", .5f);
                        GameState.inst().addScore(10);
                        getAnimationState().setAnimation(0, "chew", false);
                        getAnimationState().addAnimation(0, "blink", true, 0.0f);
                        GameState.inst().resetLives();
                    }
                }
            }

            scoreTimer -= delta;
            if (scoreTimer < 0) {
                scoreTimer = SCORE_TIME;
                GameState.inst().addScore(1);
            }
        }
    }

    @Override
    public void drawSub(SpriteBatch spriteBatch, float delta) {
    }

    @Override
    public void create() {
        getAnimationState().setAnimation(0, "blink", true);
        getAnimationState().setAnimation(1, "stand", true);
        scoreTimer = SCORE_TIME;
        dead = false;
    }

    @Override
    public void actEnd(float delta) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void collision(Entity other) {
    }

}
