package com.guilhermeleite.NotionCompass.controllers;

import com.guilhermeleite.NotionCompass.domains.user.User;
import com.guilhermeleite.NotionCompass.dtos.page.PageDetailsDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.WorkspaceDetailsDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.WorkspaceDetailsListDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.WorkspaceDetailsWithPagesListDto;
import com.guilhermeleite.NotionCompass.services.WorkspaceService;
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

@RestController
@RequestMapping("/notion")
@RequiredArgsConstructor
public class NotionController {

    private final WorkspaceService workspaceService;

    @GetMapping("/workspaces")
    public ResponseEntity<WorkspaceDetailsListDto> getWorkspaces() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        WorkspaceDetailsListDto workspaces = this.workspaceService.getWorkspacesByUserId(user.getId());
        return ResponseEntity.ok(workspaces);
    }

    @GetMapping("/workspaces/pages")
    public ResponseEntity<WorkspaceDetailsWithPagesListDto> getWorkspacesWithPages() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        WorkspaceDetailsWithPagesListDto workspaces = this.workspaceService.getWorkspacesWithPagesByUserId(user.getId());
        return ResponseEntity.ok(workspaces);
    }

    @GetMapping("/workspaces/{workspaceId}")
    public ResponseEntity<WorkspaceDetailsDto> getWorkspace(@PathVariable String workspaceId) {
        WorkspaceDetailsDto workspace = this.workspaceService.getWorkspaceById(workspaceId);
        return ResponseEntity.ok(workspace);
    }

    @GetMapping("/workspaces/{workspaceId}/pages")
    public ResponseEntity<List<PageDetailsDto>> getWorkspacePages(@PathVariable String workspaceId) {
        List<PageDetailsDto> pages = this.workspaceService.getWorkspacePagesById(workspaceId);
        return ResponseEntity.ok(pages);
    }

    @GetMapping("/workspaces/sync-status")
    public ResponseEntity<Map<String, Boolean>> areWorkspacesSynced(
            @Valid @RequestParam @NotNull(message = "lastSync must not be null") LocalDateTime lastSync
    ){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Boolean isSynced = this.workspaceService.areWorkspacesSyncedSince(user.getId(), lastSync);
        return ResponseEntity.ok(Map.of("isSynced", isSynced));
    }
}
