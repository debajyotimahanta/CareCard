package com.coronacarecard.service;

import com.coronacarecard.exceptions.BusinessAlreadyClaimedException;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.CustomerException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessRegistrationRequest;
import com.coronacarecard.model.PaymentSystem;
import org.springframework.transaction.annotation.Transactional;

public interface OwnerService {

    /**
     * This service will do the following:
     * 1. If the business already exists and is claimed return error
     * 2. If the business exits check and is in draft, check who is trying to claim
     *  2. If its the same info return the business and also generate SNS Notification
     *  2. If no info associate continue
     *  2. If its different business throw already claimed error and generate conflict notification
     * 2. If business doesn't not exists, create a business with draft status
     * 3. Create user profile with email and phone number and associate it with the business
     * 4. Set the status of the business to the claimed
     * 5. Send SNS Notification saying a business as been claimed with business id
     * 6. Return business which will have the business id
     * Next step would be to associate business with strip profile
     * @param request
     * @return
     */
    @Transactional
    Business claimBusiness(BusinessRegistrationRequest request)
            throws BusinessAlreadyClaimedException, InternalException, BusinessNotFoundException;

    /**
     * This method is called by external payment system (Stripe for now) to register user.
     * The encrypted details has more info which needs to match as follows
     * 1. Make API call to get registration details
     * 2. Compare registration details with what we know about business atleast the email or phone number needs to match
     *  1. Make it easy based on the error code to find out what didnt match
     *  1. Also log it so we trace it easily in logs
     * 2. Make sure email address matches the encrypted value
     * 3. If the encryptedDetails is null we ignore it
     * 4. Com
     * Also do platform specific validations if any like
     * for explain in Strip https://stripe.com/docs/connect/express-accounts#verify-the-accounts-capability
     * @param encryptedDetails
     * @param externalRefId
     * @return
     */
    void registerOwner(String encryptedDetails, String externalRefId);

    /**
     * Admin uses this method to mark the business as claimed. IT returns the payment system URL, that the
     * business will use to onboard.
     */
    @Transactional
    String approveClaim(PaymentSystem paymentSystem, Long id) throws CustomerException;

    /**
     * For the given business decline the claim and remove its realtionship with owner.
     * Also generate SNS notification which could be used to inform business
     * @param id
     */
    @Transactional
    void declineClaim(Long id) throws BusinessNotFoundException;
}
