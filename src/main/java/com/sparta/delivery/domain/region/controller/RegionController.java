package com.sparta.delivery.domain.region.controller;

import com.sparta.delivery.domain.region.dto.RegionRequestDto;
import com.sparta.delivery.domain.region.dto.RegionResponseDto;
import com.sparta.delivery.domain.region.dto.RegionSearchDto;
import com.sparta.delivery.domain.region.dto.RegionSummaryResponseDto;
import com.sparta.delivery.domain.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @GetMapping("/{regionId}")
    public RegionSummaryResponseDto findRegionById(@PathVariable UUID regionId){
        return regionService.findRegionById(regionId);
    }

    @PatchMapping("/{regionId}")
    public RegionSummaryResponseDto updateRegion(@PathVariable UUID regionId, @RequestBody RegionRequestDto regionRequestDto) {
        return regionService.updateRegion(regionId, regionRequestDto);
    }

    @DeleteMapping("/{regionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content 강제
    public void deleteRegion(@PathVariable UUID regionId) {
        regionService.deleteRegion(regionId);
    }
}
