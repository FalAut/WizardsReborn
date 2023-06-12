package mod.maxbogomol.wizards_reborn.common.crystal;

import mod.maxbogomol.wizards_reborn.api.crystal.PolishingType;

import java.awt.*;

public class PurePolishingType extends PolishingType {
    public PurePolishingType() {

    }

    public boolean hasParticle() {
        return true;
    }

    public Color getColor() {
        return new Color(200, 255, 245);
    }
}
