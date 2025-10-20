package com.example.math;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * Math utilities and wrapper for jME3 math primitives
 * Provides convenient access to Vec3, Quat, Mat4, and AABB
 */
public class GameMath {
    
    // Common constants
    public static final float PI = (float) Math.PI;
    public static final float TWO_PI = 2.0f * PI;
    public static final float HALF_PI = PI * 0.5f;
    public static final float DEG_TO_RAD = PI / 180.0f;
    public static final float RAD_TO_DEG = 180.0f / PI;
    public static final float EPSILON = 1e-6f;
    
    /**
     * Vec3 - 3D Vector wrapper around jME3's Vector3f
     */
    public static class Vec3 {
        public static final Vec3 ZERO = new Vec3(0, 0, 0);
        public static final Vec3 ONE = new Vec3(1, 1, 1);
        public static final Vec3 UNIT_X = new Vec3(1, 0, 0);
        public static final Vec3 UNIT_Y = new Vec3(0, 1, 0);
        public static final Vec3 UNIT_Z = new Vec3(0, 0, 1);
        public static final Vec3 UP = UNIT_Y;
        public static final Vec3 DOWN = new Vec3(0, -1, 0);
        public static final Vec3 LEFT = new Vec3(-1, 0, 0);
        public static final Vec3 RIGHT = UNIT_X;
        public static final Vec3 FORWARD = new Vec3(0, 0, -1);
        public static final Vec3 BACK = UNIT_Z;
        
        private Vector3f vec;
        
        public Vec3() {
            this.vec = new Vector3f();
        }
        
        public Vec3(float x, float y, float z) {
            this.vec = new Vector3f(x, y, z);
        }
        
        public Vec3(Vector3f vector) {
            this.vec = vector.clone();
        }
        
        public Vec3(Vec3 other) {
            this.vec = other.vec.clone();
        }
        
        // Getters/Setters
        public float x() { return vec.x; }
        public float y() { return vec.y; }
        public float z() { return vec.z; }
        
        public Vec3 setX(float x) { vec.x = x; return this; }
        public Vec3 setY(float y) { vec.y = y; return this; }
        public Vec3 setZ(float z) { vec.z = z; return this; }
        public Vec3 set(float x, float y, float z) { vec.set(x, y, z); return this; }
        public Vec3 set(Vec3 other) { vec.set(other.vec); return this; }
        
        // Operations
        public Vec3 add(Vec3 other) { return new Vec3(vec.add(other.vec)); }
        public Vec3 subtract(Vec3 other) { return new Vec3(vec.subtract(other.vec)); }
        public Vec3 multiply(float scalar) { return new Vec3(vec.mult(scalar)); }
        public Vec3 multiply(Vec3 other) { return new Vec3(vec.mult(other.vec)); }
        public Vec3 divide(float scalar) { return new Vec3(vec.divide(scalar)); }
        public Vec3 negate() { return new Vec3(vec.negate()); }
        
        // In-place operations
        public Vec3 addLocal(Vec3 other) { vec.addLocal(other.vec); return this; }
        public Vec3 subtractLocal(Vec3 other) { vec.subtractLocal(other.vec); return this; }
        public Vec3 multiplyLocal(float scalar) { vec.multLocal(scalar); return this; }
        public Vec3 multiplyLocal(Vec3 other) { vec.multLocal(other.vec); return this; }
        public Vec3 divideLocal(float scalar) { vec.divideLocal(scalar); return this; }
        public Vec3 negateLocal() { vec.negateLocal(); return this; }
        
        // Vector operations
        public float dot(Vec3 other) { return vec.dot(other.vec); }
        public Vec3 cross(Vec3 other) { return new Vec3(vec.cross(other.vec)); }
        public Vec3 crossLocal(Vec3 other) { vec.crossLocal(other.vec); return this; }
        
        public float length() { return vec.length(); }
        public float lengthSquared() { return vec.lengthSquared(); }
        public float distance(Vec3 other) { return vec.distance(other.vec); }
        public float distanceSquared(Vec3 other) { return vec.distanceSquared(other.vec); }
        
        public Vec3 normalize() { return new Vec3(vec.normalize()); }
        public Vec3 normalizeLocal() { vec.normalizeLocal(); return this; }
        
        public Vec3 lerp(Vec3 target, float t) { 
            return new Vec3(vec.interpolateLocal(target.vec, t)); 
        }
        
        // Utility
        public Vector3f toJME() { return vec.clone(); }
        public Vec3 clone() { return new Vec3(this); }
        
        @Override
        public String toString() {
            return String.format("Vec3(%.3f, %.3f, %.3f)", vec.x, vec.y, vec.z);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Vec3)) return false;
            Vec3 other = (Vec3) obj;
            return vec.equals(other.vec);
        }
        
        @Override
        public int hashCode() {
            return vec.hashCode();
        }
    }
    
    /**
     * Quat - Quaternion wrapper around jME3's Quaternion
     */
    public static class Quat {
        public static final Quat IDENTITY = new Quat(0, 0, 0, 1);
        
        private Quaternion quat;
        
        public Quat() {
            this.quat = new Quaternion();
        }
        
        public Quat(float x, float y, float z, float w) {
            this.quat = new Quaternion(x, y, z, w);
        }
        
        public Quat(Quaternion quaternion) {
            this.quat = quaternion.clone();
        }
        
        public Quat(Quat other) {
            this.quat = other.quat.clone();
        }
        
        // Factory methods
        public static Quat fromAngles(float x, float y, float z) {
            return new Quat(new Quaternion().fromAngles(x, y, z));
        }
        
        public static Quat fromAxisAngle(Vec3 axis, float angle) {
            return new Quat(new Quaternion().fromAngleAxis(angle, axis.toJME()));
        }
        
        public static Quat lookAt(Vec3 direction, Vec3 up) {
            return new Quat(new Quaternion().lookAt(direction.toJME(), up.toJME()));
        }
        
        // Getters/Setters
        public float x() { return quat.getX(); }
        public float y() { return quat.getY(); }
        public float z() { return quat.getZ(); }
        public float w() { return quat.getW(); }
        
        public Quat set(float x, float y, float z, float w) { quat.set(x, y, z, w); return this; }
        public Quat set(Quat other) { quat.set(other.quat); return this; }
        
        // Operations
        public Quat multiply(Quat other) { return new Quat(quat.mult(other.quat)); }
        public Quat multiplyLocal(Quat other) { quat.multLocal(other.quat); return this; }
        
        public Vec3 multiply(Vec3 vector) { return new Vec3(quat.mult(vector.toJME())); }
        
        public Quat inverse() { return new Quat(quat.inverse()); }
        public Quat inverseLocal() { quat.inverseLocal(); return this; }
        
        public Quat normalize() { 
            Quaternion normalized = quat.clone();
            normalized.normalizeLocal();
            return new Quat(normalized); 
        }
        public Quat normalizeLocal() { quat.normalizeLocal(); return this; }
        
        public Quat slerp(Quat target, float t) {
            Quaternion result = quat.clone();
            result.slerp(target.quat, t);
            return new Quat(result);
        }
        
        // Conversion
        public Vec3 toEulerAngles() {
            float[] angles = quat.toAngles(null);
            return new Vec3(angles[0], angles[1], angles[2]);
        }
        
        public Quaternion toJME() { return quat.clone(); }
        public Quat clone() { return new Quat(this); }
        
        @Override
        public String toString() {
            return String.format("Quat(%.3f, %.3f, %.3f, %.3f)", quat.getX(), quat.getY(), quat.getZ(), quat.getW());
        }
    }
    
    /**
     * Mat4 - 4x4 Matrix wrapper around jME3's Matrix4f
     */
    public static class Mat4 {
        public static final Mat4 IDENTITY = new Mat4();
        
        private Matrix4f mat;
        
        public Mat4() {
            this.mat = new Matrix4f();
        }
        
        public Mat4(Matrix4f matrix) {
            this.mat = matrix.clone();
        }
        
        public Mat4(Mat4 other) {
            this.mat = other.mat.clone();
        }
        
        // Factory methods
        public static Mat4 translation(Vec3 translation) {
            Mat4 result = new Mat4();
            result.mat.setTranslation(translation.toJME());
            return result;
        }
        
        public static Mat4 rotation(Quat rotation) {
            Mat4 result = new Mat4();
            result.mat.setRotationQuaternion(rotation.toJME());
            return result;
        }
        
        public static Mat4 scale(Vec3 scale) {
            Mat4 result = new Mat4();
            result.mat.setScale(scale.toJME());
            return result;
        }
        
        // Operations
        public Mat4 multiply(Mat4 other) { return new Mat4(mat.mult(other.mat)); }
        public Mat4 multiplyLocal(Mat4 other) { mat.multLocal(other.mat); return this; }
        
        public Vec3 multiply(Vec3 vector) {
            Vector3f result = new Vector3f();
            mat.mult(vector.toJME(), result);
            return new Vec3(result);
        }
        
        public Mat4 transpose() { return new Mat4(mat.transpose()); }
        public Mat4 transposeLocal() { mat.transposeLocal(); return this; }
        
        public Mat4 invert() { return new Mat4(mat.invert()); }
        public Mat4 invertLocal() { mat.invertLocal(); return this; }
        
        // Getters
        public Vec3 getTranslation() { return new Vec3(mat.toTranslationVector()); }
        public Quat getRotation() { return new Quat(mat.toRotationQuat()); }
        public Vec3 getScale() { return new Vec3(mat.toScaleVector()); }
        
        public Matrix4f toJME() { return mat.clone(); }
        public Mat4 clone() { return new Mat4(this); }
        
        @Override
        public String toString() {
            return "Mat4[\n" + mat.toString() + "]";
        }
    }
    
    /**
     * AABB - Axis-Aligned Bounding Box wrapper around jME3's BoundingBox
     */
    public static class AABB {
        private BoundingBox box;
        
        public AABB() {
            this.box = new BoundingBox();
        }
        
        public AABB(Vec3 min, Vec3 max) {
            this.box = new BoundingBox(min.toJME(), max.toJME());
        }
        
        public AABB(Vec3 center, float xExtent, float yExtent, float zExtent) {
            this.box = new BoundingBox(center.toJME(), xExtent, yExtent, zExtent);
        }
        
        public AABB(BoundingBox boundingBox) {
            this.box = (BoundingBox) boundingBox.clone();
        }
        
        public AABB(AABB other) {
            this.box = (BoundingBox) other.box.clone();
        }
        
        // Getters
        public Vec3 getMin() { 
            Vector3f center = box.getCenter();
            return new Vec3(center.x - box.getXExtent(), 
                           center.y - box.getYExtent(), 
                           center.z - box.getZExtent()); 
        }
        public Vec3 getMax() { 
            Vector3f center = box.getCenter();
            return new Vec3(center.x + box.getXExtent(), 
                           center.y + box.getYExtent(), 
                           center.z + box.getZExtent()); 
        }
        public Vec3 getCenter() { return new Vec3(box.getCenter()); }
        public Vec3 getExtent() { 
            return new Vec3(box.getXExtent(), box.getYExtent(), box.getZExtent()); 
        }
        
        public float getXExtent() { return box.getXExtent(); }
        public float getYExtent() { return box.getYExtent(); }
        public float getZExtent() { return box.getZExtent(); }
        
        public float getVolume() { return box.getVolume(); }
        
        // Operations
        public boolean contains(Vec3 point) {
            return box.contains(point.toJME());
        }
        
        public boolean intersects(AABB other) {
            return box.intersects(other.box);
        }
        
        public float distanceTo(Vec3 point) {
            return box.distanceTo(point.toJME());
        }
        
        public AABB merge(AABB other) {
            Vec3 thisMin = getMin();
            Vec3 thisMax = getMax();
            Vec3 otherMin = other.getMin();
            Vec3 otherMax = other.getMax();
            
            Vec3 newMin = new Vec3(
                Math.min(thisMin.x(), otherMin.x()),
                Math.min(thisMin.y(), otherMin.y()),
                Math.min(thisMin.z(), otherMin.z())
            );
            Vec3 newMax = new Vec3(
                Math.max(thisMax.x(), otherMax.x()),
                Math.max(thisMax.y(), otherMax.y()),
                Math.max(thisMax.z(), otherMax.z())
            );
            
            return new AABB(newMin, newMax);
        }
        
        public AABB mergeLocal(AABB other) {
            box.mergeLocal(other.box);
            return this;
        }
        
        public BoundingBox toJME() { return (BoundingBox) box.clone(); }
        public AABB clone() { return new AABB(this); }
        
        @Override
        public String toString() {
            return String.format("AABB(min=%s, max=%s)", getMin(), getMax());
        }
    }
    
    // Utility functions
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
    
    public static float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }
    
    public static float smoothstep(float edge0, float edge1, float x) {
        float t = clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        return t * t * (3.0f - 2.0f * t);
    }
    
    public static float toDegrees(float radians) {
        return radians * RAD_TO_DEG;
    }
    
    public static float toRadians(float degrees) {
        return degrees * DEG_TO_RAD;
    }
    
    public static boolean approximately(float a, float b) {
        return Math.abs(a - b) < EPSILON;
    }
}