package demoMod.bililivedanmu.helpers;

import com.badlogic.gdx.InputProcessor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.ui.panels.SeedPanel;

public class RoomNumPanelInputProcessor implements InputProcessor {
    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        String charStr = String.valueOf(character);
        if (charStr.length() != 1) {
            return false;
        } else {
            if (FontHelper.getSmartWidth(FontHelper.cardTitleFont, SeedPanel.textField, 1.0E7F, 0.0F, 0.82F) >= 240.0F * Settings.scale) {
                return false;
            }

            if (Character.isDigit(character)) {
                SeedPanel.textField = SeedPanel.textField + charStr;
            }
        }
        return true;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }
}
