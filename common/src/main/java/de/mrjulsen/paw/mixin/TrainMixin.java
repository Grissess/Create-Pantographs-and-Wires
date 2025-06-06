package de.mrjulsen.paw.mixin;

import com.simibubi.create.content.trains.entity.Train;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import de.mrjulsen.paw.util.TrainExtension;

@Mixin(value = Train.class, remap = false)
public class TrainMixin implements TrainExtension {
	@Unique protected boolean pantographIsConnected = false;
	@Unique protected float pantographBoost = 5.0f;

	@Unique public boolean isPantographConnected() { return pantographIsConnected; }
	@Unique public void setPantographConnected(boolean value) { pantographIsConnected = value; }
	@Unique public float getPantographBoost() { return pantographBoost; }
	@Unique public void setPantographBoost(float value) { pantographBoost = value; }

    @Inject(method = "maxSpeed", at = @At("RETURN"), cancellable = true)
    public void maxSpeed(CallbackInfoReturnable<Float> cir) {
    	if(pantographIsConnected)
    		cir.setReturnValue(cir.getReturnValue() * pantographBoost);
    }

    @Inject(method = "maxTurnSpeed", at = @At("RETURN"), cancellable = true)
    public void maxTurnSpeed(CallbackInfoReturnable<Float> cir) {
    	if(pantographIsConnected)
    		cir.setReturnValue(cir.getReturnValue() * pantographBoost);
    }

    @Inject(method = "acceleration", at = @At("RETURN"), cancellable = true)
    public void acceleration(CallbackInfoReturnable<Float> cir) {
    	if(pantographIsConnected)
    		cir.setReturnValue(cir.getReturnValue() * pantographBoost * pantographBoost);
    }
}
