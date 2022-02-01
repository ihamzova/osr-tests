package com.tsystems.tm.acc.ta.team.morpheus.mobiledpu;

import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.mobiledpu.MobileDpuPage;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class MobileDpu extends GigabitTest {

    @BeforeMethod
    public void init() {
        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase.RHSSOMobileDpu);
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }

    @Test
    public void testLogin() {
        MobileDpuPage mobileDpuPage = MobileDpuPage.openPage();
        mobileDpuPage.validateUrl();

    }

}
