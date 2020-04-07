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

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class IpForwardConfig {
	public static IpForwardConfig INSTANCE = new IpForwardConfig().loadConfig();
	public static Logger LOGGER = LogManager.getLogger("IpForward");

	public File configFile = new File("config", "ipforward.cfg");
	private Set<String> proxyAddresses = new HashSet<>();
	private boolean blockNonProxyConnections = true;

	public IpForwardConfig loadConfig() {
		boolean exists = configFile.exists();
		Configuration config = new Configuration(configFile);
		if (exists) {
			config.load();
		}
		Property property;
		property = config.get("general", "proxyAddresses", new String[] {"127.0.0.1"}, "A list of proxy ip addresses." +
				"\nIf a address is not in this list and blockNonProxyConnections is false then the normal authentication process will take place, allowing for direct connections." +
				"\nIf this list is empty it will accept any connections with ip-forward data. However that is not recommended as it leaves your server vulnerable to fake proxy attacks." +
				"\nOnly leave this blank if you intend on migrating attacks via firewall or some other join protection system.");
		proxyAddresses.clear();
		proxyAddresses.addAll(Arrays.asList(property.getStringList()));
		property = config.get("general", "blockNonProxyConnections", true, "Whether or not to block connections that are not made though a proxy" +
				"\nIf this is false: users may join directly to this server and they will be authenticated according to the online-mode set in the server.properties" +
				"\nIf this is true: Users joining directly and/or any connections not in the proxyAddresses list will be disconnected.");
		blockNonProxyConnections = property.getBoolean(true);
		if (!exists || config.hasChanged()) {
			config.save();
		}
		return this;
	}

	public boolean isProxyAddress(String address) {
		return proxyAddresses.isEmpty() || proxyAddresses.contains(address);
	}

	public boolean blockNonProxyConnections() {
		return this.blockNonProxyConnections;
	}
}
