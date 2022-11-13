package demoMod.bililivedanmu.vfx;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.bililivedanmu.BiliLiveDanmu;

public class MessageEffect extends AbstractGameEffect {
    private String msg;
    private int slot;
    public float height = -1.0F;
    private float lineHeight;
    private float animY = 0.0F;
    private int lineNum = 1;
    public float x;
    public float y;

    public MessageEffect(String msg, int slot) {
        this.msg = msg;
        this.slot = slot;
        this.color = Color.WHITE.cpy();
        this.color.a = 0.0F;
    }

    @Override
    public void update() {
        if (this.height < 0) {
            FontHelper.cardTitleFont.getData().setScale(1.0F);
            this.height = FontHelper.getHeight(FontHelper.cardTitleFont, " ",1.0F) * 1.3F;
            this.lineHeight = this.height;
            FontHelper.layout.setText(FontHelper.cardTitleFont, this.msg, Color.WHITE, Gdx.graphics.getWidth() * 0.18F, 8, true);
            this.lineNum = (int) Math.floor(FontHelper.layout.height / FontHelper.cardTitleFont.getData().capHeight);
            if (lineNum <= 0) {
                lineNum = 1;
            }
            this.height *= lineNum;
            for (AbstractGameEffect effect : MessageBox.messages) {
                if (effect != MessageEffect.this) {
                    ((MessageEffect) effect).moveUp(lineNum);
                }
            }
        }
        if (this.color.a < 0.6F) {
            this.color.a += Gdx.graphics.getDeltaTime();
            if (this.color.a > 0.6F) {
                this.color.a = 0.6F;
            }
        }
        if (this.slot < 0) {
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        FontHelper.cardTitleFont.getData().setScale(1.0F);
        FontHelper.renderSmartText(sb, FontHelper.cardTitleFont, this.msg, x,
                this.y + this.height + this.animY,
                Gdx.graphics.getWidth() * 0.18F,
                FontHelper.cardTitleFont.getLineHeight(),
                this.color);
    }

    public void moveUp(int lines) {
        AbstractGameAction moveUpAction = new AbstractGameAction() {
            @Override
            public void update() {
                animY = Interpolation.exp5Out.apply(0, lineHeight * (float) lines, (0.5F - this.duration) / 0.5F);
                if (slot == 0) {
                    color.a = Interpolation.exp5In.apply(0.6F, 0, 0.5F - this.duration);
                }
                tickDuration();
                if (isDone) {
                    height += animY;
                    animY = 0.0F;
                    slot--;
                }
            }
        };
        ReflectionHacks.setPrivate(moveUpAction, AbstractGameAction.class, "duration", 0.5F);
        BiliLiveDanmu.addParallelAction(moveUpAction);
    }

    @Override
    public void dispose() {

    }
}
