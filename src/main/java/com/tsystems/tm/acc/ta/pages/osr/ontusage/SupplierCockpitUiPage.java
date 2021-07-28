package com.tsystems.tm.acc.ta.pages.osr.ontusage;

import com.tsystems.tm.acc.ta.util.TestSettings;

/**
 * @author msomora
 */
interface SupplierCockpitUiPage {
    
    String APP = SupplierCockpitUiPage.getAppName();
    
    static String getAppName() {
        if (TestSettings.get().isLocalhost()) {
            return "supplier-cockpit-ui";
        }
        return "portal-proxy";
    }
}
