package com.sparta.delivery.domain.region.controller;

import com.sparta.delivery.domain.region.dto.request.RegionRequestDto;
import com.sparta.delivery.domain.region.dto.request.RegionSearchDto;
import com.sparta.delivery.domain.region.dto.request.RegionUpdateRequestDto;
import com.sparta.delivery.domain.region.dto.response.RegionResponseDto;
import com.sparta.delivery.domain.region.dto.response.RegionSummaryResponseDto;
import com.sparta.delivery.domain.region.service.RegionService;
import com.sparta.delivery.global.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<RegionResponseDto> createRegion(@Valid @RequestBody RegionRequestDto regionRequestDto) {

        return ResponseEntity.ok(regionService.createRegion(regionRequestDto));
    }

    @GetMapping
    public ResponseEntity<Page<RegionSummaryResponseDto>> findAllRegions(
            RegionSearchDto regionSearchDto,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        int size = pageable.getPageSize();
        if(size != 10 && size != 30 && size != 50){
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    10,
                    pageable.getSort()
            );
        }
        return ResponseEntity.ok(regionService.findAllRegions(regionSearchDto, pageable));
    }

    @GetMapping("/{regionId}")
    public ResponseEntity<RegionSummaryResponseDto> findRegionById(@PathVariable UUID regionId){
        return ResponseEntity.ok(regionService.findRegionById(regionId));
    }

    @PatchMapping("/{regionId}")
    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    public ResponseEntity<RegionSummaryResponseDto> updateRegion(
            @PathVariable UUID regionId,
            @RequestBody RegionUpdateRequestDto regionUpdateRequestDto) {
        return ResponseEntity.ok(regionService.updateRegion(regionId, regionUpdateRequestDto));
    }

    @DeleteMapping("/{regionId}")
    @PreAuthorize("hasAnyRole('MASTER')")
    public ResponseEntity<Void> deleteRegion(@PathVariable UUID regionId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        regionService.deleteRegion(regionId, userId);

        return ResponseEntity.noContent().build();
    }
}
