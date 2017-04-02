package com.silpe.vire.slip.quadtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <li>
 * nodes[0]: North East
 * </li>
 * <li>
 * nodes[1]: North West
 * </li>
 * <li>
 * nodes[2]: South West
 * </li>
 * <li>
 * nodes[3]: South East
 * </li>
 *
 * @author Jeff Niu
 */
public class QuadTree<T> {

    private static final int QT_NODE_CAPACITY = 6;
    private static final int QT_MAX_LEVEL = 7;

    private final int level;
    private final BoundingBox<?> bounds;
    private final List<BoundingBox<T>> objects;
    private final QuadTree<T>[] nodes;

    @SuppressWarnings("unchecked")
    public QuadTree(final int level, final BoundingBox bounds) {
        this.level = level;
        this.bounds = bounds;
        objects = new ArrayList<>(QT_NODE_CAPACITY);
        nodes = new QuadTree[4];
    }

    public void clear() {
        objects.clear();
        if (nodes[0] != null) {
            for (int i = 0; i < nodes.length; i++) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    public BoundingBox getBounds() {
        return bounds;
    }

    public List<Integer> insert(final BoundingBox<T> box) {
        List<Integer> index = new ArrayList<>();
        if (nodes[0] != null) {
            final boolean[] indices = getIndices(box);
            for (int i = 0; i < nodes.length; i++) {
                if (indices[i]) {
                    index.addAll(nodes[i].insert(box));
                }
            }
            return index;
        }
        objects.add(box);
        if (objects.size() > QT_NODE_CAPACITY && level < QT_MAX_LEVEL) {
            if (nodes[0] == null) {
                split();
            }
            final int size = objects.size();
            for (int i = 0; i < size; i++) {
                final BoundingBox<T> aabb = objects.get(i);
                final boolean[] indices = getIndices(aabb);
                for (int ii = 0; ii < indices.length; ii++) {
                    if (indices[ii]) {
                        index.addAll(nodes[ii].insert(aabb));
                    }
                }
            }
            objects.clear();
        }
        return index;
    }

    public Set<BoundingBox> retrieve(final Set<BoundingBox> set,
                                     final BoundingBox box) {
        final boolean[] indices = getIndices(box);
        for (int i = 0; i < indices.length; i++) {
            if (indices[i] && nodes[i] != null) {
                nodes[i].retrieve(set, box);
            }
        }
        set.addAll(objects);
        return set;
    }

    public Set<T> get(Set<T> t, List<Integer> index) {
        QuadTree<T> qt = this;
        for (int i = 0; i < index.size(); i++) {
            for (BoundingBox<T> bb : qt.objects) {
                t.add(bb.t);
            }
            qt = qt.nodes[index.get(i)];
            if (qt == null) return t;
        }
        return t;
    }

    private void split() {
        final float subWidth = bounds.getHalfWidth();
        final float subHeight = bounds.getHalfHeight();
        final float qtWidth = subWidth * 0.5f;
        final float qtHeight = subHeight * 0.5f;
        final double x = bounds.center.x;
        final double y = bounds.center.y;
        nodes[0] = new QuadTree<>(level + 1, new BoundingBox(new Vector(x + qtWidth, y - qtHeight), subWidth, subHeight));
        nodes[1] = new QuadTree<>(level + 1, new BoundingBox(new Vector(x - qtWidth, y - qtHeight), subWidth, subHeight));
        nodes[2] = new QuadTree<>(level + 1, new BoundingBox(new Vector(x - qtWidth, y + qtHeight), subWidth, subHeight));
        nodes[3] = new QuadTree<>(level + 1, new BoundingBox(new Vector(x + qtWidth, y + qtHeight), subWidth, subHeight));
    }

    private boolean[] getIndices(final BoundingBox box) {
        final boolean[] indices = new boolean[4];
        for (int i = 0; i < nodes.length; i++) {
            indices[i] = nodes[i] != null && nodes[i].getBounds().collidesWith(box);
        }
        return indices;
    }

}