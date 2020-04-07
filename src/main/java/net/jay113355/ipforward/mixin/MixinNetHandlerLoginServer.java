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

import com.mojang.authlib.GameProfile;
import net.jay113355.ipforward.IpForwardConfig;
import net.jay113355.ipforward.ProxyData;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.login.server.SPacketDisconnect;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextComponentTranslation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.SocketAddress;

/**
 * Created by Jay113355 on 4/3/2020.
 */
@Mixin(NetHandlerLoginServer.class)
public abstract class MixinNetHandlerLoginServer implements INetHandlerLoginServer, ITickable {
	@Final
	@Shadow
	public NetworkManager networkManager;
	@Shadow
	private GameProfile loginGameProfile;
	@Shadow
	private NetHandlerLoginServer.LoginState currentLoginState;

	@Inject(method = "processLoginStart(Lnet/minecraft/network/login/client/CPacketLoginStart;)V", cancellable = true,
			at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/NetHandlerLoginServer;loginGameProfile:Lcom/mojang/authlib/GameProfile;", opcode = 181, shift = At.Shift.AFTER))
	private void onProcessLoginStart(CPacketLoginStart packetIn, CallbackInfo ci) {
		// TARGET = AFTER PUTFIELD net/minecraft/server/network/NetHandlerLoginServer.loginGameProfile : Lcom/mojang/authlib/GameProfile;
		// this.loginGameProfile = packetIn.getProfile();
		String realAddress = addressToString(this.networkManager.channel().remoteAddress());
		if (IpForwardConfig.INSTANCE.isProxyAddress(realAddress) &&
				this.networkManager.channel().attr(ProxyData.PROXY_KEY).get() != null) {
			ProxyData data = this.networkManager.channel().attr(ProxyData.PROXY_KEY).get();
			this.loginGameProfile = data.newProfile(packetIn.getProfile());
			this.currentLoginState = net.minecraft.server.network.NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
			ci.cancel();
		} else if (IpForwardConfig.INSTANCE.blockNonProxyConnections()) {
			IpForwardConfig.LOGGER.info("Disconnecting non proxy connection from: " + realAddress);
			this.networkManager.sendPacket(new SPacketDisconnect(new TextComponentTranslation("disconnect.disconnected")));
			ci.cancel();
		}
		// if (this.server.isServerInOnlineMode() && !this.networkManager.isLocalChannel())
	}

	private static String addressToString(SocketAddress address) {
		String s = address.toString();
		if (s.contains("/")) {
			s = s.substring(s.indexOf(47) + 1);
		}
		if (s.contains(":")) {
			s = s.substring(0, s.indexOf(58));
		}
		return s;
	}
}
