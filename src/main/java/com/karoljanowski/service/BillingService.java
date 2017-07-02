package com.karoljanowski.service;

import com.karoljanowski.domain.Billing;
import com.karoljanowski.domain.UserBilling;

/**
 * Created by Karol Janowski on 2017-06-21.
 */
public interface BillingService {
    Billing setByUserBilling(UserBilling userBilling, Billing billing);

}
