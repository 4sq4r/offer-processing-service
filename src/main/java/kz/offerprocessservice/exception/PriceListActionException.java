package kz.offerprocessservice.exception;

import lombok.Getter;

@Getter
public class PriceListActionException extends RuntimeException {

    private final String priceListId;

    public PriceListActionException(String priceListId, Throwable cause) {
        super("Error during price list action, priceListId: " + priceListId, cause);
        this.priceListId = priceListId;
    }
}
