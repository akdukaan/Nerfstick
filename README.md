# Nerfstick
Nerfs debug stick to make it more fair in survival. For example, Nerfstick prevents debug usage on doors and beds since you can duplicate the item like that. It also prevents usage on waterloggable blocks in the nether since you can add water in the nether like that. It also prevents usage on crops since you can grow crops super fast like that.

## Contributing
I'd like contributions that make the plugin less strict. Here are a couple ways that could be done
1. We currently use a whitelist for the list of allowed blocks. Let's add more blocks to the whitelist if we know of any safe ones.
2. Instead of denying debug stick usage on any water-loggable block in the nether, I think it would be nice to only deny the action if it's going to waterlog the block. OR, and I don't really know if this is possible because I actually don't know too much about debug sticks, but if it's possible, it would be even better if we could make it skip over any water-loggable states, and skip to the next non-waterlogged state.
3. Similar to the previous note, it would be better if we denied usage on slabs/doors/beds only in the cases where it would turn them into dupe-able states.
