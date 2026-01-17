package com.guilhermeleite.NotionCompass.services;

import com.guilhermeleite.NotionCompass.config.ExceptionEntityHandler;
import com.guilhermeleite.NotionCompass.config.NotionProperties;
import com.guilhermeleite.NotionCompass.domains.user.User;
import com.guilhermeleite.NotionCompass.domains.workspace.Workspace;
import com.guilhermeleite.NotionCompass.dtos.NotionWorkspaceResponseDto;
import com.guilhermeleite.NotionCompass.dtos.user.CreateUserDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.CreateWorkspaceDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class NotionService {

    private final UserService userService;
    private final WorkspaceService workspaceService;
    private final PagesService pagesService;
    private final NotionProperties notionProperties;

    private static final Logger log = LoggerFactory.getLogger(ExceptionEntityHandler.class);

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
        this.pagesService.fetchPagesByWorkspace(workspace);

        return user;
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
