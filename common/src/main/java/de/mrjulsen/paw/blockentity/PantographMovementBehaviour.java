package de.mrjulsen.paw.blockentity;

import org.joml.Vector3d;

import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;
import de.mrjulsen.mcdragonlib.DragonLib;
import de.mrjulsen.paw.network.PacketSetPantoState;
import de.mrjulsen.paw.util.TrainExtension;

public class PantographMovementBehaviour implements MovementBehaviour {

	@Override
	public void tick(MovementContext context) {       
        if (context.contraption.presentBlockEntities.containsKey(context.localPos) &&
            context.contraption.presentBlockEntities.get(context.localPos) instanceof PantographBlockEntity be
        ) {
        	if(context.contraption.entity.level().isClientSide()) {
        		boolean oldIsExpanded = be.isExpanded();
            	Direction dir = context.state.getValue(HorizontalDirectionalBlock.FACING);
            	if (dir.getAxis() == Axis.X) {
                	dir = dir.getOpposite();
            	}
            	final double yRot = dir.toYRot();
            	be.updateContraptionValues(new Vector3d(context.position.x(), context.position.y() - 0.5D + PantographBlockEntity.MIN_HEIGHT, context.position.z()), (v) -> {
                	Vec3 r = VecHelper.rotate(new Vec3(v.x(), v.y(), v.z()), yRot, Axis.Y);
                	r = context.rotation.apply(r);
                	return new Vector3d(r.x(), r.y(), r.z());
            	});
            	be.contraptionTick();
				if(context.contraption.entity instanceof CarriageContraptionEntity cce &&
						oldIsExpanded != be.isExpanded()) {
					Train train = cce.getCarriage().train;
					PacketSetPantoState pkt = new PacketSetPantoState(train.id, be.isExpanded());
					DragonLib.getDragonLibNetworkManager().CHANNEL.sendToServer(pkt);
				}
            }
        }
	}

    @Override
    public boolean renderAsNormalBlockEntity() {
        return true;
    }
}
