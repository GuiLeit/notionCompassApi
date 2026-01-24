package com.guilhermeleite.NotionCompass.services;

import com.guilhermeleite.NotionCompass.config.ExceptionEntityHandler;
import com.guilhermeleite.NotionCompass.config.NotionProperties;
import com.guilhermeleite.NotionCompass.config.exceptions.EntityNotFoundException;
import com.guilhermeleite.NotionCompass.domains.user.User;
import com.guilhermeleite.NotionCompass.domains.workspace.Workspace;
import com.guilhermeleite.NotionCompass.dtos.notion.NotionWebhookPayloadDto;
import com.guilhermeleite.NotionCompass.dtos.notion.NotionWorkspaceResponseDto;
import com.guilhermeleite.NotionCompass.dtos.user.CreateUserDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.CreateWorkspaceDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class NotionService {

    private final UserService userService;
    private final WorkspaceService workspaceService;
    private final PagesService pagesService;
    private final NotionProperties notionProperties;

    private static final Logger log = LoggerFactory.getLogger(ExceptionEntityHandler.class);
    private Map<String, Consumer<NotionWebhookPayloadDto>> webhookEventHandlers;

    @PostConstruct
    public void init() {
        this.webhookEventHandlers = new HashMap<>();
        webhookEventHandlers.put("page.created", this::handlePageCreated);
        webhookEventHandlers.put("page.properties_updated", this::handlePageUpdated);
        webhookEventHandlers.put("page.deleted", this::handlePageDeleted);
        webhookEventHandlers.put("page.moved", this::handlePageMoved);
    }

    public User handleOauthCallback(String code) {
        NotionWorkspaceResponseDto rawWorkpsace = this.workspaceService.exchangeCodeForWorkspaceData(code);
        User user = userService.findOrCreateUser(new CreateUserDto(
                rawWorkpsace.getOwner().getUser().getId(),
                rawWorkpsace.getOwner().getType()
        ));

        Workspace workspace = workspaceService.createOrUpdate(new CreateWorkspaceDto(
                rawWorkpsace.getWorkspaceId(),
                user,
                rawWorkpsace.getAccessToken(),
                rawWorkpsace.getWorkspaceName(),
                rawWorkpsace.getWorkspaceIcon()
        ));

        // TODO Create a job to fetch pages later
        this.pagesService.getPagesFromNotionApi(workspace);

        return user;
    }

    public void handleWebhookEvent(NotionWebhookPayloadDto payload) {
        String eventType = payload.getType();
        Consumer<NotionWebhookPayloadDto> handler = this.webhookEventHandlers.get(eventType);

        if (handler == null) {
            log.warn("Unhandled webhook event type: {}", eventType);
            throw new IllegalArgumentException("Unhandled webhook event type: " + eventType);
        }
        handler.accept(payload);
    }

    private void handlePageCreated(NotionWebhookPayloadDto payload) {
        Workspace workspace = this.workspaceService.findByNotionId(payload.getWorkspaceId())
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found for ID: " + payload.getWorkspaceId(), null));
        this.pagesService.getPageFromNotionApi(workspace, payload.getEntity().getId());
    }

    private void handlePageUpdated(NotionWebhookPayloadDto payload) {
        Workspace workspace = this.workspaceService.findByNotionId(payload.getWorkspaceId())
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found for ID: " + payload.getWorkspaceId(), null));
        this.pagesService.getPageFromNotionApi(workspace, payload.getEntity().getId());
    }

    private void handlePageDeleted(NotionWebhookPayloadDto payload) {
        Workspace workspace = this.workspaceService.findByNotionId(payload.getWorkspaceId())
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found for ID: " + payload.getWorkspaceId(), null));
        this.pagesService.deletePage(workspace, payload.getEntity().getId());
    }

    private void handlePageMoved(NotionWebhookPayloadDto payload) {
        Workspace workspace = this.workspaceService.findByNotionId(payload.getWorkspaceId())
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found for ID: " + payload.getWorkspaceId(), null));
        this.pagesService.getPageFromNotionApi(workspace, payload.getEntity().getId());
    }

    public boolean isWebhookSignatureValid(String webhookSignature, String requestBody) {
        try {
            String computedSignature = "sha256=" + computeHmacSHA256(requestBody, this.notionProperties.getWebhookSecret());
            return computedSignature.equals(webhookSignature);
        } catch (Exception e){
            log.error("Error while validating notion signature: {}", e.getMessage());
            return false;
        }
    }

    private String computeHmacSHA256(String data, String secret)
            throws NoSuchAlgorithmException, InvalidKeyException {

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                secret.getBytes(), "HmacSHA256"
        );
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
