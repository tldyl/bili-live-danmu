package demoMod.bililivedanmu.ui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.SeedPanel;
import demoMod.bililivedanmu.BiliLiveDanmu;
import demoMod.bililivedanmu.helpers.RoomNumPanelInputProcessor;
import demoMod.bililivedanmu.relics.Danmu;

public class RoomNumPanel extends SeedPanel {
    public AbstractRelic relicRef;
    private Color uiColor = new Color(1.0F, 1.0F, 1.0F, 0.0F);
    private Color screenColor = new Color(0.0F, 0.0F, 0.0F, 0.0F);
    private static final UIStrings uiStrings;
    public static final String[] TEXT;
    private float animTimer = 0.0F;

    @Override
    public void confirm() {
        textField = textField.trim();

        if (this.relicRef != null) {
            ((Danmu)this.relicRef).setRoomId(Integer.parseInt(textField));
        }

        this.close();
    }

    @Override
    public void update() {
        if (this.shown) {
            if (this.animTimer != 0.0F) {
                this.animTimer -= Gdx.graphics.getDeltaTime();
                if (this.animTimer < 0.0F) {
                    this.animTimer = 0.0F;
                }
                this.screenColor.a = Interpolation.fade.apply(0.8F, 0.0F, this.animTimer * 1.0F / 0.25F);
                this.uiColor.a = Interpolation.fade.apply(1.0F, 0.0F, this.animTimer * 1.0F / 0.25F);
            }
            super.update();
        } else if (this.animTimer != 0.0F) {
            this.animTimer -= Gdx.graphics.getDeltaTime();
            if (this.animTimer < 0.0F) {
                this.animTimer = 0.0F;
            }

            this.screenColor.a = Interpolation.fade.apply(0.0F, 0.8F, this.animTimer * 1.0F / 0.25F);
            this.uiColor.a = Interpolation.fade.apply(0.0F, 1.0F, this.animTimer * 1.0F / 0.25F);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new RoomNumPanelInputProcessor());
        this.yesHb.move(860.0F * Settings.scale, Settings.OPTION_Y - 118.0F * Settings.scale);
        this.noHb.move(1062.0F * Settings.scale, Settings.OPTION_Y - 118.0F * Settings.scale);
        this.shown = true;
        this.animTimer = 0.25F;
    }

    @Override
    public void close() {
        super.close();
        this.animTimer = 0.25F;
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.shown) {
            sb.setColor(this.screenColor);
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float)Settings.WIDTH, (float)Settings.HEIGHT);
            sb.setColor(this.uiColor);
            sb.draw(ImageMaster.OPTION_CONFIRM, (float)Settings.WIDTH / 2.0F - 180.0F, Settings.OPTION_Y - 207.0F, 180.0F, 207.0F, 360.0F, 414.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 360, 414, false, false);
            sb.draw(ImageMaster.RENAME_BOX, (float)Settings.WIDTH / 2.0F - 160.0F, Settings.OPTION_Y - 160.0F, 160.0F, 160.0F, 320.0F, 320.0F, Settings.scale * 1.1F, Settings.scale * 1.1F, 0.0F, 0, 0, 320, 320, false, false);
            FontHelper.renderSmartText(sb, FontHelper.cardTitleFont, textField, (float)Settings.WIDTH / 2.0F - 120.0F * Settings.scale, Settings.OPTION_Y + 4.0F * Settings.scale, 100000.0F, 0.0F, this.uiColor, 0.82F);
            if (!isFull()) {
                float tmpAlpha = (MathUtils.cosDeg((float)(System.currentTimeMillis() / 3L % 360L)) + 1.25F) / 3.0F * this.uiColor.a;
                FontHelper.renderSmartText(sb, FontHelper.cardTitleFont, "_", (float)Settings.WIDTH / 2.0F - 122.0F * Settings.scale + FontHelper.getSmartWidth(FontHelper.cardTitleFont, textField, 1000000.0F, 0.0F, 0.82F), Settings.OPTION_Y + 4.0F * Settings.scale, 100000.0F, 0.0F, new Color(1.0F, 1.0F, 1.0F, tmpAlpha), 0.82F);
            }

            Color c = Settings.GOLD_COLOR.cpy();
            c.a = this.uiColor.a;
            FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, TEXT[1], (float)Settings.WIDTH / 2.0F, Settings.OPTION_Y + 126.0F * Settings.scale, c);
            if (this.yesHb.clickStarted) {
                sb.setColor(new Color(1.0F, 1.0F, 1.0F, this.uiColor.a * 0.9F));
                sb.draw(ImageMaster.OPTION_YES, (float)Settings.WIDTH / 2.0F - 86.5F - 100.0F * Settings.scale, Settings.OPTION_Y - 37.0F - 120.0F * Settings.scale, 86.5F, 37.0F, 173.0F, 74.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 173, 74, false, false);
                sb.setColor(new Color(this.uiColor));
            } else {
                sb.draw(ImageMaster.OPTION_YES, (float)Settings.WIDTH / 2.0F - 86.5F - 100.0F * Settings.scale, Settings.OPTION_Y - 37.0F - 120.0F * Settings.scale, 86.5F, 37.0F, 173.0F, 74.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 173, 74, false, false);
            }

            if (!this.yesHb.clickStarted && this.yesHb.hovered) {
                sb.setColor(new Color(1.0F, 1.0F, 1.0F, this.uiColor.a * 0.25F));
                sb.setBlendFunction(770, 1);
                sb.draw(ImageMaster.OPTION_YES, (float)Settings.WIDTH / 2.0F - 86.5F - 100.0F * Settings.scale, Settings.OPTION_Y - 37.0F - 120.0F * Settings.scale, 86.5F, 37.0F, 173.0F, 74.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 173, 74, false, false);
                sb.setBlendFunction(770, 771);
                sb.setColor(this.uiColor);
            }

            if (this.yesHb.clickStarted) {
                c = Color.LIGHT_GRAY.cpy();
            } else if (this.yesHb.hovered) {
                c = Settings.CREAM_COLOR.cpy();
            } else {
                c = Settings.GOLD_COLOR.cpy();
            }

            c.a = this.uiColor.a;
            FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, TEXT[2], (float)Settings.WIDTH / 2.0F - 110.0F * Settings.scale, Settings.OPTION_Y - 118.0F * Settings.scale, c, 0.82F);
            sb.draw(ImageMaster.OPTION_NO, (float)Settings.WIDTH / 2.0F - 80.5F + 106.0F * Settings.scale, Settings.OPTION_Y - 37.0F - 120.0F * Settings.scale, 80.5F, 37.0F, 161.0F, 74.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 161, 74, false, false);
            if (!this.noHb.clickStarted && this.noHb.hovered) {
                sb.setColor(new Color(1.0F, 1.0F, 1.0F, this.uiColor.a * 0.25F));
                sb.setBlendFunction(770, 1);
                sb.draw(ImageMaster.OPTION_NO, (float)Settings.WIDTH / 2.0F - 80.5F + 106.0F * Settings.scale, Settings.OPTION_Y - 37.0F - 120.0F * Settings.scale, 80.5F, 37.0F, 161.0F, 74.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 161, 74, false, false);
                sb.setBlendFunction(770, 771);
                sb.setColor(this.uiColor);
            }

            if (this.noHb.clickStarted) {
                c = Color.LIGHT_GRAY.cpy();
            } else if (this.noHb.hovered) {
                c = Settings.CREAM_COLOR.cpy();
            } else {
                c = Settings.GOLD_COLOR.cpy();
            }

            c.a = this.uiColor.a;
            FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, TEXT[3], (float)Settings.WIDTH / 2.0F + 110.0F * Settings.scale, Settings.OPTION_Y - 118.0F * Settings.scale, c, 0.82F);

            if (Settings.isControllerMode) {
                sb.draw(CInputActionSet.proceed.getKeyImg(), 770.0F * Settings.scale - 32.0F, Settings.OPTION_Y - 32.0F - 140.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
                sb.draw(CInputActionSet.cancel.getKeyImg(), 1150.0F * Settings.scale - 32.0F, Settings.OPTION_Y - 32.0F - 140.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
            }

            this.yesHb.render(sb);
            this.noHb.render(sb);
        }
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(BiliLiveDanmu.makeID("RoomNumPanel"));
        TEXT = uiStrings.TEXT;
        textField = "";
    }
}
