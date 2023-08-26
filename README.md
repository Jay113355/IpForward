I've archived this project as I no longer use it as it can be replaced by https://github.com/caunt/BungeeForge with higher minecraft version support.

# IPForward
This is a [MinecraftForge](https://minecraftforge.net) (core)mod that uses [Mixins](https://github.com/SpongePowered/Mixin) to add IP forwarding capabilities to forge.

This mod requires the [MixinBootstrap](https://github.com/LXGaming/MixinBootstrap) mod or a similar mod that contains and initialises the mixin library.

Fair warning, I only tested this with [Velocity](https://www.velocitypowered.com/) which handles forwarding the FML marker differently then BungeeCord. 

### Features
- Lightweight. This is designed to be an alternative to using something like [SpongeForge](https://www.spongepowered.org/) or some bukkit-forge hybrid software. 
- Secure. This mod allows you to whitelist proxies by ip instead of having to use a firewall (this can be disabled if you prefer firewalls or have some other setup.)


### Configuration
This mod has two config options

| Key | Default Value | Description |
| --- | --- | --- |
| proxyAddresses | 127.0.0.1 | A list of proxy ip addresses. |
| blockNonProxyConnections | true | Whether or not to block connections that are not made though a proxy |

##### Additional notes:
- If a address is not in the `proxyAddresses` list and `blockNonProxyConnections` is false then the normal authentication process will take place, allowing for direct connections.
- If the `proxyAddresses` list is empty it will accept any connections with ip-forward data. However that is not recommended as it leaves your server vulnerable to fake proxy attacks. Only leave this blank if you intend on migrating attacks via firewall or some other join protection system.
- If `blockNonProxyConnections` is false, users may join directly to the server and they will be authenticated according to the online-mode set in the server.properties If it is true, Users joining directly and/or any connections not in the proxyAddresses list will be disconnected.
- You do **NOT** need to change `online-mode` in the `server.properties` file for this mod to work. This mod takes priority over the normal authentication process.

### License 
IpForward is licensed under [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)
