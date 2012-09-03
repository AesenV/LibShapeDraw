package libshapedraw.shape;

import java.util.Collection;

import libshapedraw.primitive.ReadonlyVector3;

/**
 * A series of connected line segments that smoothly blends from one line style
 * to another along the segments.
 */
public class WireframeLinesBlend extends WireframeLinesBlendIterable {
    private Collection<ReadonlyVector3> pointsCollection;

    /**
     * Require a Collection rather than just an Iterable because we need to
     * know the size before iterating for blending.
     */
    public WireframeLinesBlend(Collection<ReadonlyVector3> points) {
        super(points);
        pointsCollection = points;
    }

    /**
     * The "blend endpoint" refers to the last line to be rendered, which
     * should be 100% getBlendToLineStyle().
     */
    @Override
    protected int getBlendEndpoint() {
        if (getRenderCap() < 0) {
            return pointsCollection.size() - 2;
        } else {
            return super.getBlendEndpoint();
        }
    }
}
