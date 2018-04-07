package fi.dy.masa.tweakeroo.config;

public interface IFeatureCallback
{
    /**
     * Called when (= after) the feature's value is changed.
     * @param feature
     */
    void onValueChange(FeatureToggle feature);
}
