package com.guilhermeleite.NotionCompass.controllers;

import com.guilhermeleite.NotionCompass.dtos.workspace.WorkspaceListResponseDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.WorkspaceResponseDto;
import com.guilhermeleite.NotionCompass.services.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notion")
@RequiredArgsConstructor
public class NotionController {

    private final WorkspaceService workspaceService;

    @GetMapping("/workspaces")
    public ResponseEntity<WorkspaceListResponseDto> getWorkspaces() {
        WorkspaceListResponseDto workspaces = this.workspaceService.findAll();
        return ResponseEntity.ok(workspaces);
    }

    @GetMapping("/workspaces/{notionId}")
    public ResponseEntity<WorkspaceResponseDto> getWorkspaceById(String notionId) {
        var workspaceOpt = this.workspaceService.findByNotionId(notionId);
        if (workspaceOpt.isPresent()) {
            var workspace = workspaceOpt.get();
            var workspaceDto = new WorkspaceResponseDto(
                    workspace.getNotionWorkspaceId(),
                    workspace.getName(),
                    workspace.getIcon()
            );
            return ResponseEntity.ok(workspaceDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
