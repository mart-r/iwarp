# iWarp Spigot plugin

iWarp available on [SpigotMC](https://www.spigotmc.org/resources/iwarp-warps-with-upkeep-addon-to-essentialsx.68157/).


### Releasing a new version
After changes, in order to release a new version, you need to change the version in `pom.xml`.
The version will be automatically populated into `plugin.yml`.

After that, just building the jar should be sufficient (`mvn clean package`).
The `iWarp.jar` should be in the `target` folder.
There is no automatic distribution set up at this time.
