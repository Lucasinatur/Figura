package net.blancworks.figura.lua.api.actionWheel;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.gui.ActionWheel;
import net.blancworks.figura.lua.CustomScript;
import net.blancworks.figura.lua.api.ReadOnlyLuaTable;
import net.blancworks.figura.lua.api.ScriptLocalAPITable;
import net.blancworks.figura.lua.api.item.ItemStackAPI;
import net.blancworks.figura.lua.api.math.LuaVector;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class ActionWheelAPI {
    public static final String SLOT_1 = "SLOT_1";
    public static final String SLOT_2 = "SLOT_2";
    public static final String SLOT_3 = "SLOT_3";
    public static final String SLOT_4 = "SLOT_4";
    public static final String SLOT_5 = "SLOT_5";
    public static final String SLOT_6 = "SLOT_6";
    public static final String SLOT_7 = "SLOT_7";
    public static final String SLOT_8 = "SLOT_8";

    public static Identifier getID() {
        return new Identifier("default", "action_wheel");
    }

    public static ReadOnlyLuaTable getForScript(CustomScript script) {
        return new ScriptLocalAPITable(script, new LuaTable() {{
            set(SLOT_1, getTableForPart(SLOT_1, script));
            set(SLOT_2, getTableForPart(SLOT_2, script));
            set(SLOT_3, getTableForPart(SLOT_3, script));
            set(SLOT_4, getTableForPart(SLOT_4, script));
            set(SLOT_5, getTableForPart(SLOT_5, script));
            set(SLOT_6, getTableForPart(SLOT_6, script));
            set(SLOT_7, getTableForPart(SLOT_7, script));
            set(SLOT_8, getTableForPart(SLOT_8, script));

            set("setLeftSize", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue arg) {
                    int size = arg.checkint();
                    script.actionWheelLeftSize = MathHelper.clamp(size, 1, 4);
                    return NIL;
                }
            });

            set("setRightSize", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue arg) {
                    int size = arg.checkint();
                    script.actionWheelRightSize = MathHelper.clamp(size, 1, 4);
                    return NIL;
                }
            });

            set("getLeftSize", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(script.actionWheelLeftSize);
                }
            });

            set("getRightSize", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(script.actionWheelRightSize);
                }
            });

            set("getSelectedSlot", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    int selected = ActionWheel.selectedSlot;
                    return selected == -1 ? NIL : LuaValue.valueOf(selected + 1);
                }
            });

            set("isOpen", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(FiguraMod.actionWheel.isPressed() && ActionWheel.enabled);
                }
            });
        }});
    }

    public static ReadOnlyLuaTable getTableForPart(String accessor, CustomScript script) {
        return new ActionWheelTable(accessor, script);
    }

    private static class ActionWheelTable extends ScriptLocalAPITable {
        String accessor;

        public ActionWheelTable(String accessor, CustomScript script) {
            super(script);
            this.accessor = accessor;
            super.setTable(getTable());
        }

        public LuaTable getTable() {
            LuaTable ret = new LuaTable();
            ret.set("getFunction", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return targetScript.getOrMakeActionWheelCustomization(accessor).function;
                }
            });

            ret.set("setFunction", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue arg1) {
                    targetScript.getOrMakeActionWheelCustomization(accessor).function = arg1.isnil() ? null : arg1.checkfunction();
                    return NIL;
                }
            });

            ret.set("getItem", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return ItemStackAPI.getTable(targetScript.getOrMakeActionWheelCustomization(accessor).item);
                }
            });

            ret.set("setItem", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue arg1) {
                    if (arg1.isnil()) {
                        targetScript.getOrMakeActionWheelCustomization(accessor).item = null;
                        return NIL;
                    }

                    ItemStack item = (ItemStack) arg1.get("stack").touserdata(ItemStack.class);
                    if (item == null)
                        throw new LuaError("Not a ItemStack table!");

                    targetScript.getOrMakeActionWheelCustomization(accessor).item = item;
                    return NIL;
                }
            });

            ret.set("getHoverItem", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return ItemStackAPI.getTable(targetScript.getOrMakeActionWheelCustomization(accessor).hoverItem);
                }
            });

            ret.set("setHoverItem", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue arg) {
                    if (arg.isnil()) {
                        targetScript.getOrMakeActionWheelCustomization(accessor).hoverItem = null;
                        return NIL;
                    }

                    ItemStack hoverItem = (ItemStack) arg.get("stack").touserdata(ItemStack.class);
                    if (hoverItem == null)
                        throw new LuaError("Not a ItemStack table!");

                    targetScript.getOrMakeActionWheelCustomization(accessor).hoverItem = hoverItem;
                    return NIL;
                }
            });

            ret.set("getColor", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaVector.of(targetScript.getOrMakeActionWheelCustomization(accessor).color);
                }
            });

            ret.set("setColor", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue arg) {
                    targetScript.getOrMakeActionWheelCustomization(accessor).color = arg.isnil() ? null : LuaVector.checkOrNew(arg).asV3f();
                    return NIL;
                }
            });

            ret.set("getHoverColor", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaVector.of(targetScript.getOrMakeActionWheelCustomization(accessor).hoverColor);
                }
            });

            ret.set("setHoverColor", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue arg) {
                    targetScript.getOrMakeActionWheelCustomization(accessor).hoverColor = arg.isnil() ? null : LuaVector.checkOrNew(arg).asV3f();
                    return NIL;
                }
            });

            ret.set("getTitle", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(targetScript.getOrMakeActionWheelCustomization(accessor).title);
                }
            });

            ret.set("setTitle", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue arg1) {
                    targetScript.getOrMakeActionWheelCustomization(accessor).title = arg1.isnil() ? null : arg1.checkjstring();
                    return NIL;
                }
            });

            ret.set("getTexture", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaString.valueOf(targetScript.getOrMakeActionWheelCustomization(accessor).texture.toString());
                }
            });

            ret.set("setTexture", new TwoArgFunction() {
                @Override
                public LuaValue call(LuaValue arg1, LuaValue arg2) {
                    ActionWheelCustomization cust = targetScript.getOrMakeActionWheelCustomization(accessor);
                    try {
                        cust.texture = ActionWheelCustomization.TextureType.valueOf(arg1.checkjstring());

                        if (cust.texture == ActionWheelCustomization.TextureType.Resource)
                            cust.texturePath = new Identifier(arg2.checkjstring());
                    } catch (Exception ignored) {
                        cust.texture = ActionWheelCustomization.TextureType.None;
                    }

                    return NIL;
                }
            });

            ret.set("getTextureScale", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaVector.of(targetScript.getOrMakeActionWheelCustomization(accessor).textureScale);
                }
            });

            ret.set("setTextureScale", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue arg1) {
                    targetScript.getOrMakeActionWheelCustomization(accessor).textureScale = LuaVector.checkOrNew(arg1).asV2f();
                    return NIL;
                }
            });

            ret.set("getUV", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    ActionWheelCustomization cust = targetScript.getOrMakeActionWheelCustomization(accessor);

                    Vec2f offset = cust.uvOffset;
                    Vec2f size = cust.uvSize;
                    Vec2f tex = cust.textureSize;

                    if (offset == null)
                        offset = new Vec2f(0f, 0f);

                    if (size == null)
                        size = new Vec2f(0f, 0f);

                    if (tex == null)
                        tex = new Vec2f(0f, 0f);

                    return new LuaVector(offset.x, offset.y, size.x, size.y, tex.x, tex.y);
                }
            });

            ret.set("setUV", new ThreeArgFunction() {
                @Override
                public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
                    ActionWheelCustomization cust = targetScript.getOrMakeActionWheelCustomization(accessor);

                    LuaVector offset = LuaVector.checkOrNew(arg1);
                    LuaVector size = LuaVector.checkOrNew(arg2);

                    cust.uvOffset = offset.asV2f();
                    cust.uvSize = size.asV2f();

                    if (!arg3.isnil()) {
                        LuaVector tex = LuaVector.checkOrNew(arg3);
                        cust.textureSize = tex.asV2f();
                    }

                    return NIL;
                }
            });

            ret.set("clear", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    targetScript.actionWheelCustomizations.put(accessor, new ActionWheelCustomization());
                    return NIL;
                }
            });

            return ret;
        }
    }
}
