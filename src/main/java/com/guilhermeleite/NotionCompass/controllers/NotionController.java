package com.guilhermeleite.NotionCompass.controllers;

import com.guilhermeleite.NotionCompass.domains.user.User;
import com.guilhermeleite.NotionCompass.dtos.page.PageDetailsDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.WorkspaceDetailsDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.WorkspaceDetailsListDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.WorkspaceDetailsWithPagesListDto;
import com.guilhermeleite.NotionCompass.services.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "Notion", description = "Workspace and page data for the authenticated user")
@RestController
@RequestMapping("/notion")
@RequiredArgsConstructor
public class NotionController {

    private final WorkspaceService workspaceService;

    @Operation(summary = "List workspaces", description = "Returns all workspaces belonging to the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workspaces retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/workspaces")
    public ResponseEntity<WorkspaceDetailsListDto> getWorkspaces() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        WorkspaceDetailsListDto workspaces = this.workspaceService.getWorkspacesByUserId(user.getId());
        return ResponseEntity.ok(workspaces);
    }

    @Operation(summary = "List workspaces with pages", description = "Returns all workspaces and their associated pages for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workspaces with pages retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/workspaces/pages")
    public ResponseEntity<WorkspaceDetailsWithPagesListDto> getWorkspacesWithPages() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        WorkspaceDetailsWithPagesListDto workspaces = this.workspaceService.getWorkspacesWithPagesByUserId(user.getId());
        return ResponseEntity.ok(workspaces);
    }

    @Operation(summary = "Get workspace by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workspace found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Workspace not found")
    })
    @GetMapping("/workspaces/{workspaceId}")
    public ResponseEntity<WorkspaceDetailsDto> getWorkspace(
            @Parameter(description = "Workspace ID") @PathVariable String workspaceId) {
        WorkspaceDetailsDto workspace = this.workspaceService.getWorkspaceById(workspaceId);
        return ResponseEntity.ok(workspace);
    }

    @Operation(summary = "List pages in a workspace")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pages retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Workspace not found")
    })
    @GetMapping("/workspaces/{workspaceId}/pages")
    public ResponseEntity<List<PageDetailsDto>> getWorkspacePages(
            @Parameter(description = "Workspace ID") @PathVariable String workspaceId) {
        List<PageDetailsDto> pages = this.workspaceService.getWorkspacePagesById(workspaceId);
        return ResponseEntity.ok(pages);
    }

    @Operation(summary = "Check sync status", description = "Returns whether the user's workspaces have been updated since the given timestamp")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sync status returned"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid lastSync parameter"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/workspaces/sync-status")
    public ResponseEntity<Map<String, Boolean>> areWorkspacesSynced(
            @Parameter(description = "ISO-8601 datetime of the last known sync", example = "2025-01-01T00:00:00")
            @Valid @RequestParam @NotNull(message = "lastSync must not be null") LocalDateTime lastSync
    ){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Boolean isSynced = this.workspaceService.areWorkspacesSyncedSince(user.getId(), lastSync);
        return ResponseEntity.ok(Map.of("isSynced", isSynced));
    }
}
