execute unless score @s owe matches 1 run function one_way_elytra:select_current_chestplate
data modify storage minecraft:owe owe.CurrentChestplate set from storage owe owe.Chestplate[0]
data remove storage minecraft:owe owe.Chestplate[0]
summon armor_stand ~ ~ ~ {NoGravity:1b,Silent:1b,Invulnerable:1b,Marker:1b,Invisible:1b,Tags:["owe.item_transfer"]}
data modify entity @e[type=minecraft:armor_stand,limit=1,tag=owe.item_transfer] HandItems[0] set from storage minecraft:owe owe.CurrentChestplate
item replace entity @s armor.chest from entity @e[type=minecraft:armor_stand,limit=1,tag=owe.item_transfer] weapon.mainhand