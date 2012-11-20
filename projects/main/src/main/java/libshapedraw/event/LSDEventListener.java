package libshapedraw.event;

/**
 * Handle events generated by a LibShapeDraw API instance.
 */
public interface LSDEventListener {
    /** @see LSDRespawnEvent */
    public void onRespawn(LSDRespawnEvent event);

    /** @see LSDGameTickEvent */
    public void onGameTick(LSDGameTickEvent event);

    /** @see LSDPreRenderEvent */
    public void onPreRender(LSDPreRenderEvent event);
}
