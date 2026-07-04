package com.sparta.delivery.domain.region;

import com.sparta.delivery.domain.region.dto.RegionRequestDto;
import com.sparta.delivery.domain.region.dto.RegionResponseDto;
import com.sparta.delivery.domain.region.dto.RegionSearchDto;
import com.sparta.delivery.domain.region.dto.RegionSummaryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/regions")
public class RegionController {
    private final RegionService regionService;

    @PostMapping // 추후 권한 추가
    public RegionResponseDto createRegion(@RequestBody RegionRequestDto regionRequestDto) {
        return regionService.createRegion(regionRequestDto);
    }

    @GetMapping
    public Page<RegionSummaryResponseDto> findAllRegions(RegionSearchDto regionSearchDto) {
        return regionService.findAllRegions(regionSearchDto);
    }



}
