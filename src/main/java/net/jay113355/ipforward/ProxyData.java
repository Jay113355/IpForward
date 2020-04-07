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

package net.jay113355.ipforward;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import io.netty.util.AttributeKey;

import java.net.SocketAddress;
import java.util.UUID;

/**
 * Created by Jay113355 on 11/8/2019.
 */
public class ProxyData {
	public static final AttributeKey<ProxyData> PROXY_KEY = AttributeKey.valueOf("ipforward:proxyData");
	private static final Gson GSON = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();

	private final SocketAddress remoteAddress;
	private final UUID uniqueId;
	private final PropertyMap properties;

	public ProxyData(SocketAddress remoteAddress, UUID uniqueId, String properties) {
		this.remoteAddress = remoteAddress;
		this.uniqueId = uniqueId;
		this.properties = GSON.fromJson(properties, PropertyMap.class);
	}

	public SocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	public UUID getUniqueID() {
		return uniqueId;
	}

	public PropertyMap getProperties() {
		return properties;
	}

	public boolean hasFMLMarker() {
		return properties.containsKey("forgeClient") && properties.get("forgeClient")
				.stream().findFirst().get().getValue().equalsIgnoreCase("true");
	}

	public GameProfile newProfile(GameProfile gameProfile) {
		GameProfile profile = new GameProfile(this.uniqueId, gameProfile.getName());
		profile.getProperties().putAll(this.properties);
		return profile;
	}

	@Override
	public String toString() {
		return "ProxyData{" +
				"remoteAddress=" + remoteAddress +
				", userID=" + uniqueId +
				", properties=" + properties +
				'}';
	}
}
