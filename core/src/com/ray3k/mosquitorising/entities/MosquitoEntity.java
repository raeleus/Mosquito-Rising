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
import com.ray3k.mosquitorising.Core;
import com.ray3k.mosquitorising.Entity;
import com.ray3k.mosquitorising.SpineEntity;
import com.ray3k.mosquitorising.states.GameState;

public class MosquitoEntity extends SpineEntity {
    private static final float CHASE_SPEED = 65.0f;
    private boolean chasePlayer;
    private static final Vector2 temp = new Vector2();
    private static final float VARIANCE = 60.0f;
    
    public MosquitoEntity() {
        super(Core.DATA_PATH + "/spine/mosquito.json", "movement1");
    }

    @Override
    public void actSub(float delta) {
        if (chasePlayer) {
            temp.set(getPosition());
            temp.sub(GameState.player.getPosition());
            setMotion(CHASE_SPEED, temp.angle() + 180.0f  - VARIANCE/ 2.0f + MathUtils.random(VARIANCE));
        }
    }

    @Override
    public void drawSub(SpriteBatch spriteBatch, float delta) {
    }

    @Override
    public void create() {
        int choice = MathUtils.random(3);
        
        switch (choice) {
            case 0:
                getAnimationState().setAnimation(0, "movement1", true);
                break;
            case 1:
                getAnimationState().setAnimation(0, "movement2", true);
                break;
            case 2:
                getAnimationState().setAnimation(0, "movement3", true);
                break;
            case 3:
                getAnimationState().setAnimation(0, "movement4", true);
                break;
        }
        
        getAnimationState().setAnimation(1, "spawn", false);
        
        getSkeleton().setFlipX(MathUtils.randomBoolean());
        chasePlayer = MathUtils.randomBoolean(GameState.chance);
    }

    @Override
    public void actEnd(float delta) {
    }

    @Override
    public void destroy() {
        PainEntity pain = new PainEntity();
        pain.setPosition(getX(), getY());
        GameState.entityManager.addEntity(pain);
    }

    @Override
    public void collision(Entity other) {
    }

}
