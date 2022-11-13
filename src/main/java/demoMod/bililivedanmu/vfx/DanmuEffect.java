package demoMod.bililivedanmu.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class DanmuEffect extends AbstractGameEffect {
    public String msg;
    public int lineNum;
    public float width = -1.0F;
    private float x;
    private float y;
    private boolean visible = false;

    public DanmuEffect(String msg, int lineNum) {
        this.msg = msg;
        this.lineNum = lineNum;
    }

    @Override
    public void update() {
        if (this.width < 0) {
            this.x = Gdx.graphics.getWidth();
            this.y = (Gdx.graphics.getHeight() / 20.0F) * this.lineNum;
            this.width = FontHelper.getWidth(FontHelper.menuBannerFont, this.msg, 1.0F);
            visible = true;
        }
        this.x -= (Gdx.graphics.getDeltaTime() + this.width / 15000.0F) * Gdx.graphics.getWidth() / 7.0F;
        if (visible && this.x + this.width < 0) {
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (visible) {
            float t = FontHelper.menuBannerFont.getScaleX();
            FontHelper.menuBannerFont.getData().setScale(1.0F);
            FontHelper.renderFontLeft(sb, FontHelper.menuBannerFont, this.msg, this.x, this.y + Gdx.graphics.getHeight() / 40.0F, Color.WHITE);
            sb.flush();
            FontHelper.menuBannerFont.getData().setScale(t);
        }
    }

    @Override
    public void dispose() {

    }
}
