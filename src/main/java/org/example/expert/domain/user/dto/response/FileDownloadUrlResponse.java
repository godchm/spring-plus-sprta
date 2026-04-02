package org.example.expert.domain.user.dto.response;

import lombok.Getter;

@Getter
public class FileDownloadUrlResponse {

    private final String url;

    public FileDownloadUrlResponse(String url) {
        this.url = url;
    }
}
