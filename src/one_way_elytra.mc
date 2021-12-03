function load{
    !IF(config.dev) {
        tellraw @a [{"text":"[OWE] ","color":"gold"},{"text":"reloaded successfully!","color":"white"}]
    }
    #scoreboards
    scoreboard objectives add owe dummy
}

function tick{
    execute at @e[type=marker,tag=owe.spawn] as @a[distance=<%config.dev%>] run function give_elytra
}

function set_spawn{
    kill @e[type=marker,tag=owe.spawn]
    summon marker ~ ~ ~ {Tags:["owe.spawn"]}
}

function give_elytra{
    execute if data entity @s Inventory[{Slot:102b}] run{
        name slot_occupied
        
        data modify storage owe owe.Chestplate append from entity @s Inventory[{Slot:102b}]
        execute store result score @s owe run data get storage minecraft:owe owe.Chestplate
        #item replace entity @s armor.chest with elytra{display:{Name:'{"text":"One Way Elytra","color":"gold","italic":false}',Lore:['{"text":"Diese Elytra verschwindet","color":"white","italic":false}','{"text":"nach einmaliger Benutzung","color":"white","italic":false}']},HideFlags:4,Unbreakable:1b,Enchantments:[{}]}
    }
    execute unless data entity @s Inventory[{Slot:102b}] run{
        item replace entity @s armor.chest with elytra{display:{Name:'{"text":"One Way Elytra","color":"gold","italic":false}',Lore:['{"text":"Diese Elytra verschwindet","color":"white","italic":false}','{"text":"nach einmaliger Benutzung","color":"white","italic":false}']},HideFlags:4,Unbreakable:1b,Enchantments:[{}]}
    }
}

function give_chestplate{
    execute unless score @s owe matches 1 run function one_way_elytra:select_current_chestplate
    data modify storage minecraft:owe owe.CurrentChestplate set from storage owe owe.Chestplate[0]
    data remove storage minecraft:owe owe.Chestplate[0]

    summon armor_stand ~ ~ ~ {NoGravity:1b,Silent:1b,Invulnerable:1b,Marker:1b,Invisible:1b,Tags:["owe.item_transfer"]}
    data modify entity @e[type=minecraft:armor_stand,limit=1,tag=owe.item_transfer] HandItems[0] set from storage minecraft:owe owe.CurrentChestplate
    item replace entity @s armor.chest from entity @e[type=minecraft:armor_stand,limit=1,tag=owe.item_transfer] weapon.mainhand    
}

function select_current_chestplate{
    #Noch nicht fertig!


    scoreboard players remove @s owe 1
    execute unless score @s owe matches 1 run function one_way_elytra:select_current_chestplate
}