package net.blancworks.figura.mixin;

import net.blancworks.figura.Config;
import net.blancworks.figura.gui.FiguraGuiScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {

    protected LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "hasLabel", at = @At("HEAD"), cancellable = true)
    public void hasLabel(T livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (FiguraGuiScreen.showOwnNametag || ((Boolean) Config.entries.get("ownNameTag").value && livingEntity == MinecraftClient.getInstance().player && MinecraftClient.isHudEnabled()))
            cir.setReturnValue(true);
    }
}
