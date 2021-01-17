package com.github.darkfabric.injection.mixins;

import com.github.darkfabric.DarkFabric;
import com.github.darkfabric.event.impl.player.PlayerChatsEvent;
import com.github.darkfabric.event.impl.player.PlayerTickEvent;
import com.github.darkfabric.event.impl.player.PlayerWalkingEvent;
import me.zero.alpine.event.EventState;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MixinClientPlayerEntity
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 12/11/2019 - 07:04 pm
 */
@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void tick(CallbackInfo ci) {
        DarkFabric.getInstance().getEventBus().post(new PlayerTickEvent());
    }

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void onUpdateWalkingPlayerPre(CallbackInfo callbackInfoReturnable) {
        PlayerWalkingEvent eventPlayerWalking = new PlayerWalkingEvent(EventState.PRE,
                ((ClientPlayerEntity) (Object) this));
        DarkFabric.getInstance().getEventBus().post(eventPlayerWalking);
        if (eventPlayerWalking.isCancelled())
            callbackInfoReturnable.cancel();
    }

    @Inject(method = "tickMovement", at = @At("RETURN"), cancellable = true)
    private void onUpdateWalkingPlayerPost(CallbackInfo callbackInfoReturnable) {
        PlayerWalkingEvent eventPlayerWalking = new PlayerWalkingEvent(EventState.POST, ((ClientPlayerEntity) (Object) this));
        DarkFabric.getInstance().getEventBus().post(eventPlayerWalking);
        if (eventPlayerWalking.isCancelled())
            callbackInfoReturnable.cancel();
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void sendChatMessage(String message, CallbackInfo callbackInfo) {
        PlayerChatsEvent eventSendChatMessage = new PlayerChatsEvent(message);
        DarkFabric.getInstance().getEventBus().post(eventSendChatMessage);
        if (eventSendChatMessage.isCancelled())
            callbackInfo.cancel();
    }

}
