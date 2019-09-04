package com.tsystems.tm.acc.ta.team.upiter;

import com.tsystems.tm.acc.data.models.credentials.Credentials;
import com.tsystems.tm.acc.data.models.nvt.Nvt;
import com.tsystems.tm.acc.data.models.oltdevice.OltDevice;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.nvt.NvtCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.ta.team.upiter.tbb.oltComissioning.OLTCommissioningTBB;
import com.tsystems.tm.acc.ta.ui.UITest;
import com.tsystems.tm.acc.ta.util.driver.RHSSOAuthListener;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Slf4j
@Epic("E2E")
@Feature("[TMI] Initiative 09: Gebiets Journey Pre")
@TmsLink("DIGIHUB-14774")
public class InitiativePreTest extends UITest {

    /*private InitializeSystemTBB initTbb = new InitializeSystemTBB();
    private RolloutAreaTBB rollTbb = new RolloutAreaTBB();*/
    private OLTCommissioningTBB oltTbb = new OLTCommissioningTBB();

    @BeforeClass
    public void setLoginData() {
        Credentials loginData = OsrTestContext.get().getData().getCredentialsDataProvider().get(CredentialsCase.NO_DATA_PRESENT);
        RHSSOAuthListener.resetLoginData(loginData.getLogin(), loginData.getPassword());
    }
/*
    @Test(description = "[TMI] Cleanup")
    @Description("[TMI] Cleanup")
    public void initializeSystem() {
        List<File> fileList = new ArrayList<>();
        fileList.add(new File(getClass().getResource("/e2e/SO_DATA.xlsx").getFile()));
        fileList.add(new File(getClass().getResource("/e2e/ACH_DATA.xlsx").getFile()));
        fileList.add(new File(getClass().getResource("/e2e/EXP_DATA.xlsx").getFile()));
        fileList.add(new File(getClass().getResource("/e2e/IBT_DATA.xlsx").getFile()));
        fileList.add(new File(getClass().getResource("/e2e/PRM_DATA.xlsx").getFile()));
        fileList.add(new File(getClass().getResource("/e2e/NE_DATA.xlsx").getFile()));
        fileList.add(new File(getClass().getResource("/e2e/SM_DATA_empty.xlsx").getFile()));
        fileList.add(new File(getClass().getResource("/e2e/WO_DATA.xlsx").getFile()));
        fileList.add(new File(getClass().getResource("/e2e/APM_DATA.xls").getFile()));
        fileList.add(new File(getClass().getResource("/e2e/TP_DATA.xls").getFile()));
        initTbb.restoreDbState(new EnvironmentSnapshot(fileList));
    }

    @Test(description = "DIGIHUB-13154. [TMI] Full import KLS Address Master data", dependsOnMethods = "initializeSystem")
    @TmsLink("DIGIHUB-13154")
    @Description("[TMI] Full import KLS Address Master data")
    public void uploadFullAddressData() {
        ArifactsProvider artifacts = context.getData().getArtifactsProvider();
        SFTPHelper.SftpConfig.ENVConfigEntry sftConfig = SFTPHelper.getSftpConfigMap().get(TestSettings.get().getProjectName());

        initTbb.uploadAddressDataSftp(artifacts.get(ArtifactCase.FullAddressData.name()), sftConfig);
    }

    @Test(description = "DIGIHUB-21255. [TMI] KLS update by delta import", dependsOnMethods = "uploadFullAddressData")
    @TmsLink("DIGIHUB-21255")
    @Description("[TMI] KLS update by delta import")
    public void uploadDeltaAddressData() {
        ArifactsProvider artifacts = context.getData().getArtifactsProvider();
        SFTPHelper.SftpConfig.ENVConfigEntry sftConfig = SFTPHelper.getSftpConfigMap().get(TestSettings.get().getProjectName());

        initTbb.uploadAddressDataSftp(artifacts.get(ArtifactCase.DeltaAddressData.name()), sftConfig);
    }

    @Test(dependsOnMethods = "uploadDeltaAddressData", description = "DIGIHUB-17909. [TMI] Create Supplier in IBT manually")
    @TmsLink("DIGIHUB-17909")
    @Description("[TMI] Create Supplier in IBT manually")
    public void manualSupplierCreation() {
        Supplier supplierData = context.getData().getSupplierDataProvider().get(SupplierCase.DefaultSupplier);

        rollTbb.createSupplier(supplierData);
        rollTbb.checkSupplier(supplierData);
    }*/

    @Test(/*dependsOnMethods = "manualSupplierCreation", */description = "DIGIHUB-26439. [TMI]: Automatical OLT Commissioning (New UI)")
    @TmsLink("DIGIHUB-26439")
    @Description("[TMI]: Automatical OLT Commissioning (New UI)")
    public void oltCommissioning() {
        OltDevice oltData = OsrTestContext.get().getData().getOltDeviceDataProvider().get(OltDeviceCase.FSZ_76H1);
        Nvt nvtData = OsrTestContext.get().getData().getNvtDataProvider().get(NvtCase.NO_DATA_PRESENT);

        oltTbb.newOltCommissioning(oltData, nvtData);
    }
}
