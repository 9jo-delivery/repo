package com.sparta.delivery.domain.region.controller;

import com.sparta.delivery.domain.region.dto.RegionRequestDto;
import com.sparta.delivery.domain.region.dto.RegionResponseDto;
import com.sparta.delivery.domain.region.dto.RegionSearchDto;
import com.sparta.delivery.domain.region.dto.RegionSummaryResponseDto;
import com.sparta.delivery.domain.region.service.RegionService;
import com.sparta.delivery.global.config.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/regions")
public class RegionController {
    private final RegionService regionService;

    @PostMapping
    public RegionResponseDto createRegion(@RequestBody RegionRequestDto regionRequestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();
        return regionService.createRegion(regionRequestDto, userId);
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
    public RegionSummaryResponseDto updateRegion(@PathVariable UUID regionId, @RequestBody RegionRequestDto regionRequestDto,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        return regionService.updateRegion(regionId, regionRequestDto, userId);
    }

    @DeleteMapping("/{regionId}")
    public ResponseEntity<Void> deleteRegion(@PathVariable UUID regionId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        regionService.deleteRegion(regionId, userId);

        return ResponseEntity.noContent().build();
    }
}
