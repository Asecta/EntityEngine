# EntityEngine
A WIP plugin that allows blocky models &amp; animations to be imported from blender


This is very much incomplete and is likely being scrapped. The original requirement for this was to create custom mobs and such without the use of resource packs, but with that limitation lifted, there is little point continuing development. 


Here's an example of what it looks like ingame. In this example there are two animations implemented: Walk and Idle

https://user-images.githubusercontent.com/8020221/140619204-6aaaa99b-96bd-489a-ad7e-b7527cef59fd.mp4

In blender, this is constructed out of cubes fixed too a skeleton rig. The the ingame-representations are textured by the material specified in the cube name. Not the most elegent way, but still just a working example.

https://user-images.githubusercontent.com/8020221/140619337-281f7b7c-7608-4816-ad9a-91ef2a1b4b27.mp4

A start was made on implementing dynamic animations (turning head independently of body, etc), and the remenents can be seen of the initial server-side inverse-kinematicss implementation, but this was never completed. 
