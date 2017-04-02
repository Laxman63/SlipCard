package com.silpe.vire.slip.quadtree;


class BoundingBox<T> {

    public static boolean collide(final BoundingBox box1, final BoundingBox box2) {
        final boolean xOverlap = Math.abs(box1.center.x - box2.center.x) <= box1.r[0] + box2.r[0];
        final boolean yOverlap = Math.abs(box1.center.y - box2.center.y) <= box1.r[1] + box2.r[1];
        return xOverlap && yOverlap;
    }

    public Vector center;
    public float[] r;
    public T t;

    public BoundingBox(Vector center, float width, float height) {
        this.center.x = center.x;
        this.center.y = center.y;
        r = new float[2];
        r[0] = width * 0.5f;
        r[1] = height * 0.5f;
    }

    public boolean collidesWith(BoundingBox box) {
        return collide(this, box);
    }

    public float getHalfWidth() {
        return r[0];
    }

    public float getHalfHeight() {
        return r[1];
    }

    public void put(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

}
