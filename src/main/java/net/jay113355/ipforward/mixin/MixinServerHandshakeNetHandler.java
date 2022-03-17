/*
 * IpForward
 * Copyright (C) 2020 Jay113355
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.jay113355.ipforward.mixin;

import com.mojang.util.UUIDTypeAdapter;
import net.jay113355.ipforward.ProxyData;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.ServerHandshakeNetHandler;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * Created by Jay113355 on 4/3/2020.
 */
@Mixin(ServerHandshakeNetHandler.class)
public abstract class MixinServerHandshakeNetHandler {

	@Shadow
	@Final
	private NetworkManager connection;

	@Inject(method = "handleIntention(Lnet/minecraft/network/handshake/client/CHandshakePacket;)V",
			at = @At(value = "HEAD"))
	private void onHandleServerHandshake(CHandshakePacket packet, CallbackInfo ci) {
		String[] elements = packet.hostName.split("\0");
		if (elements.length == 4) {
			String ip = elements[1]; // The actual user IP address
			String uuid = elements[2]; // The connecting user UUID (no hyphens!)
			String gameProfileProp = elements[3]; // Game profile properties (texture usually)
			ProxyData data = new ProxyData(new InetSocketAddress(ip, 0), UUIDTypeAdapter.fromString(uuid), gameProfileProp);

			boolean hasFML = data.hasFMLMarker() || Objects.equals(packet.getFMLVersion(), FMLNetworkConstants.NOVERSION);
			//manager.channel().attr(NetworkRegistry.FML_MARKER).set(hasFML);
			connection.channel().attr(ProxyData.PROXY_KEY).set(data);
			connection.address = data.getRemoteAddress();
			if (hasFML) {
				ObfuscationReflectionHelper.setPrivateValue(CHandshakePacket.class, packet, FMLNetworkConstants.NETVERSION, "fmlVersion");
			}
		}
	}
}
