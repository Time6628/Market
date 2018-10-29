# Market

Market is similar to the [GlobalMarket](https://dev.bukkit.org/projects/global-market) plugin from bukkit, but is very different.

## Features:
- Blacklist items from being sold.
- Redis based
- Completely Chat Based (No Chests Needed!)
Multi-Server support
- Item/Player Search


## Requirements:
- A Redis Server.
### Optional for 0.2:
 - (Optional) [HuskykUI](https://forums.spongepowered.org/t/huskyui-a-simple-fast-ui-system-for-plugins/19557) Plugin for chest GUI listings.
 - (Optional) Use MongoDB or DynamoDB instead of Redis.
 
## Commands:
### User Commands:
 - /market - Base command - market.command.base
- /market create - Creates a listing - market.command.createlisting
- /market listings - Shows all the listings - market.command.listings
- /market check - Shows information about a listing, all where you can easily get the command to buy from that listing - market.command.check
- /market buy - Buy from a listing - market.command.buy
- /market addstock - Add more stock to a listing - market.command.addstock
- /market removelisting - Remove a listing - market.command.removelisting
- /market blacklist - List all the blacklisted items - market.command.blacklist
- /market search <Item | User> <modid:itemid | Player Name> - Search the listings by player or item.

### Admin/Staff Commands:
- /market blacklist add modid:itemid - Add an item to the blacklist - market.command.staff.blacklist.add
- /market blacklist remove modid:itemid - Remove an item to the blacklist - market.command.staff.blacklist.remove

### Other Permissions:
- market.command.staff.removelisting - Allows staff to remove listings that aren’t there’s.

Discord: https://discord.gg/yUJc9G7

## Support Me:
Enjoying the plugin? You can support me here: https://www.paypal.me/KookyKraftMCNetwork

## Known Issues:
 - No file based db.
 
## Planned Features:
 - Server(Infinite) Listings.

Screenshots
Base command result:
![Base command result](https://i.imgur.com/dpIjUt9.png)


Listings:  
![listings](https://i.imgur.com/dpIjUt9.png)


/market check id:  
![check](https://i.imgur.com/OgD2c4L.png)


Anything in blue brackets is an item you can hover over.
