package com.guilhermeleite.NotionCompass.controllers;

import com.guilhermeleite.NotionCompass.domains.user.User;
import com.guilhermeleite.NotionCompass.dtos.page.PageDetailsDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.WorkspaceDetailsDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.WorkspaceDetailsListDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.WorkspaceDetailsWithPagesListDto;
import com.guilhermeleite.NotionCompass.services.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
