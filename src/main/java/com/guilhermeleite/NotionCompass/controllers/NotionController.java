package com.guilhermeleite.NotionCompass.controllers;

import com.guilhermeleite.NotionCompass.dtos.workspace.WorkspaceDetailsDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.WorkspaceDetailsListDto;
import com.guilhermeleite.NotionCompass.services.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/notion")
@RequiredArgsConstructor
public class NotionController {

    private final WorkspaceService workspaceService;

    @GetMapping("/workspaces")
    public ResponseEntity<WorkspaceDetailsListDto> getWorkspaces() {
        WorkspaceDetailsListDto workspaces = this.workspaceService.getWorkspaces();
        return ResponseEntity.ok(workspaces);
    }

    @GetMapping("/workspaces/{workspaceId}")
    public ResponseEntity<WorkspaceDetailsDto> getWorkspace(@PathVariable String workspaceId) {
        WorkspaceDetailsDto workspace = this.workspaceService.getWorkspaceById(workspaceId);
        return ResponseEntity.ok(workspace);
    }

    @GetMapping("/workspaces/{workspaceId}/pages")
    public ResponseEntity<Map<String, Object>> getWorkspacePages(@PathVariable String workspaceId) {
        Map<String, Object> pages = this.workspaceService.getWorkspacePagesById(workspaceId);
        return ResponseEntity.ok(pages);
    }
}
