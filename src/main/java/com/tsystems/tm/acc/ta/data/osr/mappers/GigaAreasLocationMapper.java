package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.tests.osr.area.data.management.external.client.model.FibreOnLocationV2DTO;
import com.tsystems.tm.acc.tests.osr.area.data.management.external.client.model.PageFibreOnLocationV2DTO;

import java.time.OffsetDateTime;
import java.util.Collections;

public class GigaAreasLocationMapper {

  public PageFibreOnLocationV2DTO getPageGigaAreaV2DTO(DpuDevice dpuDevice) {

      PageFibreOnLocationV2DTO pageFibreOnLocationV2DTO = new PageFibreOnLocationV2DTO();
      FibreOnLocationIdV2DTO fibreOnLocationIdV2DTO = new FibreOnLocationIdV2DTO();

      fibreOnLocationIdV2DTO.setCreationDate(OffsetDateTime.now());
      fibreOnLocationIdV2DTO.setModificationDate(OffsetDateTime.now());
      fibreOnLocationIdV2DTO.setId(dpuDevice.getFiberOnLocationId());
      fibreOnLocationIdV2DTO.setKlsId(Long.getLong(dpuDevice.getKlsId()));
      fibreOnLocationIdV2DTO.setGe( Integer.getInteger(dpuDevice.getPonConnectionGe()));
      fibreOnLocationIdV2DTO.setWe( Integer.getInteger(dpuDevice.getPonConnectionWe()));
      fibreOnLocationIdV2DTO.setDistributionPointName("C");
      fibreOnLocationIdV2DTO.setInstallationStatus(FibreOnLocationV2DTO.InstallationStatusEnum.CONNECTED);

      pageFibreOnLocationV2DTO.setContent(Collections.singletonList(fibreOnLocationIdV2DTO));
      return pageFibreOnLocationV2DTO;

  }
}
