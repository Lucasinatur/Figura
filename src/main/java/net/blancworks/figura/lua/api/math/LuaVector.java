package net.blancworks.figura.lua.api.math;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Iterator;

public class LuaVector extends LuaValue implements Iterable<Float> {
    public static final int TYPE = LuaValue.TVALUE;
    public static final LuaVector ORIGIN = new LuaVector();

    private final float[] values;
    private Double cachedLength = null;

    public LuaVector(float ... values) {
        if (values.length > 6) {
            throw new IllegalArgumentException("LuaVector cannot have more than 6 fields!");
        }
        this.values = values;
    }

    public static LuaVector of(Vector4f vec) {
        if (vec == null) return (LuaVector)NIL;
        return new LuaVector(vec.getX(), vec.getY(), vec.getZ(), vec.getW());
    }

    public static LuaVector of(Vector3f vec) {
        if (vec == null) return (LuaVector)NIL;
        return new LuaVector(vec.getX(), vec.getY(), vec.getZ());
    }

    public static LuaVector of(Vec3d vec) {
        if (vec == null) return (LuaVector)NIL;
        return new LuaVector((float) vec.x, (float) vec.y, (float) vec.z);
    }

    public static LuaVector of(Vec3i vec) {
        if (vec == null) return (LuaVector)NIL;
        return new LuaVector((float) vec.getX(), (float) vec.getY(), (float) vec.getZ());
    }

    public static LuaVector of(Vec2f vec) {
        if (vec == null) return (LuaVector)NIL;
        return new LuaVector(vec.x, vec.y);
    }

    public static LuaVector of(LuaTable t) {
        int n = Math.min(6, t.length());
        float[] arr = new float[n];
        for (int i = 0; i < n; i++) {
            LuaValue l =  t.get(i + 1);
            l.checknumber();
            arr[i] = l.tofloat();
        }
        return new LuaVector(arr);
    }

    public static LuaVector check(LuaValue val) {
        if (val instanceof LuaVector) {
            return (LuaVector) val;
        }
        throw new LuaError("Not a Vector table!");
    }

    public static LuaVector checkOrNew(LuaValue val) {
        if (val instanceof LuaVector) {
            return (LuaVector) val;
        } else if (val.istable()) {
            return of((LuaTable)val);
        }
        throw new LuaError("Not a Vector table!");
    }

    public Vector4f asV4f() {
        return new Vector4f(x(), y(), z(), w());
    }

    public Vector3f asV3f() {
        return new Vector3f(x(), y(), z());
    }

    public Vec3d asV3d() {
        return new Vec3d(x(), y(), z());
    }

    public Vec3i asV3iFloored() {
        return new Vec3i(Math.floor(x()), Math.floor(y()), Math.floor(z()));
    }

    public Vec3i asV3iCeiled() {
        return new Vec3i(Math.ceil(x()), Math.ceil(y()), Math.ceil(z()));
    }

    public Vec3i asV3iRounded() {
        return new Vec3i(Math.round(x()), Math.round(y()), Math.round(z()));
    }

    public Vec2f asV2f() {
        return new Vec2f(x(), y());
    }

    @Override
    public int type() {
        return TYPE;
    }

    @Override
    public String typename() {
        return "vector";
    }

    @Override
    public LuaValue add(LuaValue rhs) {
        if (rhs.isnumber()) return _add(rhs.tofloat());
        return _add(check(rhs));
    }

    @Override
    public LuaValue sub(LuaValue rhs) {
        if (rhs.isnumber()) return _sub(rhs.tofloat());
        return _sub(check(rhs));
    }

    @Override
    public LuaValue mul(LuaValue rhs) {
        if (rhs.isnumber()) return _mul(rhs.tofloat());
        return _mul(check(rhs));
    }

    @Override
    public LuaValue div(LuaValue rhs) {
        if (rhs.isnumber()) return _div(rhs.tofloat());
        return _div(check(rhs));
    }

    @Override
    public LuaValue get(int key) {
        Float f = _get(key + 1);
        if (f == null) return NIL;
        return LuaNumber.valueOf(f);
    }

    @Override
    public LuaValue get(LuaValue key) {
        return rawget(key);
    }

    @Override
    public LuaValue rawget(LuaValue key) {
        return get(key.tojstring());
    }

    @Override
    public LuaValue get(String key) {
        Float f = _get(key);
        if (f == null) return _functions(key);
        return LuaNumber.valueOf(f);
    }

    @Override
    public LuaValue tostring() {
        return LuaString.valueOf(this.toString());
    }

    public float x() { return _get(1); }
    public float y() { return _get(2); }
    public float z() { return _get(3); }
    public float w() { return _get(4); }
    public float t() { return _get(5); }
    public float h() { return _get(6); }

    public LuaVector _add(LuaVector vec) {
        int n = Math.max(_size(), vec._size());
        float[] vals = new float[n];
        for (int i = 0; i < n; i++) {
            vals[i] = _get(i + 1) + vec._get(i + 1);
        }
        return new LuaVector(vals);
    }

    public LuaVector _add(float f) {
        int n = _size();
        float[] vals = new float[n];
        for (int i = 0; i < n; i++) {
            vals[i] = _get(i + 1) + f;
        }
        return new LuaVector(vals);
    }

    public LuaVector _sub(LuaVector vec) {
        int n = Math.max(_size(), vec._size());
        float[] vals = new float[n];
        for (int i = 0; i < n; i++) {
            vals[i] = _get(i + 1) - vec._get(i + 1);
        }
        return new LuaVector(vals);
    }

    public LuaVector _sub(float f) {
        int n = _size();
        float[] vals = new float[n];
        for (int i = 0; i < n; i++) {
            vals[i] = _get(i + 1) - f;
        }
        return new LuaVector(vals);
    }

    public LuaVector _mul(LuaVector vec) {
        int n = Math.max(_size(), vec._size());
        float[] vals = new float[n];
        for (int i = 0; i < n; i++) {
            vals[i] = _get(i + 1) * vec._get(i + 1);
        }
        return new LuaVector(vals);
    }

    public LuaVector _mul(float f) {
        int n = _size();
        float[] vals = new float[n];
        for (int i = 0; i < n; i++) {
            vals[i] = _get(i + 1) * f;
        }
        return new LuaVector(vals);
    }

    public LuaVector _div(LuaVector vec) {
        int n = Math.max(_size(), vec._size());
        float[] vals = new float[n];
        for (int i = 0; i < n; i++) {
            vals[i] = _get(i + 1) / vec._get(i + 1);
        }
        return new LuaVector(vals);
    }

    public LuaVector _div(float f) {
        int n = _size();
        float[] vals = new float[n];
        for (int i = 0; i < n; i++) {
            vals[i] = _get(i + 1) / f;
        }
        return new LuaVector(vals);
    }

    public LuaValue _functions(String name) {
        switch (name) {
            case "distanceTo":
                return new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue arg1) {
                        return LuaNumber.valueOf(_distanceTo(arg1));
                    }
                };
            case "getLength":
                return new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaNumber.valueOf(_length());
                    }
                };
            case "normalized":
                return new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return _normalized();
                    }
                };
            default:
                return NIL;
        }
    }

    public double _distanceTo(LuaValue vector) {
        LuaVector vec = check(vector);
        int n = Math.max(_size(), vec._size()); // Only calculate for as many values as actually exist between both vectors
        float s = 0; // Sum value
        for (int i = 1; i <= n; i++) {
            float a = this._get(i); // This vector's value at current index
            float b = vec._get(i); // The passed vector's value at current index
            if(a == 0 && b == 0) continue; // Do not operate on values that are zero for both
            if(a == 0) {
                s += (b * b); // Only square the non zero value
                continue;
            }
            if(b == 0) {
                s += (a * a); // Only square the non zero value
                continue;
            }
            s += Math.pow(b - a, 2); // Square the difference when both values are non zero
        }
        return Math.sqrt(s); // Square root of the sum of all values
    }

    public double _length() {
        // Caches the vector's length upon first request, to conserve performance on repeated length tests on the same vector
        if (cachedLength == null) {
            int n = _size();
            float s = 0;
            for (int i = 1; i <= n; i++) {
                float v = this._get(i);
                if(v != 0) s += v * v;
            }
            cachedLength = Math.sqrt(s);
        }
        return cachedLength;
    }

    public LuaVector _normalized() {
        int n = _size();
        float s = 0;
        float[] vals = new float[n];
        for (int i = 1; i <= n; i++) {
            float v = this._get(i);
            if(v != 0) s += v * v;
        }
        float r = MathHelper.fastInverseSqrt(s);
        for (int j = 0; j < n; j++) {
            float v = this._get(j+1);
            if(v != 0) vals[j] = v * r;
        }
        return new LuaVector(vals);
    }

    public int _size() {
        return values.length;
    }

    public Float _get(int index) {
        if (index > 6 || index < 1) {
            return null;
        }
        if (index <= _size()) {
            return values[index - 1];
        }
        return 0f;
    }

    public Float _get(String name) {
        switch (name) {
            case "x":
            case "r":
            case "u":
            case "pitch":
                return x();
            case "y":
            case "g":
            case "v":
            case "yaw":
            case "volume":
                return y();
            case "z":
            case "b":
            case "roll":
                return z();
            case "w":
            case "a":
                return w();
            case "t":
                return t();
            case "h":
                return h();
            default:
                return null;
        }
    }

    @NotNull
    @Override
    public Iterator<Float> iterator() {
        return new Iter(this);
    }

    public static class Iter implements Iterator<Float> {
        private final LuaVector vector;
        private int index;

        public Iter(LuaVector vector) {
            this.vector = vector;
        }

        @Override
        public boolean hasNext() {
            return index < 6;
        }

        @Override
        public Float next() {
            Float r = vector._get(index);
            index++;
            if (r == null) throw new IllegalStateException("Iterator at invalid index!");
            return r;
        }
    }

    @Override
    public String toString() {
        return String.format("vector: x=%f, y=%f, z=%f, w=%f, t=%f, h=%f", x(), y(), z(), w(), t(), h());
    }
}