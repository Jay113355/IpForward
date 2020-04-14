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

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.client.C00Handshake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by Jay113355 on 4/14/2020.
 */
@Mixin(C00Handshake.class)
public class MixinC00Handshake {

	/**
	 * Changes the maxLength value passed to readString() on the ip field.
	 *
	 * This is so we can collect all the data sent by bungee/velocity
	 */
	@ModifyConstant(method = "readPacketData(Lnet/minecraft/network/PacketBuffer;)V",
			constant = @Constant(intValue = 255))
	private int onReadPacketData(int value) {
		return 65536;// 64KiB
	}

	/**
	 * Road blocks the string split call forge adds which wipes out the data from the proxy.
	 */
	@Inject(method = "readPacketData(Lnet/minecraft/network/PacketBuffer;)V", cancellable = true,
			at = @At(value = "FIELD", target = "Lnet/minecraft/network/handshake/client/C00Handshake;hasFMLMarker:Z", opcode = 181, shift = At.Shift.AFTER))
	private void onReadPacketData(PacketBuffer buf, CallbackInfo ci) {
		ci.cancel();
	}
}
