##Colored Light Core##
The colored light core is an open source Forge Core-Mod that aims to replace Minecraft's lighting engine with a backwards compatible system that adds three new channels of light.  With the addition of Red, Green, and Blue color data, blocks can be configured with customized light values, while the OpenGL and the lighting engine deals with the new information.  That's not all though!  While colored lights are awesome, the ultimate goal of this project is to extend an API for other mod writers to use!  Expect to see some mods show up that hook into the API we provide!
![splash](http://i.imgur.com/DpmhN9Q.png "Minecraft Forge 1.7.10")


- If you're interested in helping develop the Colored Light Core, you probably want to setup the development environment .  
- If you want to hook into our API, you'll want to import the Colored Light API Jarfiles, and Access Transformer into your current Development Environment!
- If you want to install, and play with the colored light core, You'll have to be satisfied with our early beta release, found on the [Minecraft Forums](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/wip-mods/1445251-1-7-2-beta-wip-colored-light-progress-and "Minecraft Forums - Beta WIP Colored Lights - Progress and Discussion")

================
####Setup Colored Light Development Environment####
You're going to need to pull the repository, and switch to the current development Branch.  Then you can use the gradle wrapper (or gradle if you have it installed) to setup and build the workspace

`cd the/place/you/want/to/put/this`  
`git clone -b1.7.2 https://github.com/CptSpaceToaster/CptsModdingLight`  
`cd CptsModdingLight`  
`gradlew setupDecompWorkspace`  
`gradlew eclipse (or your preferred development environment)`  

Once Gradle takes 30 minutes or less to do it's nonsense, you'll actually have an entire eclipse project to work with!  Open up eclipse to a brand new workspace, *NOT* in the CptsModdingLight directory, then `import an existing project` 
![import](http://i.imgur.com/iyw5zHG.png)

Select the CptsModdingLight directory as your existing project, and BAM!  You'll actually have most of the project after that, but you still need to setup a run configuration:

![run_config1](http://i.imgur.com/XwlGnEw.png)  
![run_config2](http://i.imgur.com/stb8IIN.png)  
![run_config3](http://i.imgur.com/iQlQaSX.png)  

` --version 1.7 --tweakClass cpw.mods.fml.common.launcher.FMLTweaker --accessToken MINECRAFT_USERNAME --username=IN_GAME_NAME --userProperties={} --assetIndex 1.7.10 --assetsDir D:\users\USERNAME\.gradle\caches\minecraft\assets  
  
-Dfml.ignoreInvalidMinecraftCertificates=true -Dfml.coreMods.load=coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin `
================
####Add the Colored Light API to an existing Forge Environment####
I'll have to write this eventually
================
####Install the Colored Light Core####
Installs like a forge core mod... I'll write this up eventually as well I suppose
