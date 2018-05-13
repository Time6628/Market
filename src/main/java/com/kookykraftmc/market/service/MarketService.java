package com.kookykraftmc.market.service;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kookykraftmc.market.Market;
import com.kookykraftmc.market.config.ConfigLoader;
import com.kookykraftmc.market.config.Texts;
import com.kookykraftmc.market.model.BlackListItem;
import com.kookykraftmc.market.model.ItemStackId;
import com.kookykraftmc.market.model.Listing;
import com.kookykraftmc.market.repositories.BlackListRepository;
import com.kookykraftmc.market.repositories.ListingRepository;
import com.kookykraftmc.market.repositories.RepositoryProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Singleton
public class MarketService {

    private final BlackListRepository<?> blackListRepository;
    private final ListingRepository<?> listingRepository;
    private final Cause marketCause;
    @Inject
    private ConfigLoader configLoader;

    @Inject
    private UuidCacheService uuidCache;

    private List<BlackListItem> blacklistedItems = newArrayList();

    @Inject
    public MarketService(RepositoryProvider repositoryProvider, Market marketPlugin) {
        this.blackListRepository = repositoryProvider.getBlackListRepository();
        this.listingRepository = repositoryProvider.getListingRepository();
        marketCause = Cause.builder().append(marketPlugin).build(EventContext.builder().build());
    }

    public static PaginationService getPaginationService() {
        return Sponge.getServiceManager().provide(PaginationService.class).get();
    }

    public static EconomyService getEconomyService() {
        return Sponge.getServiceManager().provide(EconomyService.class).get();
    }

    /**
     * Update a name attached to a uuid.
     *
     * @param player The player to update.
     */
    public void updateUUIDCache(Player player) {
        this.uuidCache.update(player.getUniqueId(), player.getName());
    }

    /**
     * Add a new listing to the database.
     *
     * @param listing The {@link Listing} that's selling it.
     * @return the listing or empty if an error occured
     */
    public Optional<Listing> addListing(Listing listing) {
        if (listing.getStock() < listing.getQuantityPerSale() || listing.getQuantityPerSale() <= 0 || isBlacklisted(listing.getItemStack()))
            return Optional.empty();
        if (exists(listing))
            return Optional.empty();

        return listingRepository.addListing(listing);
    }

    /**
     * Checks to see if a player is already selling an item of similar type.
     *
     * @param listing The {@link Listing} to check for.
     * @return true if another listing exists, false otherwise.
     */
    public boolean exists(Listing listing) {
        return listingRepository.exists(ItemStackId.from(listing.getItemStack()), listing.getSeller());
    }

    /**
     * Gets all of the current listings.
     *
     * @param playerUUID The {@link UUID} of the person doing the list.
     * @param staff      true if the player is staff
     * @return A list of {@link Listing}s.
     */
    public List<Listing> getListings(@Nullable UUID playerUUID, boolean staff) {
        return this.listingRepository.all()
                .filter(listing -> listing.getStock() > listing.getQuantityPerSale() || staff || listing.getSeller().equals(playerUUID))
                .collect(Collectors.toList());
    }

    public PaginationList getListingsPagination(UUID player, boolean staff) {
        List<Text> texts = new ArrayList<>();
        getListings(player, staff).forEach(listing -> texts.add(toText(listing)));
        return getPaginationService().builder().contents(texts).title(Texts.MARKET_LISTINGS).build();
    }

    /**
     * Remove a listing from the listings.
     *
     * @param id    The listing id to remove.
     * @param uuid  The {@link UUID} of the person removing it.
     * @param staff If true, remove the listing regardless of the remover's {@link UUID},
     *              otherwise it will ensure the remover is the seller of the listing.
     * @return The removed items.
     */
    public List<ItemStack> removeListing(String id, UUID uuid, boolean staff) {
        return listingRepository.getById(id)
                .filter(listing -> listing.getSeller().equals(uuid) || staff)
                .map(listing -> {
                    int inStock = listing.getStock();

                    int stacksInStock = inStock / listing.getItemStack().getMaxStackQuantity();
                    //new list for stacks
                    List<ItemStack> stacks = new ArrayList<>();
                    //until all stacks are pulled out, keep adding more stacks to stacks
                    for (int i = 0; i < stacksInStock; i++) {
                        stacks.add(listing.getItemStack().copy());
                    }
                    if (inStock % listing.getItemStack().getMaxStackQuantity() != 0) {
                        ItemStack extra = listing.getItemStack().copy();
                        extra.setQuantity(inStock % listing.getItemStack().getMaxStackQuantity());
                        stacks.add(extra);
                    }
                    //remove from the listings
                    listingRepository.deleteById(id);

                    return stacks;
                }).orElseGet(Lists::newArrayList);
    }

    public PaginationList getListing(String id) {
        Optional<Listing> listing = listingRepository.getById(id);
        return getPaginationService().builder()
                .contents(
                        listing.map(l -> {
                            List<Text> texts = newArrayList();
                            texts.add(Texts.quickItemFormat(l.getItemStack()));
                            texts.add(Text.of(" Seller: " + uuidCache.getName(l.getSeller())));
                            texts.add(Text.of(" Price: " + l.getPrice()));
                            texts.add(Text.of(" Quantity: " + l.getQuantityPerSale()));
                            texts.add(Text.of(" Stock: " + l.getStock()));

                            texts.add(Text.builder()
                                    .append(Text.builder()
                                            .color(TextColors.GREEN)
                                            .append(Text.of("[Buy]"))
                                            .onClick(TextActions.suggestCommand("/market buy " + id))
                                            .build())
                                    .append(Text.of(" "))
                                    .append(Text.builder()
                                            .color(TextColors.GREEN)
                                            .append(Text.of("[QuickBuy]"))
                                            .onClick(TextActions.runCommand("/market buy " + id))
                                            .onHover(TextActions.showText(Text.of("Click here to run the command to buy the item.")))
                                            .build())
                                    .build());
                            return texts;
                        }).orElseGet(() -> {
                            List<Text> texts = newArrayList();
                            texts.add(Text.of(TextColors.RED, "Listing not found."));
                            return texts;
                        })
                ).title(Texts.MARKET_LISTING.apply(Collections.singletonMap("id", id)).build()).build();
    }

    /**
     * Adds stock to a listing.
     *
     * @param itemStack  The {@link ItemStack} to add.
     * @param listingId  The listing id to add to.
     * @param playerUUID The uuid of the player adding to the stock.
     * @return false if listing is null, or the listing's {@link ItemStack} doesn't match.
     * true if it added successfully.
     */
    public boolean addStock(ItemStack itemStack, String listingId, UUID playerUUID) {
        return listingRepository.getById(listingId)
                .filter(listing -> listing.getSeller().equals(playerUUID))
                .filter(listing -> ItemStackId.from(itemStack).equals(ItemStackId.from(listing.getItemStack())))
                .map(listing -> {
                    listing.setStock(listing.getStock() + itemStack.getQuantity());

                    listingRepository.deleteById(listing.getId());
                    listingRepository.addListing(listing);
                    return true;
                }).orElse(false);
    }

    /**
     * Buy from a listing.
     *
     * @param uniqueAccount The account of the player buying it.
     * @param id            The listing id
     * @return the {@link ItemStack} created from the purchase,
     * null if it could not purchase it.
     */
    public ItemStack purchase(UniqueAccount uniqueAccount, String id) {
        return listingRepository.getById(id)
                .map(listing -> {
                    TransactionResult tr = uniqueAccount.transfer(
                            getEconomyService().getOrCreateAccount(listing.getSeller()).get(),
                            getEconomyService().getDefaultCurrency(),
                            BigDecimal.valueOf(listing.getPrice()),
                            this.marketCause);
                    if (tr.getResult().equals(ResultType.SUCCESS)) {
                        //getById the itemstack
                        ItemStack is = listing.getItemStack();
                        //getById the quantity per sale
                        int quant = listing.getQuantityPerSale();
                        //getById the amount in stock
                        int inStock = listing.getStock();
                        //getById the new quantity
                        int newQuant = inStock - quant;
                        //if the new quantity is less than the quantity to be sold, expire the listing
                        listing.setStock(newQuant);
                        listingRepository.deleteById(listing.getId());
                        listingRepository.addListing(listing);
                        ItemStack nis = is.copy();
                        nis.setQuantity(quant);
                        return nis;
                    } else {
                        return null;
                    }
                }).orElse(null);
    }

    public PaginationList getBlacklistedItemList() {
        List<Text> texts = new ArrayList<>();
        for (BlackListItem blacklistedItem : this.blacklistedItems) {
            texts.add(Text.of(blacklistedItem.getId()));
        }
        return getPaginationService().builder().contents(texts).title(Text.of(TextColors.GREEN, "Market Blacklist")).build();
    }

    public boolean isBlacklisted(ItemStack itemStack) {
        ItemStackId id = ItemStackId.from(itemStack);
        return blacklistedItems.stream().anyMatch(blItem -> id.equals(blItem.getId()));
    }

    public boolean addToBlackList(String id) {
        BlackListItem blackListItem = new BlackListItem(new ItemStackId(id));
        boolean ok = blackListRepository.add(blackListItem);
        blacklistedItems.add(blackListItem);
        return ok;

    }

    public boolean removeFromBlackList(ItemStackId id) {
        boolean ok = blackListRepository.deleteById(id);
        blacklistedItems.removeIf(bl -> bl.getId().equals(id));
        return ok;
    }

    /**
     * Search the listings for a specific item.
     *
     * @param itemType The itemtype to search for.
     * @return A {@link PaginationList} of the results to easily send it to players.
     */
    public PaginationList searchForItem(ItemType itemType) {
        List<Text> texts = new ArrayList<>();

        listingRepository.findAllByItemType(itemType).forEach(listing -> texts.add(toText(listing)));

        if (texts.size() == 0) texts.add(Text.of(TextColors.RED, "No listings found."));
        return getPaginationService().builder().contents(texts).title(Texts.MARKET_SEARCH).build();

    }

    public PaginationList searchForUUID(UUID sellerId) {
        List<Text> texts = new ArrayList<>();

        listingRepository.findAllBySellerId(sellerId).forEach(listing -> {
            texts.add(toText(listing));
        });

        if (texts.size() == 0) texts.add(Text.of(TextColors.RED, "No listings found."));
        return getPaginationService().builder().contents(texts).title(Texts.MARKET_SEARCH).build();
    }

    private Text toText(Listing listing) {
        Text.Builder l = Text.builder();
        l.append(Texts.quickItemFormat(listing.getItemStack()));
        l.append(Text.of(" "));
        l.append(Text.of(TextColors.WHITE, "@"));
        l.append(Text.of(" "));
        l.append(Text.of(TextColors.GREEN, "$" + listing.getPrice()));
        l.append(Text.of(" "));
        l.append(Text.of(TextColors.WHITE, "for"));
        l.append(Text.of(" "));
        l.append(Text.of(TextColors.GREEN, listing.getQuantityPerSale() + "x"));
        l.append(Text.of(" "));
        l.append(Text.of(TextColors.WHITE, "Seller:"));
        l.append(Text.of(TextColors.LIGHT_PURPLE, " " + this.uuidCache.getName(listing.getSeller())));
        l.append(Text.of(" "));
        l.append(Text.builder()
                .color(TextColors.GREEN)
                .onClick(TextActions.runCommand("/market check " + listing.getId()))
                .append(Text.of("[Info]"))
                .onHover(TextActions.showText(Text.of("View more info about this listing.")))
                .build());
        return l.build();
    }

    public void updateBlackList() {
        this.blacklistedItems = blackListRepository.all().collect(Collectors.toList());
    }


    public boolean isHuskyUILoaded() {
        return Sponge.getPluginManager().isLoaded("huskyui");
    }

    public boolean isChestGUIDefault() {
        configLoader.loadConfig();
        return configLoader.getMarketConfig().chestDefault;
    }

}
