package com.tsystems.tm.acc.ta.data.morpheus.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.external.v4_2_0.client.model.*;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

public class OltResourceInventoryMapper {
    public Device getDevice(Device.LifeCycleStateEnum deviceState, Port.LifeCycleStateEnum portState) {
        return new Device()
                .shortName("MABC VB")
                .materialNumber("40247069")
                .emsNbiName("MA5600T")
                .tkz1("02351082")
                .tkz2(null)
                .descriptionUrl("https://edmpc3.one-erp.telekom.de/sap(bD1kZSZjPTAzMA==)/bc/bsp/sap/zmnetkat/material.htm?matnr=40247069")
                .lastDiscovery(OffsetDateTime.now())
                .id(1003L)
                .resourceId("1003")
                .deviceName("ETSI Service Shelf H8-MABC")
                .endSz("{{request.requestLine.query.endsz}}")
                .fachSz(null)
                .klsId(16063944L)
                .ipAddress("10.182.112.196")
                .firmwareVersion(null)
                .resourceState(Device.ResourceStateEnum.NON_WORKING)
                .communicationState(Device.CommunicationStateEnum.AVAILABLE)
                .serialNumber("12345")
                .importState(Device.ImportStateEnum.PLANNED)
                .type(Device.TypeEnum.DPU)
                .importDate(OffsetDateTime.now())
                .description("string")
                .asb(0L)
                .technicalLocation("string")
                .equipmentNumber("string")
                .manufacturer("string")
                .distributionPointId("123456789")
                .lifeCycleState(deviceState)
                .compositePartyId(0L)
                .equipmentHolders(getEquipmentHolders())
                .ports(Collections.singletonList(getPort(portState)));
    }

    public Port getPort(Port.LifeCycleStateEnum portState) {
        return new Port()
                .id(1049L)
                .resourceId("1049")
                .lifeCycleState(portState)
                .portType(Port.PortTypeEnum.PON)
                .portNumber("1")
                .opticalModule(new OpticalModule()
                        .shortName("GPPD")
                        .materialNumber("40261742")
                        .emsNbiName("H805GPBD")
                        .tkz1("03021BQW")
                        .tkz2("string")
                        .descriptionUrl("https://edmpc3.one-erp.telekom.de/sap(bD1kZSZjPTAzMA==)/bc/bsp/sap/zmnetkat/material.htm?matnr=40261742")
                        .lastDiscovery(OffsetDateTime.now())
                        .id(0L)
                        .resourceId("1049")
                        .name("8-port GPON OLT Interface Board V2")
                        .description("string")
                        .manufacturer("string")
                        .compositePartyId(0L)
                )
                .compositePartyId(0L);
    }

    private List<EquipmentHolder> getEquipmentHolders() {
        return Collections.singletonList(new EquipmentHolder()
                .id(1011L)
                .slotNumber("4")
                .card(new Card()
                        .shortName("GPPD")
                        .materialNumber("40261742")
                        .emsNbiName("H805GPBD")
                        .tkz1("03021BQW")
                        .tkz2(null)
                        .descriptionUrl("https://edmpc3.one-erp.telekom.de/sap(bD1kZSZjPTAzMA==)/bc/bsp/sap/zmnetkat/material.htm?matnr=40261742")
                        .lastDiscovery(OffsetDateTime.now())
                        .id(1011L)
                        .resourceId("1011")
                        .name("8-port GPON OLT Interface Board V2")
                        .description(null)
                        .serialNumber("021BQW10B6000065")
                        .serviceState(null)
                        .resourceState("INSTALLING_INSTALLED")
                        .firmwareVersion("507(2015-8-27)")
                        .cardType(Card.CardTypeEnum.GPON)
                        .equipmentNumber("40261742")
                        .manufacturer(null)
                        .ports(Collections.singletonList(new Port()
                                        .id(1049L)
                                        .resourceId("1049")
                                        .portType(Port.PortTypeEnum.PON)
                                        .portNumber("1")
                                        .opticalModule(new OpticalModule()
                                                .shortName("GPPD")
                                                .materialNumber("40261742")
                                                .emsNbiName("H805GPBD")
                                                .tkz1("03021BQW")
                                                .tkz2(null)
                                                .descriptionUrl("https://edmpc3.one-erp.telekom.de/sap(bD1kZSZjPTAzMA==)/bc/bsp/sap/zmnetkat/material.htm?matnr=40261742")
                                                .lastDiscovery(OffsetDateTime.now())
                                                .id(0L)
                                                .resourceId("1049")
                                                .name("8-port GPON OLT Interface Board V2")
                                                .description(null)
                                                .manufacturer(null)
                                                .compositePartyId(0L)
                                        )
                                        .compositePartyId(0L)
                                )
                        )
                        .compositePartyId(0L)
                )
        );
    }

    public DpuPonConnectionDto getDpuPonConnection(OltDevice oltDevice, Dpu dpu) {
        return new DpuPonConnectionDto()
                .id(1049L)
                .oltPonPortEndsz(oltDevice.getEndsz())
                .oltPonPortNumber(oltDevice.getOltPort())
                .oltPonSlotNumber(oltDevice.getOltSlot())
                .dpuPonPortEndsz("{{request.requestLine.query.dpuPonPortEndsz}}")
                .dpuPonPortNumber("{{request.requestLine.query.dpuPonPortNumber}}");
    }

    public UplinkDTO getEthernetLink(OltDevice oltDevice, Dpu dpu) {
        return new UplinkDTO()
                .id(1049L)
                .oltEndSz("{{request.requestLine.query.oltEndSz}}")
                .oltSlot(oltDevice.getOltSlot())
                .oltPortNumber(oltDevice.getOltPort())
                .bngEndSz("49/30/179/43G1")
                .bngSlot("1")
                .bngPortNumber("ge-1/2/3")
                .ancpSessions(Collections.singletonList(
                        new ANCPSession()
                                .id(1215L)
                                .vlan(16)
                                .partitionId(2)
                                .sessionId(2)
                                .sessionType(ANCPSession.SessionTypeEnum.OLT)
                                .rmkEndpointId("1187037139")
                                .sealConfigurationId("0-8-1-2-16")
                                .ipSubnet(new IPSubnet()
                                        .id(1215L)
                                        .subnetMask("/30")
                                        .ipAddressLoopback("10.150.240.100")
                                        .ipAddressBng("10.150.240.102")
                                        .ipAddressBroadcast("10.150.240.103")
                                        .rmkAccessId("1187037139")
                                        .allocatedIPAddresses(Collections.singletonList(
                                                new AllocatedIPAddresses()
                                                        .id(1215L)
                                                        .ipAddress("10.150.240.101")

                                        ))
                                )
                                .sessionStatus(ANCPSession.SessionStatusEnum.ACTIVE)
                                .endsz("{{request.requestLine.query.endsz}}")
                                .allocatedIPAddress(
                                        new AllocatedIPAddresses()
                                                .id(1215L)
                                                .ipAddress("10.150.240.101")
                                )
                                .configurationStep(ANCPSession.ConfigurationStepEnum.CREATE_IP_RANGE_ASSIGNMENT)
                                .additionalInfo("0")
                                .lastActivity(OffsetDateTime.now())
                                .port(new Port()
                                        .id(6537L)
                                        .resourceId("e346b861-7a9e-45d2-a96c-2f29b12b33b3")
                                        .lifeCycleState(Port.LifeCycleStateEnum.NOT_OPERATING)
                                        .portType(Port.PortTypeEnum.PON)
                                        .portNumber("1")
                                        .opticalModule(new OpticalModule()
                                                .shortName("GPPD")
                                                .materialNumber("40261742")
                                                .emsNbiName("H805GPBD")
                                                .tkz1("03021BQW")
                                                .tkz2(null)
                                                .descriptionUrl("https://edmpc3.one-erp.telekom.de/sap(bD1kZSZjPTAzMA==)/bc/bsp/sap/zmnetkat/material.htm?matnr=40261742")
                                                .lastDiscovery(OffsetDateTime.now())
                                                .id(0L)
                                                .resourceId("1049")
                                                .name("8-port GPON OLT Interface Board V2")
                                                .description(null)
                                                .manufacturer(null)
                                                .compositePartyId(0L)
                                        )
                                        .compositePartyId(10001L)
                                )
                        )
                )
                .lsz(UplinkDTO.LszEnum._4C1)
                .orderNumber(10)
                .uewegId(null)
                .pluralId(null)
                .versionId(null)
                .version(null)
                .ipStatus(UplinkDTO.IpStatusEnum.ACTIVE)
                .ancpStatus(null)
                .activeAncpSession(true)
                .ipManagedAddress("172.22.53.4")
                .compositePartyId(10001L);
    }

    public AncpSessionDto getAncpSessionDto(AncpSessionDto.SessionTypeEnum sessionType, Dpu dpu, OltDevice olt) {
                return new AncpSessionDto()
                .id(99990L)
                .vlan(7)
                .sessionId(98765)
                .sessionType(sessionType)
                .rmkEndpointId("rmk12345")
                .sealConfigurationId("seal12345")
                .partitionId(123)
                .ipSubnet(new IpSubnetDto()
                        .id(0L)
                        .subnetMask("24")
                        .ipAddressLoopback("10.40.120.4")
                        .ipAddressBng("10.40.120.3")
                        .ipAddressBroadcast("10.40.120.5")
                        .rmkAccessId("rmk6789")
                        .allocatedIPAddresses(Collections.singletonList(new AllocatedIPAddressesDto()
                                .id(99992L)
                                .ipAddress("10.40.120.2"))
                        )
                )
                .sessionStatus(AncpSessionDto.SessionStatusEnum.ACTIVE)
                .endsz(sessionType == AncpSessionDto.SessionTypeEnum.DPU ? dpu.getEndSz():olt.getEndsz())
                .allocatedIPAddress(new AllocatedIPAddressesDto()
                        .id(99993L)
                        .ipAddress("10.40.120.1")
                )
                .configurationStep(AncpSessionDto.ConfigurationStepEnum.CREATE_IP_RANGE_ASSIGNMENT)
                .additionalInfo("string")
                .lastActivity(OffsetDateTime.now())
                .slotNumber("1")
                .portNumber("2");
    }

    public DpuAtOltConfigurationDto getDpuAtOltConfigurationDto(boolean valuesInRequest, OltDevice olt, Dpu dpu) {
        if (valuesInRequest) {
            return new DpuAtOltConfigurationDto()
                    .id(12345L)
                    .dpuEndsz("{{jsonPath request.body '$.dpuEndsz'}}")
                    .backhaulId("{{jsonPath request.body '$.backhaulId'}}")
                    .onuId(12345)
                    .configurationState("{{jsonPath request.body '$.configurationState'}}")
                    .serialNumber("{{jsonPath request.body '$.serialNumber'}}")
                    .oltEndsz("{{jsonPath request.body '$.oltEndsz'}}")
                    .oltPonSlot("{{jsonPath request.body '$.oltPonSlot'}}")
                    .oltPonPort("{{jsonPath request.body '$.oltPonPort'}}")
                    .oltUplinkSlot("{{jsonPath request.body '$.oltUplinkSlot'}}")
                    .oltUplinkPort("{{jsonPath request.body '$.oltUplinkPort'}}");
        } else {
            return new DpuAtOltConfigurationDto()
                    .id(12345L)
                    .dpuEndsz(dpu.getEndSz())
                    .backhaulId("backhaulId01")
                    .onuId(12345)
                    .configurationState("ACTIVE")
                    .serialNumber("48AB541118CC5191")
                    .oltEndsz(olt.getEndsz())
                    .oltPonSlot(olt.getOltSlot())
                    .oltPonPort(olt.getOltPort())
                    .oltUplinkSlot(olt.getUplinkSlot())
                    .oltUplinkPort(olt.getUplinkPort());
        }
    }

    public DpuEmsConfigurationDto getDpuEmsConfigurationDto(boolean valuesInRequest) {
        if (valuesInRequest) {
            return new DpuEmsConfigurationDto()
                    .id(12345L)
                    .ancpBngIpAddress("{{jsonPath request.body '$.ancpBngIpAddress'}}")
                    .ancpIpAddressSubnetMask("{{jsonPath request.body '$.ancpIpAddressSubnetMask'}}")
                    .ancpOwnIpAddress("{{jsonPath request.body '$.ancpOwnIpAddress'}}")
                    .backhaulId("{{jsonPath request.body '$.backhaulId'}}")
                    .configurationState("{{jsonPath request.body '$.configurationState'}}")
                    .emsNbiName("{{jsonPath request.body '$.emsNbiName'}}")
                    .dpuEndsz("{{jsonPath request.body '$.dpuEndsz'}}")
                    .managementDomain("{{jsonPath request.body '$.managementDomain'}}")
                    .serialNumber("{{jsonPath request.body '$.serialNumber'}}");
        } else {
            return new DpuEmsConfigurationDto()
                    .id(12345L)
                    .ancpBngIpAddress("10.40.120.1")
                    .ancpIpAddressSubnetMask("24")
                    .ancpOwnIpAddress("10.40.120.1")
                    .backhaulId("backhaulId01")
                    .configurationState("ACTIVE")
                    .emsNbiName("H805GPBD")
                    .dpuEndsz("{{request.requestLine.query.dpuEndsz}}")
                    .managementDomain("49_30_179_43G1")
                    .serialNumber("48AB541118CC5191");
        }
    }
}
