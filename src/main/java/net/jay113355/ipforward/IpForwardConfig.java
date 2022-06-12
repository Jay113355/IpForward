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

import com.electronwill.nightconfig.core.file.FileConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IpForwardConfig {
	public static IpForwardConfig INSTANCE = new IpForwardConfig();
	public static Logger LOGGER = LogManager.getLogger("IpForward");

	public File configFile = new File("config", "ipforward.toml");
	private final Set<String> proxyAddresses = new HashSet<>();
	private boolean blockNonProxyConnections = true;

	public IpForwardConfig loadConfig() {
		FileConfig config = FileConfig.builder(configFile)
				.defaultData(IpForwardConfig.class.getResource("/ipforward.toml"))
				.charset(StandardCharsets.UTF_8)
				.build();
		config.load();
		List<String> addresses = config.get("general.proxyAddresses");
		proxyAddresses.clear();
		proxyAddresses.addAll(addresses);
		blockNonProxyConnections = config.get("general.blockNonProxyConnections");
		config.save();
		return this;
	}

	public boolean isProxyAddress(String address) {
		return proxyAddresses.isEmpty() || proxyAddresses.contains(address);
	}

	public boolean blockNonProxyConnections() {
		return this.blockNonProxyConnections;
	}
}
