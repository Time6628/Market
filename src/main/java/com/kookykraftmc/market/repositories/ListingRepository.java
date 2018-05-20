package com.kookykraftmc.market.repositories;

import com.kookykraftmc.market.model.ItemStackId;
import com.kookykraftmc.market.model.Listing;
import org.spongepowered.api.item.ItemType;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface ListingRepository<C> {

    void init(C config);

    Optional<Listing> upsert(Listing listing);

    Stream<Listing> all();

    /**
     * Should be override for better perf
     * @param itemStackId
     * @param seller
     * @return
     */
    default boolean exists(ItemStackId itemStackId, UUID seller) {
        return findAllBySellerId(seller).anyMatch(listing -> ItemStackId.from(listing.getItemStack()).equals(itemStackId));
    }

    /**
     * Should be override for better perf
     * @param sellerId
     * @return
     */
    default Stream<Listing> findAllBySellerId(UUID sellerId) {
        return all().filter(listing -> listing.getSeller().equals(sellerId));
    }

    /**
     * Should be override for better perf
     * @param itemType
     * @return
     */
    default Stream<Listing> findAllByItemType(ItemType itemType) {
        return all().filter(listing -> listing.getItemStack().getType().equals(itemType));
    }

    Optional<Listing> getById(String id);

    void deleteById(String listingId);
}
