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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/regions")
public class RegionController {
    private final RegionService regionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
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
    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    public RegionSummaryResponseDto updateRegion(@PathVariable UUID regionId, @RequestBody RegionRequestDto regionRequestDto) {
        return regionService.updateRegion(regionId, regionRequestDto);
    }

    @DeleteMapping("/{regionId}")
    @PreAuthorize("hasAnyRole('MASTER')")
    public ResponseEntity<Void> deleteRegion(@PathVariable UUID regionId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        regionService.deleteRegion(regionId, userId);

        return ResponseEntity.noContent().build();
    }
}
