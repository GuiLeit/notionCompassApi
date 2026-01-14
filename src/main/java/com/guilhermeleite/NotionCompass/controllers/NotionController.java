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

    @GetMapping("/workspaces/{workspaceNotionId}")
    public ResponseEntity<WorkspaceDetailsDto> getWorkspace(@PathVariable String workspaceNotionId) {
        WorkspaceDetailsDto workspace = this.workspaceService.getWorkspaceByNotionId(workspaceNotionId);
        return ResponseEntity.ok(workspace);
    }

    @GetMapping("/workspaces/{workspaceNotionId}/pages")
    public ResponseEntity<Map<String, Object>> getWorkspacePages(@PathVariable String workspaceNotionId) {
        Map<String, Object> pages = this.workspaceService.getWorkspacePagesByNotionId(workspaceNotionId);
        return ResponseEntity.ok(pages);
    }
}
