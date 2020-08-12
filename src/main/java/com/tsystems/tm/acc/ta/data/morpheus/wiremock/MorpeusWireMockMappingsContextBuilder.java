package com.tsystems.tm.acc.ta.data.morpheus.wiremock;

import com.tsystems.tm.acc.ta.data.morpheus.wiremock.mappings.*;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.SealStub;
import com.tsystems.tm.acc.ta.wiremock.ExtendedWireMock;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextBuilder;

public class MorpeusWireMockMappingsContextBuilder extends WireMockMappingsContextBuilder {
    public MorpeusWireMockMappingsContextBuilder(ExtendedWireMock wireMock) {
        super(wireMock);
    }

    public MorpeusWireMockMappingsContextBuilder(WireMockMappingsContext context) {
        super(context);
    }

    public MorpeusWireMockMappingsContextBuilder addAllSuccess(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(olt, dpu, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, true);
        addPostAncpConfStub(dpu, true, true);
        addGetDPUAncpStub(dpu, true);
        addGetOLTAncpStub(olt, dpu);
        addGetDpuAtOltConfigStub(dpu, false);
        addPostDpuAtOltConfigStub(dpu);
        addDpuConfigurationTaskStub(dpu, true);
        addPutDpuAtOltConfigStub(dpu);
        addGetDpuEmsConfigStub(dpu, false);
        addPostDpuEmsConfigStub(dpu);
        addSealPostDpuConfStub(dpu, true);
        addPutDpuEmsConfigStub(dpu);
        addPostProvisioningDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllSuccessWithDpuAtOltConfigurationExists(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(olt, dpu, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, true);
        addPostAncpConfStub(dpu, true, true);
        addGetDPUAncpStub(dpu, true);
        addGetOLTAncpStub(olt, dpu);
        addGetDpuAtOltConfigStub(dpu, true);
        addPostDpuAtOltConfigStub(dpu);
        addDpuConfigurationTaskStub(dpu, true);
        addPutDpuAtOltConfigStub(dpu);
        addGetDpuEmsConfigStub(dpu, false);
        addPostDpuEmsConfigStub(dpu);
        addSealPostDpuConfStub(dpu, true);
        addPutDpuEmsConfigStub(dpu);
        addPostProvisioningDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllSuccessWithDpuEmsConfigurationExists(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(olt, dpu, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, true);
        addPostAncpConfStub(dpu, true, true);
        addGetDPUAncpStub(dpu, true);
        addGetOLTAncpStub(olt, dpu);
        addGetDpuAtOltConfigStub(dpu, false);
        addPostDpuAtOltConfigStub(dpu);
        addDpuConfigurationTaskStub(dpu, true);
        addPutDpuAtOltConfigStub(dpu);
        addGetDpuEmsConfigStub(dpu, true);
        addPostDpuEmsConfigStub(dpu);
        addSealPostDpuConfStub(dpu, true);
        addPutDpuEmsConfigStub(dpu);
        addPostProvisioningDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForGetDevice400(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, false);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForDpuPonConn400(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(olt, dpu, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForGetEthLink400(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(olt, dpu, true);
        addGetEthLinkStub(olt, dpu, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForGetOnuId400(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(olt, dpu, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForGetBackhaul400(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(olt, dpu, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForPostDeprovision400(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(olt, dpu, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForConfigureAncp400(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(olt, dpu, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, true);
        addPostAncpConfStub(dpu, true, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForGetAncp400(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(olt, dpu, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, true);
        addPostAncpConfStub(dpu, true, true);
        addGetDPUAncpStub(dpu, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForPostDeprovisionCallbackError(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(olt, dpu, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForPostConfigureANCPCallbackError(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(olt, dpu, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, true);
        addPostAncpConfStub(dpu, true, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForPostSealDpuAtOltConfigCallbackError(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(olt, dpu, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, true);
        addPostAncpConfStub(dpu, true, true);
        addGetDPUAncpStub(dpu, true);
        addGetOLTAncpStub(olt, dpu);
        addGetDpuAtOltConfigStub(dpu, false);
        addPostDpuAtOltConfigStub(dpu);
        addDpuConfigurationTaskStub(dpu, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForPostSealDpuEmsConfigCallbackError(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(olt, dpu, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, true);
        addPostAncpConfStub(dpu, true, true);
        addGetDPUAncpStub(dpu, true);
        addGetOLTAncpStub(olt, dpu);
        addGetDpuAtOltConfigStub(dpu, false);
        addPostDpuAtOltConfigStub(dpu);
        addDpuConfigurationTaskStub(dpu, true);
        addPutDpuAtOltConfigStub(dpu);
        addGetDpuEmsConfigStub(dpu, false);
        addPostDpuEmsConfigStub(dpu);
        addSealPostDpuConfStub(dpu, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForPostDeviceProvisioningCallbackError(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(olt, dpu, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, true);
        addPostAncpConfStub(dpu, true, true);
        addGetDPUAncpStub(dpu, true);
        addGetOLTAncpStub(olt, dpu);
        addGetDpuAtOltConfigStub(dpu, false);
        addPostDpuAtOltConfigStub(dpu);
        addDpuConfigurationTaskStub(dpu, true);
        addPutDpuAtOltConfigStub(dpu);
        addGetDpuEmsConfigStub(dpu, false);
        addPostDpuEmsConfigStub(dpu);
        addSealPostDpuConfStub(dpu, true);
        addPutDpuEmsConfigStub(dpu);
        addPostProvisioningDeviceStub(dpu, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addDpuDecommissioningSuccess(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        addPostDeprovisioningDeviceStub(dpu, true);
        addPutDpuEmsConfigStub(dpu);
        addSealPostDpuDeconfStub(dpu,true);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForPostSealDpuEmsDeconfigCallbackError(OltDevice olt, Dpu dpu){
        addGetDpuDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        addPostDeprovisioningDeviceStub(dpu, true);
        addPutDpuEmsConfigStub(dpu);
        addSealPostDpuDeconfStub(dpu,false);
        return this;
    }

    // 1_OLT_RI_GET_DeviceDPU.json
    public MorpeusWireMockMappingsContextBuilder addGetDpuDeviceStub(Dpu dpu, boolean success) {
        if (success) {
            context.add(new OltResourceInventoryStub().getDpuDevice200(dpu));
        } else {
            context.add(new OltResourceInventoryStub().getDpuDevice400(dpu));
        }
        return this;
    }

    // 2_OLT_RI_GET_DpuPonConn.json
    public MorpeusWireMockMappingsContextBuilder addGetDpuPonConnStub(OltDevice olt, Dpu dpu, boolean success) {
        if (success) {
            context.add(new OltResourceInventoryStub().getDpuPonConnection200(olt, dpu));
        } else {
            context.add(new OltResourceInventoryStub().getDpuPonConnection400(olt, dpu));
        }
        return this;
    }

    // 3_OLT_RI_GET_EthernetLink.json
    public MorpeusWireMockMappingsContextBuilder addGetEthLinkStub(OltDevice olt, Dpu dpu, boolean success) {
        if (success) {
            context.add(new OltResourceInventoryStub().getEthernetLink200(olt, dpu));
        } else {
            context.add(new OltResourceInventoryStub().getEthernetLink400(olt, dpu));
        }
        return this;
    }

    // 4_AL_RI_POST_OnuId.json
    public MorpeusWireMockMappingsContextBuilder addGetOnuIdStub(Dpu dpu, boolean success) {
        if (success) {
            context.add(new AccessLineManagementStub().postAssignOnuIdTask200(dpu));
        } else {
            context.add(new AccessLineManagementStub().postAssignOnuIdTask400(dpu));
        }
        return this;
    }

    // 5_AL_RI_POST_Backhaul_id.json
    public MorpeusWireMockMappingsContextBuilder addGetBackhaulIdStub(OltDevice olt, Dpu dpu, boolean success) {
        if (success) {
            context.add(new AccessLineResourceInventoryStub().postBackhaulIdSearch200(olt, dpu));
        } else {
            context.add(new AccessLineResourceInventoryStub().postBackhaulIdSearch400(olt, dpu));
        }
        return this;
    }

    // 6_Wg_FTTH_AP_POST_DeprovisionOltPort.json
    public MorpeusWireMockMappingsContextBuilder addPostDeprovisionOltStub(OltDevice olt, Dpu dpu, boolean success, boolean callbackSuccess) {
        if (success) {
            if (callbackSuccess) {
                context.add(new WgAccessProvisioningStub().postPortDeprovisioning201(olt, dpu));
            } else {
                context.add(new WgAccessProvisioningStub().postPortDeprovisioning201CallbackError(olt, dpu));
            }
        } else {
            context.add(new WgAccessProvisioningStub().postPortDeprovisioning400(olt, dpu));
        }
        return this;
    }

    // 7_Ancp_Conf_POST_AncpConf.json
    public MorpeusWireMockMappingsContextBuilder addPostAncpConfStub(Dpu dpu, boolean success, boolean callbackSuccess) {
        if (success) {
            if (callbackSuccess) {
                context.add(new AncpConfigurationStub().postCreateAncpConfiguration200(dpu));
            } else {
                context.add(new AncpConfigurationStub().postCreateAncpConfiguration200CallbackError(dpu));
            }
        } else {
            context.add(new AncpConfigurationStub().postCreateAncpConfiguration400(dpu));
        }
        return this;
    }

    // 8_OLT_RI_GET_DPUAncpSession.json
    public MorpeusWireMockMappingsContextBuilder addGetDPUAncpStub(Dpu dpu, boolean success) {
        if (success) {
            context.add(new OltResourceInventoryStub().getDpuAncpSession200(dpu));
        } else {
            context.add(new OltResourceInventoryStub().getDpuAncpSession400(dpu));
        }
        return this;
    }

    // 8_OLT_RI_GET_OLTAncpSession.json
    public MorpeusWireMockMappingsContextBuilder addGetOLTAncpStub(OltDevice olt, Dpu dpu) {
        context.add(new OltResourceInventoryStub().getOltAncpSession200(olt, dpu));
        return this;
    }

    // 9_OLT_RI_POST_DpuAtOltConf_GET.json
    public MorpeusWireMockMappingsContextBuilder addGetDpuAtOltConfigStub(Dpu dpu, boolean exist) {
        if (exist) {
            context.add(new OltResourceInventoryStub().getDpuAtOltConfExist200(dpu));
        } else {
            context.add(new OltResourceInventoryStub().getDpuAtOltConfNew200(dpu));
        }
        return this;
    }

    // 9_OLT_RI_POST_DpuAtOltConf_POST.json
    public MorpeusWireMockMappingsContextBuilder addPostDpuAtOltConfigStub(Dpu dpu) {
        context.add(new OltResourceInventoryStub().postDpuAtOltConf200(dpu));
        return this;
    }

    // 10_SEAL_POST_DpuAtOltConf_OLT.json
    public MorpeusWireMockMappingsContextBuilder addDpuConfigurationTaskStub(Dpu dpu, boolean callbackSuccess) {
        if (callbackSuccess) {
            context.add(new SealStub().postOltDpuConfiguration202(dpu));
        } else {
            context.add(new SealStub().postOltDpuConfiguration202CallbackError(dpu));
        }
        return this;
    }

    // 11_OLT_RI_PUT_DpuAtOltConf.json
    public MorpeusWireMockMappingsContextBuilder addPutDpuAtOltConfigStub(Dpu dpu) {
        context.add(new OltResourceInventoryStub().putDpuAtOltConf200(dpu));
        return this;
    }

    // 12_OLT_RI_POST_DpuEmsConf_GET.json
    public MorpeusWireMockMappingsContextBuilder addGetDpuEmsConfigStub(Dpu dpu, boolean exist) {
        if (exist) {
            context.add(new OltResourceInventoryStub().getDpuEmsConfExist200(dpu));
        } else {
            context.add(new OltResourceInventoryStub().getDpuEmsConfNew200(dpu));
        }
        return this;
    }

    // 12_OLT_RI_POST_DpuEmsConf_POST.json
    public MorpeusWireMockMappingsContextBuilder addPostDpuEmsConfigStub(Dpu dpu) {
        context.add(new OltResourceInventoryStub().postDpuEmsConf200(dpu));
        return this;
    }

    // 13_SEAL_POST_DpuAtOltConf_DPU.json
    public MorpeusWireMockMappingsContextBuilder addSealPostDpuConfStub(Dpu dpu, boolean callbackSuccess) {
        if (callbackSuccess) {
            context.add(new SealStub().postDpuDpuConfiguration202(dpu));
        } else {
            context.add(new SealStub().postDpuDpuConfiguration202CallbackError(dpu));
        }
        return this;
    }

    // 14_OLT_RI_PUT_DpuEmsConf.json
    public MorpeusWireMockMappingsContextBuilder addPutDpuEmsConfigStub(Dpu dpu) {
        context.add(new OltResourceInventoryStub().putDpuEmsConf200(dpu));
        return this;
    }

    // 15_Wg_FTTB_AP_POST_ProvisioningDevice.json
    public MorpeusWireMockMappingsContextBuilder addPostProvisioningDeviceStub(Dpu dpu, boolean callbackSuccess) {
        if (callbackSuccess) {
            context.add(new WgFttbAccessProvisioningStub().postDeviceProvisioning202(dpu));
        } else {
            context.add(new WgFttbAccessProvisioningStub().postDeviceProvisioning202CallbackError(dpu));
        }
        return this;
    }

    // 16_OLT_RI_PATCH_LifecycleState_device.json
    public MorpeusWireMockMappingsContextBuilder addPatchLifecycleStateDeviceStub(Dpu dpu) {
        context.add(new OltResourceInventoryStub().patchDpuDevice200(dpu));
        return this;
    }

    // 17_OLT_RI_PATCH_LifecycleState_port.json
    public MorpeusWireMockMappingsContextBuilder addPatchLifecycleStatePortStub(Dpu dpu) {
        context.add(new OltResourceInventoryStub().patchDpuPort200(dpu));
        return this;
    }

    //4_Aid_DeprovisionFTTBaccessprovisioningDPU
    public MorpeusWireMockMappingsContextBuilder addPostDeprovisioningDeviceStub(Dpu dpu, boolean callbackSuccess) {
        if (callbackSuccess) {
            context.add(new WgFttbAccessProvisioningStub().postDeviceDeprovisioning202(dpu));
        } else {
            context.add(new WgFttbAccessProvisioningStub().postDeviceDeprovisioning202CallbackError(dpu));
        }
        return this;
    }

    // 6_Aid_deconfigureDpuEmsConfinEMS
    public MorpeusWireMockMappingsContextBuilder addSealPostDpuDeconfStub(Dpu dpu, boolean callbackSuccess) {
        if (callbackSuccess) {
            context.add(new SealStub().postDpuDpuDeconfiguration202(dpu));
        } else {
            context.add(new SealStub().postDpuDpuDeconfiguration202CallbackError(dpu));
        }
        return this;
    }
}
