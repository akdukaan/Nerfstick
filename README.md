# Nerfstick
Nerfs debug stick to make it more fair in survival. For example, Nerfstick can prevents debug usage on doors and beds since you can duplicate the item like that. It can also prevent usage on waterloggable blocks in the nether since you can add water in the nether like that. This should also respect claims and other protections. It's super configurable too with permissions! 

## Setup for Luckperms
Here's what I use on my server and it's a good starting point. Luckperms works with regex and that's what the r= means in the permissions below but some permission plugins might not support this so if you're not using Luckperms, look into that some more.

Set **minecraft.debugstick.always** to true to allow players to use the debug stick outside of creative mode. This permission comes from Paper so if you're not using paper, see if there's another perm or plugin to do the same.

Set **r=nerfstick.use.minecraft.\*.waterlogged** to **false** in context of dimension-type **the_nether**. This is to prevent waterlogging blocks in the nether.

Set these permissions to true. It's a whitelist of blocks that could be modified with the stick.
* nerfstick.use.minecraft.barrel.\*
* nerfstick.use.minecraft.bell.\*
* nerfstick.use.minecraft.furnace.\*
* nerfstick.use.minecraft.ladder.\*
* nerfstick.use.minecraft.lectern.\*
* nerfstick.use.minecraft.lever.\*
* nerfstick.use.minecraft.lightning_rod.\*
* nerfstick.use.minecraft.note_block.\*
* nerfstick.use.minecraft.observer.\*
* nerfstick.use.minecraft.rail.\*
* nerfstick.use.minecraft.redstone_comparator.\*
* nerfstick.use.minecraft.tripwire_hook.\*
* nerfstick.use.redstone_lamp.\*
* nerfstick.use.minecraft.ender_chest
* r=nerfstick.use.minecraft.\*_chest.\*
* r=nerfstick.use.minecraft.\*_fence.\*
* r=nerfstick.use.minecraft.\*_gate.\*
* r=nerfstick.use.minecraft.\*_glazed_terracotta.\*
* r=nerfstick.use.minecraft.\*_lantern.\*
* r=nerfstick.use.minecraft.\*_leaves.\*
* r=nerfstick.use.minecraft.\*_log.\*
* r=nerfstick.use.minecraft.\*_rail.\*
* r=nerfstick.use.minecraft.\*_repeater.\*
* r=nerfstick.use.minecraft.\*_sign.\*
* r=nerfstick.use.minecraft.\*_stairs.\*
* r=nerfstick.use.minecraft.\*_trapdoor.\*
* r=nerfstick.use.minecraft.\*_wall.\*



