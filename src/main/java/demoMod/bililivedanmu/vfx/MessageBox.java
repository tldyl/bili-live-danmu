package demoMod.bililivedanmu.vfx;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.bililivedanmu.BiliLiveDanmu;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MessageBox {
    private static Texture bg;
    private float x;
    private float y;
    static final List<AbstractGameEffect> messages = new Vector<>();
    private int popularity = 0;
    private AbstractPower icon;
    private float lineHeight;
    private Hitbox hb;

    private boolean draggingMode = false;
    private float dragFadeOut = 0.0F;
    private int fadeOutX;
    private int fadeOutY;

    public MessageBox() {
        if (bg == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            Color c = Color.BLACK.cpy();
            c.a = 0.25F;
            pixmap.setColor(c);
            pixmap.fill();
            bg = new Texture(pixmap);
        }
        x = 0;
        y = Gdx.graphics.getHeight() * 0.1F;
        icon = new MantraPower(null, 0);
        lineHeight = FontHelper.getHeight(FontHelper.cardDescFont_N);
        this.hb = new Hitbox(x, y, Gdx.graphics.getWidth() * 0.2F, Gdx.graphics.getHeight() * 0.8F);
    }

    public void update() {
        synchronized (messages) {
            List<AbstractGameEffect> toRemove = new ArrayList<>();
            for (AbstractGameEffect effect : messages) {
                effect.update();
                if (effect.isDone) {
                    toRemove.add(effect);
                }
            }
            messages.removeAll(toRemove);
        }
        hb.update();
        if (this.hb != null) {
            if (this.hb.hovered && InputHelper.isMouseDown && !draggingMode) {
                draggingMode = true;
                dragFadeOut = 0.7F;
            }
            if (draggingMode && !InputHelper.isMouseDown) {
                draggingMode = false;
                fadeOutX = (int)((InputHelper.mX - this.x - this.hb.width / 2.0F) / 2 + this.x);
                fadeOutY = (int)((InputHelper.mY - this.y - this.hb.height / 2.0F) / 2 + this.y);
            }
            if (draggingMode) {
                float dx = this.calculateVelocity((int)this.x, (int) (InputHelper.mX - this.hb.width / 2.0F));
                float dy = this.calculateVelocity((int)this.y, (int)(InputHelper.mY - this.hb.height / 2.0F));
                synchronized (messages) {
                    for (AbstractGameEffect effect : messages) {
                        ((MessageEffect) effect).x += dx;
                        ((MessageEffect) effect).y += dy;
                    }
                }
                this.x += dx;
                this.y += dy;
                this.hb.move(this.x + Gdx.graphics.getWidth() * 0.1F, this.y + this.hb.height / 2.0F);
            } else if (dragFadeOut > 0) {
                float dx = this.calculateVelocity((int)this.x, fadeOutX);
                float dy = this.calculateVelocity((int)this.y, fadeOutY);
                synchronized (messages) {
                    for (AbstractGameEffect effect : messages) {
                        ((MessageEffect) effect).x += dx;
                        ((MessageEffect) effect).y += dy;
                    }
                }
                this.x += dx;
                this.y += dy;
                this.hb.move(this.x + Gdx.graphics.getWidth() * 0.1F, this.y + this.hb.height / 2.0F);
                this.dragFadeOut -= Gdx.graphics.getDeltaTime();
            }
        }
    }

    private int calculateVelocity(int src, int target) {
        return (target - src) / 10;
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        sb.draw(bg, x, y, Gdx.graphics.getWidth() * 0.2F, Gdx.graphics.getHeight() * 0.8F);
        synchronized (messages) {
            for (AbstractGameEffect effect : messages) {
                effect.render(sb);
            }
        }
        icon.renderIcons(sb, x + Gdx.graphics.getWidth() * 0.15F, y + 16.0F * Settings.scale, Color.WHITE);
        FontHelper.renderFontLeft(sb, FontHelper.cardDescFont_N, Integer.toString(this.popularity), x + Gdx.graphics.getWidth() * 0.15F + 24.0F * Settings.scale, y + lineHeight / 2.0F + 8.0F * Settings.scale, Color.WHITE);
        hb.render(sb);
    }

    public void addDanmu(String username, String msg) {
        synchronized (messages) {
            String coloredUsername = username.replace(" ", " #b");
            if (!coloredUsername.startsWith("#b")) {
                coloredUsername = "#b" + coloredUsername;
            }
            final String finalName = coloredUsername;
            AbstractGameAction action = new AbstractGameAction() {
                @Override
                public void update() {
                    if (duration == 0.5F) {
                        MessageEffect messageEffect = new MessageEffect(finalName + " " + msg, BiliLiveDanmu.maxDanmuSize);
                        messageEffect.x = MessageBox.this.x + Gdx.graphics.getWidth() * 0.01F;
                        messageEffect.y = MessageBox.this.y + Gdx.graphics.getHeight() * 0.02F;
                        messages.add(messageEffect);
                    }
                    tickDuration();
                }
            };
            ReflectionHacks.setPrivate(action, AbstractGameAction.class, "duration", 0.5F);
            BiliLiveDanmu.addToBot(action);
        }
    }

    public synchronized void setPopularity(int num) {
        this.popularity = num;
    }
}
