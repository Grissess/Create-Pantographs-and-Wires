package de.mrjulsen.paw.mixin;

import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import de.mrjulsen.paw.util.TrainExtension;
import java.util.*;
@Mixin(value = Train.class, remap = false)
public class TrainMixin implements TrainExtension {
	@Unique protected boolean pantographIsConnected = false;
	@Unique protected float pantographBoost = 5.0f;
	@Shadow public double speed;
	@Unique public boolean isPantographConnected() { return pantographIsConnected; }
	@Unique public void setPantographConnected(boolean value) { pantographIsConnected = value; }
	@Unique public float getPantographBoost() { return pantographBoost; }
	@Unique public void setPantographBoost(float value) { pantographBoost = value; }
	@Shadow public int fuelTicks;
	@Shadow public List<Carriage> carriages;


	@Inject(method = "burnFuel", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", shift = At.Shift.AFTER))
	private void electricTrain(CallbackInfo info) {
		boolean iterateFromBack = speed < 0;
		int carriageCount = carriages.size();

		if(pantographIsConnected)
		for (int index = 0; index < carriageCount; index++) {
			int i = iterateFromBack ? carriageCount - 1 - index : index;
			Carriage carriage = carriages.get(i);
			//CombinedTankWrapper fuelFluids = ((IFuelInventory) carriage.storage).railways$getFuelFluids();

			//if (fuelFluids == null) continue;

			fuelTicks = 1; //LiquidFuelTrainHandler.handleFuelDraining(fuelFluids);
		}
	}

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

    @Inject(method = "acceleration", at = @At("HEAD"), cancellable = true)
    public void acceleration(CallbackInfoReturnable<Float> cir) {
    	if(pantographIsConnected)
    		cir.setReturnValue(cir.getReturnValue() * pantographBoost * pantographBoost);
    }
}
