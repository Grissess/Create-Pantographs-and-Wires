package de.mrjulsen.paw.network;

import java.util.UUID;
import java.util.function.Supplier;

import com.simibubi.create.Create;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.trains.entity.Train;

import de.mrjulsen.mcdragonlib.DragonLib;
import de.mrjulsen.mcdragonlib.net.IPacketBase;
import de.mrjulsen.paw.util.TrainExtension;
import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PacketSetPantoState implements IPacketBase<PacketSetPantoState>{
	public final UUID trainUuid;
	public final boolean pantoState;
	
	static {
		PacketSetPantoState example = new PacketSetPantoState();
		DragonLib.getDragonLibNetworkManager().CHANNEL.register(
				PacketSetPantoState.class, 
				example::encode, 
				example::decode, 
				example::handle
		);
	}
	
	protected PacketSetPantoState() {
		trainUuid = null;
		pantoState = false;
	}
	
	public PacketSetPantoState(UUID trainUuid, boolean pantoState) {
		this.trainUuid = trainUuid;
		this.pantoState = pantoState;
	}

	@Override
	public PacketSetPantoState decode(FriendlyByteBuf buf) {
		return new PacketSetPantoState(buf.readUUID(), buf.readBoolean());
	}

	@Override
	public void encode(PacketSetPantoState pkt, FriendlyByteBuf buf) {
		buf.writeUUID(pkt.trainUuid);
		buf.writeBoolean(pkt.pantoState);
	}

	@Override
	public void handle(PacketSetPantoState pkt, Supplier<PacketContext> ctx) {
		ctx.get().queue(() -> {
			Player ply = ctx.get().getPlayer();
			Level level = ply != null ? ply.level() : null;
			Train t = Create.RAILWAYS.sided(level).trains.get(pkt.trainUuid);
			if(t == null) return;
			TrainExtension te = (TrainExtension)t;
			te.setPantographConnected(pkt.pantoState);
			if(ply != null) {
				DragonLib.getDragonLibNetworkManager().CHANNEL.sendToPlayers(
						ctx.get().getPlayer().getServer().getPlayerList().getPlayers(),
						pkt
				);
			}
		});
	}
}
