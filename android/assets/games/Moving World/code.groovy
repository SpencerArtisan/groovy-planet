////////////////////////////////////////////// 
//         Welcome to Planet Burpl!         // 
//                                          // 
//   Use MOVETOWARDS to move your sprite    // 
////////////////////////////////////////////// 

world = blurp.createImageSprite("world.png")
targetX = 400
targetY = 300
speed = 500

while (true) {
    world.moveTowards(targetX, targetY, speed)
    blurp.blurpify()
    if (world.x == targetX && world.y == targetY) {
        targetX = utils.random(150, 750)
        targetY = utils.random(150, 450)
        speed = utils.random(100, 1000)
    }
}
