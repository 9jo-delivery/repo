package com.sparta.delivery.domain.region.service;

import com.sparta.delivery.domain.region.dto.request.RegionRequestDto;
import com.sparta.delivery.domain.region.dto.request.RegionSearchDto;
import com.sparta.delivery.domain.region.dto.request.RegionUpdateRequestDto;
import com.sparta.delivery.domain.region.dto.response.RegionResponseDto;
import com.sparta.delivery.domain.region.dto.response.RegionSummaryResponseDto;
import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.region.repository.RegionRepository;
import com.sparta.delivery.global.common.Enums;
import com.sparta.delivery.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;

    @Transactional
    public RegionResponseDto createRegion(RegionRequestDto regionRequestDto) {

        Region parentRegion = null;
        if(regionRequestDto.getParentRegionId() != null){
            parentRegion = regionRepository.findById(regionRequestDto.getParentRegionId()).orElseThrow(()
            -> new ResourceNotFoundException("선택한 상위 지역이 존재하지 않습니다."));
        }

        Enums.RegionType currentType = regionRequestDto.getRegionType();

        if(currentType == Enums.RegionType.SIDO){
            if(parentRegion != null){
                throw new IllegalStateException("시/도는 최상위 지역입니다. 지역 선택을 해제해 주세요.");
            }
        }else if(currentType == Enums.RegionType.SIGUNGU){
            if(parentRegion == null || parentRegion.getRegionType() != Enums.RegionType.SIDO){
                throw new IllegalStateException("시/군/구를 등록하려면 먼저 시/도를 반드시 선택하셔야 합니다.");
            }
        }else if(currentType == Enums.RegionType.DONG){
            if(parentRegion == null || parentRegion.getRegionType() != Enums.RegionType.SIGUNGU){
                throw new IllegalStateException("읍/면/동을 등록하려면 먼저 시/군/구를 반드시 선택하셔야 합니다.");
            }
        }

        Region region = Region.create(
                parentRegion,
                regionRequestDto.getName(),
                regionRequestDto.getRegionType(),
                regionRequestDto.getIsServiceAvailable()
        );

        Region savedRegion = regionRepository.save(region);

        return RegionResponseDto.from(savedRegion);
    }

    @Transactional(readOnly = true)
    public Page<RegionSummaryResponseDto> findAllRegions(RegionSearchDto regionSearchDto, Pageable pageable) {

        Page<Region> regionPage = regionRepository.searchRegions(
                regionSearchDto.getParentRegionId(),
                regionSearchDto.getName(),
                regionSearchDto.getRegionType(),
                regionSearchDto.getIsServiceAvailable(),
                pageable
        );

        return regionPage.map(RegionSummaryResponseDto::from);
    }

    @Transactional(readOnly = true)
    public RegionSummaryResponseDto findRegionById(UUID regionId) {
        Region region = regionRepository.findById(regionId).orElseThrow(()
                -> new ResourceNotFoundException("해당 지역이 존재하지 않습니다."));

        return RegionSummaryResponseDto.from(region);
    }

    @Transactional
    public RegionSummaryResponseDto updateRegion(UUID regionId, RegionUpdateRequestDto regionUpdateRequestDto) {

        Region region = regionRepository.findById(regionId).orElseThrow(
                ()-> new ResourceNotFoundException("해당 지역이 존재하지 않습니다."));

        region.update(regionUpdateRequestDto.getName(),
                regionUpdateRequestDto.getIsServiceAvailable());

        return RegionSummaryResponseDto.from(region);
    }
    @Transactional
    public void deleteRegion(UUID regionId, Long userId) {

        Region region = regionRepository.findById(regionId).orElseThrow(
                ()-> new ResourceNotFoundException("해당 지역이 존재하지 않습니다."));

        if(regionRepository.existsByParentRegionIdAndIsDeletedFalse(regionId)){
            throw new IllegalStateException("하위 지역이 존재하여 삭제할 수 없습니다.");
        }

        region.markAsDeleted(userId);
    }
}
