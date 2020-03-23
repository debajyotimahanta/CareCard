package com.coronacarecard.model;

public enum BusinessState {
    /**
     * This is the state of the business when it is first created.
     * Its not associated with an owner and no one has claimed it
     */
    Draft,

    /**
     * Once a business is claimed, we mark it as claimed, if the owner
     * has entered all the details
     */
    Claimed,

    /**
     * Once our admin validates the business details, we mark it as claimed.
     * Then we wait for the business to fill in there payment details
     */
    Pending,

    /**
     * After the owner fills in the business payment details we mark it as Active
     * It can now recieve payments
     */
    Active
}
