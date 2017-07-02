package com.karoljanowski.service.impl;

import com.karoljanowski.domain.Billing;
import com.karoljanowski.domain.UserBilling;
import com.karoljanowski.service.BillingService;
import org.springframework.stereotype.Service;

/**
 * Created by Karol Janowski on 2017-06-29.
 */
@Service
public class BillingServiceImpl implements BillingService{

    @Override
    public Billing setByUserBilling(UserBilling userBilling, Billing billing) {
        billing.setBillingName(userBilling.getUserBillingName());
        billing.setBillingStreet1(userBilling.getUserBillingStreet1());
        billing.setBillingStreet2(userBilling.getUserBillingStreet2());
        billing.setBillingCity(userBilling.getUserBillingCity());
        billing.setBillingZipCode(userBilling.getUserBillingZipCode());
        billing.setBillingCountry(userBilling.getUserBillingCountry());
        return billing;
    }
}

















