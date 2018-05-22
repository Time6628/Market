package com.kookykraftmc.market.repositories;

import com.kookykraftmc.market.model.Listing;
import com.kookykraftmc.market.service.ItemStackComparator;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface ListingRepository<C> {

    void init(C config);

    Optional<Listing> upsert(Listing listing);

    Stream<Listing> all();

    /**
     * Should be override for better perf
     *
     * @param itemStack
     * @param seller
     * @return
     */
    default boolean exists(ItemStack itemStack, UUID seller) {
        return findAllBySellerId(seller).anyMatch(listing -> ItemStackComparator.eq(listing.getItemStack(), itemStack));
    }

    /**
     * Should be override for better perf
     *
     * @param sellerId
     * @return
     */
    default Stream<Listing> findAllBySellerId(UUID sellerId) {
        return all().filter(listing -> listing.getSeller().equals(sellerId));
    }

    /**
     * Should be override for better perf
     *
     * @param itemType
     * @return
     */
    default Stream<Listing> findAllByItemType(ItemType itemType) {
        return all().filter(listing -> listing.getItemStack().getType().equals(itemType));
    }

    Optional<Listing> getById(String id);

    void deleteById(String listingId);
}
