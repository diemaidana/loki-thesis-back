package com.loki.tesis.offers;

public enum OfferStatus {
    /**
     * Negotiation created. Waiting response
     */
    OPEN,

    /**
     * At least one counter-offer has been made.
     */
    IN_PROGRESS,

    /**
     * Offer accepted by all parts. Payment needed.
     */
    ACCEPTED,

    /**
     * One user has rejected the last offered price.
     */
    REJECTED,

    /**
     * One user has canceled the offer.
     */
    CANCELLED,

    /**
     * The offer has expired after 48hs without response.
     */
    EXPIRED,

    /**
     * The offer has been payed.
     */
    PAID
}
