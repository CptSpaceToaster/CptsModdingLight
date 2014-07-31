####Setting up the Colored Light Development Environment####
You're going to need to pull the repository, and switch to the current development branch.  Then you can use the gradle wrapper (or gradle if you have it installed) to setup and build the workspace

`cd the/place/you/want/to/put/this`  
`git clone -b1.7.2 https://github.com/CptSpaceToaster/CptsModdingLight`  
`cd CptsModdingLight`  
`gradlew setupDecompWorkspace`  
`gradlew eclipse (or your preferred development environment)`  

Once Gradle takes 30 minutes or less to do it's nonsense, you'll actually have an entire eclipse project to work with!  Open up eclipse to a brand new workspace, *NOT* in the CptsModdingLight directory, then `import an existing project`

![import1](http://i.imgur.com/iyw5zHG.png)
![import2](http://i.imgur.com/HMVrjcX.png)

Go ahead and select the CptsModdingLight directory as your existing project, and BAM!  You'll actually have most of the project after that, but you still need to setup a run configuration:

![run_config1](http://i.imgur.com/XwlGnEw.png)  
![run_config2](http://i.imgur.com/stb8IIN.png)  
![run_config3](http://i.imgur.com/iQlQaSX.png)  

Program Arguments:  
`--version 1.7 --tweakClass cpw.mods.fml.common.launcher.FMLTweaker --accessToken MINECRAFT_USERNAME  --username=IN_GAME_NAME --userProperties={} --assetIndex 1.7.10 --assetsDir C:\users\USERNAME\.gradle\caches\minecraft\assets`

VM Arguments:  
`-Dfml.ignoreInvalidMinecraftCertificates=true -Dfml.coreMods.load=coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin`
