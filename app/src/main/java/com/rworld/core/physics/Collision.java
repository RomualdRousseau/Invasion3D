package com.rworld.core.physics;

public class Collision {

    public static boolean testSphere(PhysicalEntity e1, PhysicalEntity e2) {
        if ((e1.life <= 0.0f) || (e2.life <= 0.0f)) {
            return false;
        }
        float d = (e2.positionX - e1.positionX) * (e2.positionX - e1.positionX) + (e2.positionY - e1.positionY) * (e2.positionY - e1.positionY) + (e2.positionZ - e1.positionZ) * (e2.positionZ - e1.positionZ);
        float r = (e1.size + e2.size) * (e1.size + e2.size);
        return (d < r);
    }

    public static PhysicalEntity testSphere(PhysicalEntity entity, PhysicalEntityCollection<? extends PhysicalEntity> entities) {
        for (int i = 0, n = entities.size(); i < n; i++) {
            PhysicalEntity otherEntity = entities.get(i);
            if (Collision.testSphere(entity, otherEntity)) {
                return otherEntity;
            }
        }
        return null;
    }
}
