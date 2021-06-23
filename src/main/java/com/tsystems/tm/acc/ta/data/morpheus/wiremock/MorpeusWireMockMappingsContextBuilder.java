package com.tsystems.tm.acc.ta.data.morpheus.wiremock;

import com.tsystems.tm.acc.ta.data.morpheus.wiremock.mappings.*;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
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
        addGetDpuPonConnStub(dpu, olt, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, true);
        addPostAncpConfStub(dpu, true, true);
        addGetDPUAncpStub(olt, dpu, true);
        addGetOLTAncpStub(olt, dpu);
        addGetDpuAtOltConfigStub(dpu, olt, false,false);
        addPostDpuAtOltConfigStub(dpu, olt);
        addDpuConfigurationTaskStub(dpu, true);
        addPutDpuAtOltConfigStub(dpu, olt);
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
        addGetDpuPonConnStub(dpu, olt, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, true);
        addPostAncpConfStub(dpu, true, true);
        addGetDPUAncpStub(olt, dpu, true);
        addGetOLTAncpStub(olt, dpu);
        addGetDpuAtOltConfigStub(dpu,olt,  true,false);
        addPostDpuAtOltConfigStub(dpu, olt);
        addDpuConfigurationTaskStub(dpu, true);
        addPutDpuAtOltConfigStub(dpu, olt);
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
        addGetDpuPonConnStub(dpu, olt, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, true);
        addPostAncpConfStub(dpu, true, true);
        addGetDPUAncpStub(olt, dpu, true);
        addGetOLTAncpStub(olt, dpu);
        addGetDpuAtOltConfigStub(dpu, olt, false,false);
        addPostDpuAtOltConfigStub(dpu, olt);
        addDpuConfigurationTaskStub(dpu, true);
        addPutDpuAtOltConfigStub(dpu, olt);
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
        addGetDpuPonConnStub(dpu, olt, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForGetEthLink400(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(dpu, olt, true);
        addGetEthLinkStub(olt, dpu, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForGetOnuId400(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(dpu, olt, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForGetBackhaul400(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(dpu, olt, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForPostDeprovision400(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(dpu, olt, true);
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
        addGetDpuPonConnStub(dpu, olt, true);
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
        addGetDpuPonConnStub(dpu, olt, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, true);
        addPostAncpConfStub(dpu, true, true);
        addGetDPUAncpStub(olt, dpu, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForPostDeprovisionCallbackError(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(dpu, olt,true);
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
        addGetDpuPonConnStub(dpu, olt, true);
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
        addGetDpuPonConnStub(dpu, olt, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, true);
        addPostAncpConfStub(dpu, true, true);
        addGetDPUAncpStub(olt, dpu, true);
        addGetOLTAncpStub(olt, dpu);
        addGetDpuAtOltConfigStub(dpu, olt, false,false);
        addPostDpuAtOltConfigStub(dpu, olt);
        addDpuConfigurationTaskStub(dpu, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForPostSealDpuEmsConfigCallbackError(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(dpu, olt, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, true);
        addPostAncpConfStub(dpu, true, true);
        addGetDPUAncpStub(olt, dpu, true);
        addGetOLTAncpStub(olt, dpu);
        addGetDpuAtOltConfigStub(dpu, olt, false,false);
        addPostDpuAtOltConfigStub(dpu, olt);
        addDpuConfigurationTaskStub(dpu, true);
        addPutDpuAtOltConfigStub(dpu, olt);
        addGetDpuEmsConfigStub(dpu, false);
        addPostDpuEmsConfigStub(dpu);
        addSealPostDpuConfStub(dpu, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForPostDeviceProvisioningCallbackError(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnStub(dpu, olt, true);
        addGetEthLinkStub(olt, dpu, true);
        addGetOnuIdStub(dpu, true);
        addGetBackhaulIdStub(olt, dpu, true);
        addPostDeprovisionOltStub(olt, dpu, true, true);
        addPostAncpConfStub(dpu, true, true);
        addGetDPUAncpStub(olt, dpu, true);
        addGetOLTAncpStub(olt, dpu);
        addGetDpuAtOltConfigStub(dpu, olt, false,false);
        addPostDpuAtOltConfigStub(dpu, olt);
        addDpuConfigurationTaskStub(dpu, true);
        addPutDpuAtOltConfigStub(dpu, olt);
        addGetDpuEmsConfigStub(dpu, false);
        addPostDpuEmsConfigStub(dpu);
        addSealPostDpuConfStub(dpu, true);
        addPutDpuEmsConfigStub(dpu);
        addPostProvisioningDeviceStub(dpu, false);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }


    public MorpeusWireMockMappingsContextBuilder addAllForGetPonConnDiffPortsError(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addGetDpuPonConnDiffPortsStub(dpu, olt, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addDpuDecommissioningSuccess(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        addDeleteDeprovisioningDeviceStub(dpu, true);
        addGetDpuEmsConfigStub(dpu, true);
        addPutDpuEmsConfigStub(dpu);
        addSealPostDpuDeconfStub(dpu,true);
        addDeleteDpuEmsConfigStub();
        addGetDpuAtOltConfigStub(dpu, olt, true,false);
        addPutDpuAtOltConfigStub(dpu, olt);
        addSealPostDpuOltDeconfStub(dpu, true);
        addPostReleaseOnuIdTask(olt, true);
        addDeleteDpuOltConfigStub();
        addGetDPUAncpStub(olt, dpu, true);
        addDeleteAncpStub(true);
        addGetDpuAtOltConfigStub(dpu, olt, false,true);
        addGetDpuPonConnStub(dpu, olt, true);
        addPostPreprovisionFTTHStub(olt, dpu,true,true);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForPostSealDpuEmsDeconfigCallbackError(OltDevice olt, Dpu dpu){
        addGetDpuDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        addDeleteDeprovisioningDeviceStub(dpu, true);
        addGetDpuEmsConfigStub(dpu, true);
        addPutDpuEmsConfigStub(dpu);
        addSealPostDpuDeconfStub(dpu,false);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForPostSealDpuOltDeconfigCallbackError(OltDevice olt, Dpu dpu){
        addGetDpuDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        addDeleteDeprovisioningDeviceStub(dpu, true);
        addGetDpuEmsConfigStub(dpu, true);
        addPutDpuEmsConfigStub(dpu);
        addSealPostDpuDeconfStub(dpu,true);
        addDeleteDpuEmsConfigStub();
        addGetDpuAtOltConfigStub(dpu, olt, true,false);
        addPutDpuAtOltConfigStub(dpu, olt);
        addSealPostDpuOltDeconfStub(dpu, false);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addDpuDecommissioningDpuEmsConfigDoesntExist(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        addDeleteDeprovisioningDeviceStub(dpu, true);
        addGetDpuEmsConfigStub(dpu, false);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addDpuDecommissioningDpuOltConfigDoesntExist(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        addDeleteDeprovisioningDeviceStub(dpu, true);
        addGetDpuEmsConfigStub(dpu, false);
        addGetDpuAtOltConfigStub(dpu, olt, false,false);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addDpuDecommissioningReleaseOnuIdTask400(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        addDeleteDeprovisioningDeviceStub(dpu, true);
        addGetDpuEmsConfigStub(dpu, true);
        addPutDpuEmsConfigStub(dpu);
        addSealPostDpuDeconfStub(dpu,true);
        addDeleteDpuEmsConfigStub();
        addGetDpuAtOltConfigStub(dpu, olt, true,false);
        addPutDpuAtOltConfigStub(dpu, olt);
        addSealPostDpuOltDeconfStub(dpu, true);
        addDeleteDpuOltConfigStub();
        addPostReleaseOnuIdTask(olt, false);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addDpuDecommissioningAncpSessionDoesntExist(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        addDeleteDeprovisioningDeviceStub(dpu, true);
        addGetDpuEmsConfigStub(dpu, true);
        addPutDpuEmsConfigStub(dpu);
        addSealPostDpuDeconfStub(dpu,true);
        addDeleteDpuEmsConfigStub();
        addGetDpuAtOltConfigStub(dpu, olt, true,false);
        addPutDpuAtOltConfigStub(dpu, olt);
        addSealPostDpuOltDeconfStub(dpu, true);
        addPostReleaseOnuIdTask(olt, true);
        addDeleteDpuOltConfigStub();
        addGetDPUAncpStub404(dpu);
        addGetDpuAtOltConfigStub(dpu, olt, false,true);
        addGetDpuPonConnStub(dpu, olt, true);
        addPostPreprovisionFTTHStub(olt, dpu,true,true);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addDpuDecommissioningDeleteAncpErrorCallback(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        addDeleteDeprovisioningDeviceStub(dpu, true);
        addGetDpuEmsConfigStub(dpu, true);
        addPutDpuEmsConfigStub(dpu);
        addSealPostDpuDeconfStub(dpu,true);
        addDeleteDpuEmsConfigStub();
        addGetDpuAtOltConfigStub(dpu, olt, true,false);
        addPutDpuAtOltConfigStub(dpu, olt);
        addSealPostDpuOltDeconfStub(dpu, true);
        addPostReleaseOnuIdTask(olt, true);
        addDeleteDpuOltConfigStub();
        addGetDPUAncpStub(olt, dpu, true);
        addDeleteAncpStub(false);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForPostPreprovisionFTTHCallbackError(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        addDeleteDeprovisioningDeviceStub(dpu, true);
        addGetDpuEmsConfigStub(dpu, true);
        addPutDpuEmsConfigStub(dpu);
        addSealPostDpuDeconfStub(dpu,true);
        addDeleteDpuEmsConfigStub();
        addGetDpuAtOltConfigStub(dpu, olt, true,false);
        addPutDpuAtOltConfigStub(dpu, olt);
        addSealPostDpuOltDeconfStub(dpu, true);
        addDeleteDpuOltConfigStub();
        addPostReleaseOnuIdTask(olt, true);
        addDeleteDpuOltConfigStub();
        addGetDPUAncpStub(olt, dpu, true);
        addDeleteAncpStub(true);
        addGetDpuAtOltConfigStub(dpu, olt, false, true);
        addGetDpuPonConnStub(dpu, olt,true);
        addPostPreprovisionFTTHStub(olt, dpu,true,false);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForPostPreprovisionFTTHDPUisAlreadyKnown(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        addDeleteDeprovisioningDeviceStub(dpu, true);
        addGetDpuEmsConfigStub(dpu, true);
        addPutDpuEmsConfigStub(dpu);
        addSealPostDpuDeconfStub(dpu,true);
        addDeleteDpuEmsConfigStub();
        addGetDpuAtOltConfigStub(dpu, olt, true,false);
        addPutDpuAtOltConfigStub(dpu, olt);
        addSealPostDpuOltDeconfStub(dpu, true);
        addDeleteDpuOltConfigStub();
        addPostReleaseOnuIdTask(olt, true);
        addDeleteDpuOltConfigStub();
        addGetDPUAncpStub(olt, dpu, true);
        addDeleteAncpStub(true);
        addGetDpuAtOltConfigStub(dpu, olt, true, true);
        addGetDpuPonConnStub(dpu, olt, true);
        addPostPreprovisionFTTHStub(olt, dpu,true,false);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForPostPreprovisionFTTHAnotherDPUKnown(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        addDeleteDeprovisioningDeviceStub(dpu, true);
        addGetDpuEmsConfigStub(dpu, true);
        addPutDpuEmsConfigStub(dpu);
        addSealPostDpuDeconfStub(dpu,true);
        addDeleteDpuEmsConfigStub();
        addGetDpuAtOltConfigStub(dpu, olt, true,false);
        addPutDpuAtOltConfigStub(dpu, olt);
        addSealPostDpuOltDeconfStub(dpu, true);
        addDeleteDpuOltConfigStub();
        addPostReleaseOnuIdTask(olt, true);
        addDeleteDpuOltConfigStub();
        addGetDPUAncpStub(olt, dpu, true);
        addDeleteAncpStub(true);
        addGetDpuAtOltConfigAnotherDPUStub(dpu, olt);
        addGetDpuPonConnStub(dpu, olt, true);
        addPostPreprovisionFTTHStub(olt, dpu,true,true);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addMocksForDomain(DpuDevice dpu) {
        context.add(new SealStub().postDomainDpuDpuConfiguration202(dpu));
        context.add(new SealStub().postDomainOltDpuConfiguration202(dpu));
        context.add(new PonInventoryStub().getLlcForDomain200(dpu));
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForDecomGetPonPortDiffSlotError(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        addDeleteDeprovisioningDeviceStub(dpu, true);
        addGetDpuEmsConfigStub(dpu, true);
        addPutDpuEmsConfigStub(dpu);
        addSealPostDpuDeconfStub(dpu,true);
        addDeleteDpuEmsConfigStub();
        addGetDpuAtOltConfigStub(dpu, olt, true,false);
        addPutDpuAtOltConfigStub(dpu, olt);
        addSealPostDpuOltDeconfStub(dpu, true);
        addPostReleaseOnuIdTask(olt, true);
        addDeleteDpuOltConfigStub();
        addGetDPUAncpStub(olt, dpu, true);
        addDeleteAncpStub(true);
        addGetDpuAtOltConfigStub(dpu, olt, false,true);
        addGetDpuPonConnDiffSlotsStub(dpu, olt, true);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addAllForDeleteDeviceDeprovisioningCallbackError(OltDevice olt, Dpu dpu) {
        addGetDpuDeviceStub(dpu, true);
        addPatchLifecycleStateDeviceStub(dpu);
        addPatchLifecycleStatePortStub(dpu);
        addDeleteDeprovisioningDeviceStub(dpu, false);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addWorkorderStub(){
        addGetWorkorderStub(true, "DPU_INSTALLATION");
        addPatchInProgressWorkorderStub(true);
        addPatchCreatedWorkorderStub(true);
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addOlRiStub(Dpu dpu){
        addGetDpuDeviceFolIdStub(dpu, true);
        addGetDpuDeviceMobileDpuBffEndsz(dpu, true);
        addPatchDpuDeviceMobileDpuBff(dpu, true);
        addPatchDpuPortMobileDpuBff(dpu, true);
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
    public MorpeusWireMockMappingsContextBuilder addGetDpuPonConnStub(Dpu dpu, OltDevice olt, boolean success) {
        if (success) {
            context.add(new PonInventoryStub().getLlc200(dpu, olt));
        } else {
            context.add(new PonInventoryStub().getLlc400(dpu, olt));
        }
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addGetDpuPonConnDiffPortsStub(Dpu dpu, OltDevice olt, boolean success) {
        if (success) {
            context.add(new PonInventoryStub().getLlcDiffPorts200(dpu, olt));
        } else {
            context.add(new PonInventoryStub().getLlc400(dpu, olt));
        }
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addGetDpuPonConnDiffSlotsStub(Dpu dpu, OltDevice olt, boolean success) {
        if (success) {
            context.add(new PonInventoryStub().getLlcDiffSlots200(dpu, olt));
        } else {
            context.add(new PonInventoryStub().getLlc400(dpu, olt));
        }
        return this;
    }

    // 3_OLT_RI_GET_EthernetLink.json
    public MorpeusWireMockMappingsContextBuilder addGetEthLinkStub(OltDevice olt, Dpu dpu, boolean success) {
        if (success) {
            context.add(new DeviceResourceInventoryStub().getEthernetLink200(olt));
        } else {
            context.add(new DeviceResourceInventoryStub().getEthernetLink400(olt));
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
                context.add(new WgAccessProvisioningStub().postPortDeprovisioning202(olt, dpu));
            } else {
                context.add(new WgAccessProvisioningStub().postPortDeprovisioning202CallbackError(olt, dpu));
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
                context.add(new AncpConfigurationStub().postCreateAncpConfiguration202(dpu));
            } else {
                context.add(new AncpConfigurationStub().postCreateAncpConfiguration202CallbackError(dpu));
            }
        } else {
            context.add(new AncpConfigurationStub().postCreateAncpConfiguration400(dpu));
        }
        return this;
    }

    // 8_OLT_RI_GET_DPUAncpSession.json
    public MorpeusWireMockMappingsContextBuilder addGetDPUAncpStub(OltDevice olt, Dpu dpu, boolean success) {
        if (success) {
            context.add(new DeviceResourceInventoryStub().getDpuAncpSession200(dpu));
        } else {
            context.add(new DeviceResourceInventoryStub().getDpuAncpSession400(dpu));
        }
        return this;
    }

    // 8_OLT_RI_GET_OLTAncpSession.json
    public MorpeusWireMockMappingsContextBuilder addGetOLTAncpStub(OltDevice olt, Dpu dpu) {
        context.add(new DeviceResourceInventoryStub().getOltAncpSession200(olt));
        return this;
    }

    // 9_OLT_RI_POST_DpuAtOltConf_GET.json
    public MorpeusWireMockMappingsContextBuilder addGetDpuAtOltConfigStub(Dpu dpu, OltDevice olt, boolean exist, boolean forOLT) {
        if(!forOLT) {
            if (exist) {
                context.add(new OltResourceInventoryStub().getDpuAtOltConfExist200(dpu, olt));
            } else {
                context.add(new OltResourceInventoryStub().getDpuAtOltConfNew200(dpu));
            }
            return this;
        } else {
            if (exist) {
                context.add(new OltResourceInventoryStub().getDpuAtOltConfOLTExist200(dpu, olt));
            } else {
                context.add(new OltResourceInventoryStub().getDpuAtOltConfOLTNew200(olt));
            }
            return this;
        }
    }

    // 9_OLT_RI_POST_DpuAtOltConf_GET.json
    public MorpeusWireMockMappingsContextBuilder addGetDpuAtOltConfigAnotherDPUStub(Dpu dpu, OltDevice olt) {
        context.add(new OltResourceInventoryStub().getDpuAtOltConfExistAnother200(dpu, olt));
        return this;
    }

    // 9_OLT_RI_POST_DpuAtOltConf_POST.json
    public MorpeusWireMockMappingsContextBuilder addPostDpuAtOltConfigStub(Dpu dpu, OltDevice olt) {
        context.add(new OltResourceInventoryStub().postDpuAtOltConf200(dpu, olt));
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
    public MorpeusWireMockMappingsContextBuilder addPutDpuAtOltConfigStub(Dpu dpu, OltDevice olt) {
        context.add(new OltResourceInventoryStub().putDpuAtOltConf200(dpu, olt));
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
    public MorpeusWireMockMappingsContextBuilder addDeleteDeprovisioningDeviceStub(Dpu dpu, boolean callbackSuccess) {
        if (callbackSuccess) {
            context.add(new WgFttbAccessProvisioningStub().deleteDeviceDeprovisioning202(dpu));
        } else {
            context.add(new WgFttbAccessProvisioningStub().deleteDeviceDeprovisioning202CallbackError(dpu));
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

    //8_Aid_DeleteDpuEmsConfiguration
    public MorpeusWireMockMappingsContextBuilder addDeleteDpuEmsConfigStub(){
        context.add(new OltResourceInventoryStub().deleteDpuEmsConf201());
        return this;
    }

    //7_Aid_releaseOnuId
    public MorpeusWireMockMappingsContextBuilder addPostReleaseOnuIdTask(OltDevice olt, boolean success){
        if (success) {
            context.add(new AccessLineManagementStub().postReleaseOnuIdTask200(olt));
        } else {
            context.add(new AccessLineManagementStub().postReleaseOnuIdTask400(olt));
        }
        return this;
    }

    // 9_Aid_deconfigureDpuOltConfing
    public MorpeusWireMockMappingsContextBuilder addSealPostDpuOltDeconfStub(Dpu dpu, boolean callbackSuccess) {
        if (callbackSuccess) {
            context.add(new SealStub().postDpuOltDeconfiguration202(dpu));
        } else {
            context.add(new SealStub().postDpuOltDeconfiguration202CallbackError(dpu));
        }
        return this;
    }

    //12_Aid_deleteDpuAtOltConfiguration
    public MorpeusWireMockMappingsContextBuilder addDeleteDpuOltConfigStub(){
        context.add(new OltResourceInventoryStub().deleteDpuOltConf201());
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addGetDPUAncpStubEmptyBody(Dpu dpu){
        context.add(new OltResourceInventoryStub().getDpuAncpSession200EmptyBody(dpu));
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addGetDPUAncpStub404(Dpu dpu){
        context.add(new OltResourceInventoryStub().getDpuAncpSession404(dpu));
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addDeleteAncpStub(boolean success){
        if(success) {
            context.add(new AncpConfigurationStub().deleteAncpConfiguration202());
        } else {
            context.add(new AncpConfigurationStub().deleteAncpConfiguration202CallbackError());
        }
        return this;
    }

    //13_Aid_deleteANCPSession
    //TODO

    //14_Activity_0or8som //task id should be changed
    public MorpeusWireMockMappingsContextBuilder addPostPreprovisionFTTHStub(OltDevice olt, Dpu dpu, boolean success, boolean callbackSuccess){
        if (success) {
            if (callbackSuccess) {
                context.add(new WgAccessProvisioningStub().postPortProvisioning202(olt, dpu));
            } else {
                context.add(new WgAccessProvisioningStub().postPortProvisioning202CallbackError(olt, dpu));
            }
        } else {
            context.add(new WgAccessProvisioningStub().postPortProvisioning400(olt, dpu));
        }
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addGetWorkorderStub(boolean success, String woType){
        if(success && woType=="DPU_INSTALLATION") {
            context.add(new WorkorderStub().getWorkorder200());
        }else if(success){
            context.add(new WorkorderStub().getWorkorderGF_AP_INSTALLATION());
        }else{
            context.add(new WorkorderStub().getWorkorder404());
        }
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addPatchInProgressWorkorderStub(boolean success){
        if (success) {
            context.add(new WorkorderStub().patchWorkorderInProgress200());
        }else{
            context.add(new WorkorderStub().patchWorkorderInProgress404());
        }
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addPatchCreatedWorkorderStub(boolean success){
        if (success) {
            context.add(new WorkorderStub().patchWorkorderCompleted200());
        }else{
            context.add(new WorkorderStub().patchWorkorderCompleted404());
        }
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addGetDpuDeviceFolIdStub(Dpu dpu, boolean success){
        if(success) {
            context.add(new OltResourceInventoryStub().getDpuDeviceFolId200(dpu));
        }else{
            context.add(new OltResourceInventoryStub().getDpuDeviceFolId400(dpu));
        }
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addGetDpuDeviceMobileDpuBffEndsz(Dpu dpu, boolean success){
        if(success) {
            context.add(new OltResourceInventoryStub().getDpuDeviceMobileDpuBffEndsz200(dpu));
        }else{
            context.add(new OltResourceInventoryStub().getDpuDevice400(dpu));
        }
        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addPatchDpuDeviceMobileDpuBff(Dpu dpu, boolean success){
        if(success) {
            context.add(new OltResourceInventoryStub().patchDpuDeviceMobileDpuBff200(dpu));
        } else {
            context.add(new OltResourceInventoryStub().patchDpuDeviceMobileDpuBff404());
        }
        return this;

    }

    public MorpeusWireMockMappingsContextBuilder addPatchDpuPortMobileDpuBff(Dpu dpu, boolean success){
        if(success){
            context.add(new OltResourceInventoryStub().patchDpuPortMobileDpuBff200(dpu));
        } else{
            context.add(new OltResourceInventoryStub().patchDpuPortMobileDpuBff404());
        }

        return this;
    }

    public MorpeusWireMockMappingsContextBuilder addStartDpuCommissioningMobileDpuBff(Dpu dpu, boolean success){
        if(success) {
            context.add(new OltResourceInventoryStub().postStartDpuComissioning200());
        } else {
            context.add(new OltResourceInventoryStub().postStartDpuComissioning500());
        }
        return this;

    }
}
