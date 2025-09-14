ZeMarketPlugin:

Let's be honest, running a server marketplace is a thankless job. You build a beautiful area, and your players proceed to fill it with half-finished dirt huts before disappearing for six months. You, the benevolent server owner, are left to clean up the architectural graveyard.

No more.

ZeMarketPlugin is the cold, calculating, automated capitalist your server deserves. It's a hands-off management system that handles the buying, the protecting, and most importantly, the evicting, so you don't have to. Set it up, and watch the glorious churn of commerce manage itself.

How Does This Glorious Dystopia Work?
It's beautifully simple. You, the admin, define a large plot of land using a WorldGuard region. The plugin then swoops in and meticulously carves that land into a perfect grid of tiny, identical plots, separated by paths of a size you dictate.

Players can then pay a one-time fee to claim an empty plot. It's theirs. They can build, they can sell, and no one else can touch their stuff. But, if they don't log in for a configurable amount of time, the plugin will mercilessly evict them, wipe the plot clean, and put it back on the market.

Simple. Efficient. Unforgiving.

Features
Automatic Plot Generation
Stop painstakingly marking out 5x5 plots by hand. Just give the plugin a giant rectangle and watch it create a perfect, soulless grid of commercial opportunity.

Ruthless Inactivity Kicker
The plugin's main feature. It keeps track of when players were last online. If someone exceeds the expiration date, their plot is automatically unclaimed and reset. Their junk is gone. The plot is pristine. The market remains beautiful. No more player-made ruins.

Hands-Off Administration
Once you've defined the market regions and set the price, your work is done. Sit back, relax, and let the unfeeling logic of the plugin handle the rest.

Vault Integration
Because the only thing better than giving players a shop is making them pay for it first. This plugin hooks directly into your server's economy.

Plot Protection
Naturally, only the owner of a plot can build or break blocks on it. This prevents theft, griefing, and unauthorized interior decorating.

A Player's Guide to Surviving the Market

/market claim	Your one-time payment to enter the glorious world of retail.

/market unclaim	For when you realize running a shop is actual work. No refunds.

/market home	Can't remember which of the 100 identical plots is yours? Use this.

/market info	Check who owns the plot you're currently loitering on.

/market help	In case you forget these four simple commands.

The Admin's Rulebook
Permission: market.admin

/market define <name> <region>	Tell the machine which WorldGuard region to convert into tiny boxes of commerce.

/market delete <name>	Deletes a market definition. Does not automatically fire everyone.

/market setprice <price>	Decide how much it costs to live the dream.

/market setexpire <days>	Set the official "you've been gone too long, you're evicted" timer.

/market reload	Force the plugin to re-read its configuration files.

Dependencies (Don't Skip This Part)

This plugin has no soul, but it does have dependencies. You absolutely need the following installed and working for this plugin to function:

Vault: For handling all the money.

An Economy Plugin: Any plugin that hooks into Vault (like EssentialsX, iConomy, etc.).

WorldGuard: For defining the market regions. The plugin doesn't work without it.

Installation:

Make sure you have all the dependencies installed.

Download the latest ZeMarketPlugin.jar from the Releases page.

Place the .jar file into your server's /plugins directory.

Restart the server.

Use the commands above to configure your automated market empire. If it works, you did it right. If not, you probably missed step 1.
