package org.example.postservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "channel")
public interface CommunityClient {
    @GetMapping("/{id}/check-permission")
    boolean checkPermission(@PathVariable Long id , @RequestParam Long userId , @RequestParam String action);
}
