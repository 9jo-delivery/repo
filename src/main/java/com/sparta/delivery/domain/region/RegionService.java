package com.sparta.delivery.domain.region;

import com.sparta.delivery.domain.region.dto.RegionRequestDto;
import com.sparta.delivery.domain.region.dto.RegionResponseDto;
import com.sparta.delivery.domain.region.dto.RegionSearchDto;
import com.sparta.delivery.domain.region.dto.RegionSummaryResponseDto;
import com.sparta.delivery.global.common.Enums;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;

    @Transactional
    public RegionResponseDto createRegion(RegionRequestDto regionRequestDto) {
        Region parentRegion = null;
        if(regionRequestDto.getParentRegionId() != null){
            parentRegion = regionRepository.findById(regionRequestDto.getParentRegionId()).orElseThrow(()
            -> new IllegalArgumentException("선택한 상위 지역이 존재하지 않습니다."));
        }

        Enums.RegionType currentType = regionRequestDto.getRegionType();

        if(currentType == Enums.RegionType.SIDO){
            if(parentRegion != null){
                throw new IllegalArgumentException("시/도(SIDO)는 최상위 지역입니다. 지역 선택을 해제해 주세요.");
            }
        }else if(currentType == Enums.RegionType.SIGUNGU){
            if(parentRegion == null || parentRegion.getRegionType() != Enums.RegionType.SIDO){
                throw new IllegalArgumentException("시/군/구를 등록하려면 먼저 시/도를 반드시 선택하셔야 합니다.");
            }
        }else if(currentType == Enums.RegionType.DONG){
            if(parentRegion == null || parentRegion.getRegionType() != Enums.RegionType.SIGUNGU){
                throw new IllegalArgumentException("읍/면/동을 등록하려면 먼저 시/군/구를 반드시 선택하셔야 합니다.");
            }
        }

        Region region = Region.builder()
                .parentRegion(parentRegion)
                .name(regionRequestDto.getName())
                .regionType(currentType)
                .isServiceAvailable(regionRequestDto.isServiceAvailable())
                .build();

        Region savedRegion = regionRepository.save(region);

        // [4단계] 결과를 응답 가방에 이쁘게 담아서 돌려주기
        return RegionResponseDto.builder()
                .id(savedRegion.getId())
                .name(savedRegion.getName())
                .regionType(savedRegion.getRegionType())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<RegionSummaryResponseDto> findAllRegions(RegionSearchDto regionSearchDto) {

        int validatedSize = regionSearchDto.getSize();
        if(validatedSize != 10 && validatedSize != 30 && validatedSize != 50){
            validatedSize = 10;
        }

        Sort sort = Sort.by("createdAt").descending();

        Pageable pageable = PageRequest.of(regionSearchDto.getPage(), validatedSize, sort);

        Page<Region> regionPage = regionRepository.searchRegions(
                regionSearchDto.getParentRegionId(),
                regionSearchDto.getName(),
                regionSearchDto.getRegionType(),
                regionSearchDto.getIsServiceAvailable(),
                pageable
        );

        return regionPage.map(region -> RegionSummaryResponseDto.builder()
                .id(region.getId())
                .name(region.getName())
                .regionType(region.getRegionType())
                .isServiceAvailable(region.isServiceAvailable())
                .parentRegionId(region.getParentRegion() != null ? region.getParentRegion().getId() : null)
                .build());
    }
}
