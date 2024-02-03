package tweakeroo.util;

import com.google.common.collect.ImmutableList;

import malilib.config.value.BaseOptionListConfigValue;
import malilib.util.position.BlockPos;
import malilib.util.position.Direction;
import tweakeroo.tweaks.PlacementTweaks;

public class PlacementRestrictionMode extends BaseOptionListConfigValue
{
    public static final PlacementRestrictionMode FACE     = new PlacementRestrictionMode("face",     "tweakeroo.label.placement_restriction_mode.face", PlacementTweaks::isNewPositionValidForFaceMode);
    public static final PlacementRestrictionMode LAYER    = new PlacementRestrictionMode("layer",    "tweakeroo.label.placement_restriction_mode.layer", PlacementTweaks::isNewPositionValidForLayerMode);
    public static final PlacementRestrictionMode DIAGONAL = new PlacementRestrictionMode("diagonal", "tweakeroo.label.placement_restriction_mode.diagonal", PlacementTweaks::isNewPositionValidForDiagonalMode);
    public static final PlacementRestrictionMode LINE     = new PlacementRestrictionMode("line",     "tweakeroo.label.placement_restriction_mode.line", PlacementTweaks::isNewPositionValidForLineMode);
    public static final PlacementRestrictionMode COLUMN   = new PlacementRestrictionMode("column",   "tweakeroo.label.placement_restriction_mode.column", PlacementTweaks::isNewPositionValidForColumnMode);
    public static final PlacementRestrictionMode PLANE    = new PlacementRestrictionMode("plane",    "tweakeroo.label.placement_restriction_mode.plane", PlacementTweaks::isNewPositionValidForPlaneMode);

    public static final ImmutableList<PlacementRestrictionMode> VALUES = ImmutableList.of(FACE, LAYER, DIAGONAL, LINE, COLUMN, PLANE);

    private final PlacementTweaks.PlacementRestrictionCheck check;

    private PlacementRestrictionMode(String name, String translationKey, PlacementTweaks.PlacementRestrictionCheck check)
    {
        super(name, translationKey);

        this.check = check;
    }

    public boolean isPositionValid(BlockPos posNew, Direction side, BlockPos posFirst, Direction sideFirst)
    {
        return this.check.isPositionValid(posNew, side, posFirst, sideFirst);
    }
}
